package pl.edu.mimuw.cloudatlas;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import pl.edu.mimuw.cloudatlas.model.*;
import spark.Spark;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.management.Query;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static pl.edu.mimuw.cloudatlas.interpreter.Main.executeQueries;

/**
 * Created by jks on 1/4/17.
 */
public class ModuleAgent extends Module implements AgentInterface {
    static int MSG_SYSTEM_DATA = 1;
    static int MSG_COMPUTE_QUERIES = 2;
    static int MSG_TIMESTAMPS = 3;
    static int MSG_QUERY = 4;
    static int MSG_ATTRIBUTES = 5;
    static int MSG_GOSSIP = 6;
    static String name = "agent";
    static ModuleAgent instance = new ModuleAgent();
    protected ModuleAgent() {
        super(name);
    }
    public static ModuleAgent getInstance(){
        return instance;
    }
    ZMI root = null;
    ZMI myLeaf = null;
    List<ZMI> myZones;

    HashSet<PathName> myPaths;
    HashSet<PathName> myZMIs = new HashSet<>();
    HashMap<PathName, ZMI> path2ZMI = new HashMap<>();

    HashMap<QueryInfo, Date> installedQueries = new HashMap<>();
    HashMap<QueryInfo, Date> revokedQueries = new HashMap<>();

    PathName myPath;
    String myAddress;
    int myPort;
    ValueContact myContact;
    Set<ValueContact> fallbackContacts;
    int computeQueriesPeriod;
    ZMIPicker picker;
    PublicKey publicKey = null;
    int gossipPeriod;



    void restoreMyInfo(){
        if(root == null){
            root = new ZMI();
            root.getAttributes().add("name", new ValueString(null));
            root.getAttributes().add("contacts", new ValueSet(TypePrimitive.CONTACT));
        }

        ZMI cur = root;
        addZone(myPath);
        myZones.clear();
        myZones.add(cur);
        for(String s: myPath.getComponents()){
            List<ZMI> l = cur.getSons();
            ZMI nxt = null;
            for(ZMI z: l){
                if(((ValueString)z.getAttributes().get("name")).toString().equals(s)){
                    nxt = z;
                } else if (!((ValueSet)z.getAttributes().get("contacts")).isEmpty()){
                    picker.put(new PathName(getPath(z)));
                }
            }
            assert(nxt != null);
            cur = nxt;
            myZones.add(cur);
        }
        myLeaf = cur;
        ValueSet leafContacts = ((ValueSet)myLeaf.getAttributes().get("contacts"));
        if(!leafContacts.contains(myContact)){
            leafContacts.add(myContact);
        }

        myZMIs.clear();
        restoreMyZMIs(root);

        path2ZMI.clear();
        restorePath2ZMI(root);
    }

    void restoreMyZMIs(ZMI z){
        if(z == null){
            return;
        }
        myZMIs.add(new PathName(getPath(z)));
        for(ZMI s: z.getSons()){
            restoreMyZMIs(s);
        }
    }


    void restorePath2ZMI(ZMI z){
        if(z == null){
            return;
        }
        path2ZMI.put(new PathName(getPath(z)), z);
        for(ZMI s: z.getSons()){
            restorePath2ZMI(s);
        }
    }

    void addZone(PathName p){
        ZMI cur = root;
        for(String s: p.getComponents()){
            List<ZMI> l = cur.getSons();
            ZMI nxt = null;
            for(ZMI z: l){
                if(((ValueString)z.getAttributes().get("name")).toString().equals(s)){
                    nxt = z;
                    break;
                }
            }
            if(nxt == null){
                nxt = new ZMI();
                nxt.getAttributes().add("name", new ValueString(s));
                nxt.getAttributes().add("contacts", new ValueSet(TypePrimitive.CONTACT));
                cur.addSon(nxt);
                nxt.setFather(cur);
                path2ZMI.put(new PathName(getPath(nxt)), nxt);
            }
            cur = nxt;
            myZones.add(cur);
        }
    }

    static boolean isInteresting(PathName l, PathName r){
        //r interesting for stamps
        int i = 0;
        while(l.getComponents().size() > i
                && r.getComponents().size() > i
                && l.getComponents().get(i).equals(r.getComponents().get(i))){
            i++;
        }
        return i >= r.getComponents().size() - 1;
    }

    void gatherInterestingZMI(PathName l, HashSet<ZMI> hmap){
        int i = 0;
        int sz = l.getComponents().size();
        ZMI cur = root;
        for(String s: l.getComponents()){
            List<ZMI> sons = cur.getSons();
            ZMI nxt = null;
            for(ZMI z: sons){
                if(((ValueString)z.getAttributes().get("name")).toString().equals(s)){
                    nxt = z;
                    break;
                } else{
                    hmap.add(z);
                }
            }
            if(nxt == null){
                break;
            }
            cur = nxt;
        }
    }

    HashMap<PathName, Date> gatherInterestingTimeStamps(PathName path){
        HashMap<PathName, Date> res = new HashMap<>();
        HashSet<ZMI> r1 = new HashSet<>();
        gatherInterestingZMI(path, r1);
        for(ZMI z: r1){
            res.put(new PathName(getPath(z)), z.timestamp);
        }
        return res;
    }

    HashMap<PathName, AttributesMap> gatherInterestingAttributes(PathName path){
        HashMap<PathName, AttributesMap> res = new HashMap<>();
        HashSet<ZMI> r1 = new HashSet<>();
        gatherInterestingZMI(path, r1);
        for(ZMI z: r1){
            res.put(new PathName(getPath(z)), z.getAttributes().clone());
        }
        return res;
    }

    void readPublicKey(){
        byte[] bpu = null;
        try {
            bpu = Files.readAllBytes(Paths.get("config.publickey"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic (new X509EncodedKeySpec(bpu));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    boolean verify(QueryInfo q, byte[] sign){
        MessageDigest digestGenerator =
                null;
        try {
            digestGenerator = MessageDigest.getInstance("SHA-1");
            byte[] digest = digestGenerator.digest(q.serialize());
            System.out.println(new String(Base64.getEncoder().encode(digest)));
            Cipher verifyCipher = Cipher.getInstance("RSA");
            verifyCipher.init(Cipher.DECRYPT_MODE, publicKey);
            return Arrays.equals(digest, verifyCipher.doFinal(sign));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return false;
    }

    void setPicker(){
        String mode = Util.p.getProperty("gossipstrat");
        if(mode.equals("robin")){
            picker = new ZMIPickerRoundRobin();
        } else if(mode.equals("robinexp")){
            picker = new ZMIPickerRoundRobinExp();
        } else if(mode.equals("random")){
            picker = new ZMIPickerRandom();
        } else if(mode.equals("randomexp")){
            picker = new ZMIPickerRandomExp();
        }
    }

    public void init()
    {
        super.init();
        readPublicKey();
        setPicker();

        fallbackContacts = new HashSet<>();
        myZones = new ArrayList<>();
        myPath = new PathName(Util.p.getProperty("myname"));
        myAddress = Util.p.getProperty("myaddress");
        myPort = Integer.parseInt(Util.p.getProperty("server_port"));
        computeQueriesPeriod = Integer.parseInt(Util.p.getProperty("computequeriesperiod"));
        myContact = new ValueContact(myPath, new InetSocketAddress(myAddress, myPort));
        //fallbackContacts.add(myContact);
        gossipPeriod = Integer.parseInt(Util.p.getProperty("gossipperiod"));

        restoreMyInfo();

        Message m = new Message(getInstance(), getInstance(), MSG_COMPUTE_QUERIES);
        Message tm = new MessageCallback(getInstance(), ModuleTimer.getInstance(), ModuleTimer.MSG_CALLBACK_PERIODIC, new CallbackSendMessage(m), computeQueriesPeriod);
        Module.sendMessage(tm);

        m = new Message(getInstance(), getInstance(), MSG_GOSSIP);
        tm = new MessageCallback(getInstance(), ModuleTimer.getInstance(), ModuleTimer.MSG_CALLBACK_PERIODIC, new CallbackSendMessage(m), gossipPeriod);
        Module.sendMessage(tm);

        try {
            AgentInterface stub = (AgentInterface) UnicastRemoteObject.exportObject(instance, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("AgentInterface", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("AgentInterface bound");
    }

    String getPath(ZMI z){
        if(z == root){
            return "/";
        } else{
            return (z.getFather() == root?"":getPath(z.getFather())) + "/" + z.getAttributes().get("name");
        }
    }

    boolean executeQuery(String s){
        ZMI copy = root.clone();
        try {
            executeQueries(copy, s);
            root = copy;
            restoreMyInfo();
            return true;
        } catch (Exception e) {
            debug("Query: " + s + " failed");
            return false;
        }
    }

    void sendToContact(Message m, ValueContact c){
        MessageOverNetwork msg = new MessageOverNetwork(m, c.getAddress().getHostName(), c.getAddress().getPort(), ModuleUdpSender.MSG_SEND_MESSAGE);
        //Message.deserialize(ex.kryo, msg.serialize(ex.kryo));
        if(m instanceof MessageZMIAttributes){
            MessageZMIAttributes m1 = (MessageZMIAttributes )m;
            for(Map.Entry<PathName, AttributesMap> e : m1.zmiAttrs.entrySet()){
                assert(e.getValue() != null);
            }
        }
        Module.sendMessage(msg);
    }

    void collectNewData(MessageZMIAttributes msg){
        for(Map.Entry<QueryInfo, Date> e : msg.installedQueries.entrySet()){
            if(!installedQueries.containsKey(e.getKey()) || installedQueries.get(e.getKey()).before(e.getValue())){
                installedQueries.put(e.getKey(), e.getValue());
            }
        }
        for(Map.Entry<QueryInfo, Date> e : msg.revokedQueries.entrySet()){
            if(!revokedQueries.containsKey(e.getKey()) || revokedQueries.get(e.getKey()).before(e.getValue())){
                revokedQueries.put(e.getKey(), e.getValue());
            }
        }
        for(Map.Entry<PathName, AttributesMap> e : msg.zmiAttrs.entrySet()){
            if(!path2ZMI.containsKey(e.getKey())){
                addZone(e.getKey());
            }
            ZMI t = path2ZMI.get(e.getKey());
            Date stamp = t.timestamp;
            Date stamp2 = msg.zmiStamps.get(e.getKey());
            if(stamp.before(stamp2)){
                t.timestamp = stamp2;
                for(Map.Entry<Attribute, Value> e1: e.getValue()){
                    t.getAttributes().addOrChange(e1.getKey(), e1.getValue());
                }
            }
        }
    }

    void purgeOld(ZMI z, Date d){
        if(d.getTime() - z.timestamp.getTime() > 60 * 1000){
            z.purge();
        }
        for(ZMI t : z.getSons()){
            purgeOld(t, d);
        }
    }

    @Override
    synchronized public void receiveMessage(Message m) {
        if(m.messageType == MSG_SYSTEM_DATA){
            MessageZMISystemInfo tm = (MessageZMISystemInfo) m;
            Set<Map.Entry<Attribute, Value>> s = tm.zmiAttrs.entrySet();
            for(Map.Entry<Attribute, Value> entry : s){
                myLeaf.getAttributes().addOrChange(entry);
            }
            myLeaf.pokeTimeStamp();
        } else if(m.messageType == MSG_COMPUTE_QUERIES){
            purgeOld(root, new Date());
            for(ZMI z: myZones){
                if(z != myLeaf){
                    z.purge();
                }
            }

            executeQuery("SELECT to_set(random(3, unfold(contacts))) AS contacts");
            ArrayList<QueryInfo> toRemove = new ArrayList<>();
            for(Map.Entry<QueryInfo, Date> entry : installedQueries.entrySet()){
                if(!revokedQueries.containsKey(entry.getKey())
                        || revokedQueries.get(entry.getKey()).before(entry.getValue())){
                    debug("Executing: " + entry.getKey().query);
                    executeQuery(entry.getKey().query);
                }
            }
            for(ZMI z : myZones){
                z.pokeTimeStamp();
            }
        } else if(m.messageType == MSG_QUERY){
            MessageQuery tm = (MessageQuery) m;
            QueryInfo info = tm.info;
            byte[] sign = tm.sign;
            if(verify(info, sign)){
                if(tm.install){
                    installedQueries.put(info, new Date());
                } else{
                    revokedQueries.put(info, new Date());
                }
            }
        } else if(m.messageType == MSG_TIMESTAMPS){
            MessageZMITimestamps msg = (MessageZMITimestamps)m;
            updateMessageForGTP(msg);
            if(msg.noreply){
                MessageZMIAttributes myMsg = getGossipAttributes(msg.sender.getName(), msg);
                myMsg.stamps = msg.stamps;
                sendToContact(myMsg, msg.sender);
            } else{
                MessageZMITimestamps myMsg = getGossipTimestamps(msg.sender.getName());
                myMsg.stamps = msg.stamps;
                myMsg.noreply = true;
                sendToContact(myMsg, msg.sender);
            }
        } else if(m.messageType == MSG_ATTRIBUTES){
            MessageZMIAttributes msg = (MessageZMIAttributes)m;
            updateMessageForGTP(msg);
            if(!msg.noreply){
                MessageZMIAttributes myMsg = getGossipAttributes(msg.sender.getName(), msg);
                myMsg.stamps = msg.stamps;
                myMsg.noreply = true;
                sendToContact(myMsg, msg.sender);
            }
            collectNewData(msg);
        } else if(m.messageType == MSG_GOSSIP){
            PathName chosen = picker.getNext();
            ValueContact contact = null;
            if(chosen == null){
                if(fallbackContacts.isEmpty()){
                    return;
                } else{
                    int r = ThreadLocalRandom.current().nextInt(fallbackContacts.size());
                    int i = 0;
                    for(ValueContact c: fallbackContacts){
                        if(i >= r){
                            contact = (ValueContact)c;
                            break;
                        }
                        i++;
                    }
                }
            } else{
                try{
                    ValueSet s = (ValueSet) path2ZMI.get(chosen).getAttributes().get("contacts");
                    int r = ThreadLocalRandom.current().nextInt(s.size());
                    int i = 0;
                    for(Value c: s){
                        if(i >= r){
                            contact = (ValueContact)c;
                            break;
                        }
                        i++;
                    }
                } catch(Exception e){
                    
                }
            }
            if(contact != null){
                MessageZMITimestamps myMsg = getGossipTimestamps(contact.getName());
                sendToContact(myMsg, contact);
            } else{
                debug("didn't contact anyone :(");
            }
        }
        //System.out.println(picker.getNext());
        //addZone(new PathName("/army1/division1/regiment2"));
//        root.printAttributes(System.out);
    }

    long getOffset(Message GTPSource){
        long t1a, t1b, t2b, t2a;
        t1a = GTPSource.stamps.get(0).getTime();
        t1b = GTPSource.stamps.get(1).getTime();
        t2b = GTPSource.stamps.get(2).getTime();
        t2a = GTPSource.stamps.get(3).getTime();

        long rtd = (t2a- t1a) - (t2b - t1b);

        long dT = t2b + rtd / 2 - t2a;
        return dT;
    }

    void updateMessageForGTP(MessageZMITimestamps msg){
        if(msg.stamps.size() < 4){
            return;
        }

        long dT = getOffset(msg);

        HashMap<PathName, Date> nmap = new HashMap<>();
        for(Map.Entry<PathName, Date> e: msg.zmiMap.entrySet()){
            nmap.put(e.getKey(), new Date(e.getValue().getTime() + dT));
        }

        msg.zmiMap = nmap;

        HashMap<String, Date> qmap = new HashMap<>();
        for(Map.Entry<String, Date> e: msg.queryMap.entrySet()){
            qmap.put(e.getKey(), new Date(e.getValue().getTime() + dT));
        }
        msg.queryMap = qmap;
    }

    void updateMessageForGTP(MessageZMIAttributes msg){
        if(msg.stamps.size() < 4){
            return;
        }
        long dT = getOffset(msg);
        HashMap<PathName, Date> nmap = new HashMap<>();
        for(Map.Entry<PathName, Date> e: msg.zmiStamps.entrySet()){
            nmap.put(e.getKey(), new Date(e.getValue().getTime() + dT));
        }

        msg.zmiStamps = nmap;

        HashMap<QueryInfo, Date> qmap1 = new HashMap<>();
        for(Map.Entry<QueryInfo, Date> e: msg.installedQueries.entrySet()){
            qmap1.put(e.getKey(), new Date(e.getValue().getTime() + dT));
        }
        msg.installedQueries = qmap1;

        HashMap<QueryInfo, Date> qmap2 = new HashMap<>();
        for(Map.Entry<QueryInfo, Date> e: msg.revokedQueries.entrySet()){
            qmap2.put(e.getKey(), new Date(e.getValue().getTime() + dT));
        }
        msg.revokedQueries = qmap2;
    }

   synchronized MessageZMITimestamps getGossipTimestamps(PathName p){
        HashMap<PathName, Date> res = gatherInterestingTimeStamps(p);;
        debug("for " + p.toString());
        debug("following interesting:");
        for(Map.Entry<PathName, Date> r : res.entrySet()){
            debug(r.getKey().toString());
        }
        MessageZMITimestamps msg = new MessageZMITimestamps(getInstance(), getInstance(), MSG_TIMESTAMPS, myContact);
        msg.zmiMap = res;
        for(Map.Entry<QueryInfo, Date> e: installedQueries.entrySet()){
            msg.queryMap.put(e.getKey().name, e.getValue());
        }
        for(Map.Entry<QueryInfo, Date> e: revokedQueries.entrySet()){
            System.out.println(msg.queryMap == null);
            if(!msg.queryMap.containsKey(e.getKey().name) || msg.queryMap.get(e.getKey().name).before(e.getValue())){
                msg.queryMap.put(e.getKey().name, e.getValue());
            }
        }
        return msg;
    }



    synchronized MessageZMIAttributes getGossipAttributes(PathName p, MessageZMITimestamps m){
        return getGossipAttributes(p, m.zmiMap, m.queryMap);
    }

    synchronized MessageZMIAttributes getGossipAttributes(PathName p, MessageZMIAttributes m){
        HashMap<String, Date> queryMap = new HashMap<>();
        for(Map.Entry<QueryInfo, Date> e : m.installedQueries.entrySet()){
            queryMap.put(e.getKey().name, e.getValue());
        }
        for(Map.Entry<QueryInfo, Date> e : m.revokedQueries.entrySet()){
            if(!queryMap.containsKey(e.getKey().name) || queryMap.get(e.getKey().name).before(e.getValue())){
                queryMap.put(e.getKey().name, e.getValue());
            }
        }
        return getGossipAttributes(p, m.zmiStamps, queryMap);
    }


    synchronized MessageZMIAttributes getGossipAttributes(PathName p, Map<PathName, Date> zmiMap, Map<String, Date> queryMap){
        HashMap<PathName, AttributesMap> res = gatherInterestingAttributes(p);
        MessageZMIAttributes msg = new MessageZMIAttributes(getInstance(), getInstance(), MSG_ATTRIBUTES, myContact);
        for(Map.Entry<QueryInfo, Date> e: installedQueries.entrySet()){
            Date stamp = e.getValue();
            if(!queryMap.containsKey(e.getKey().name) || queryMap.get(e.getKey().name).before(stamp)){
                msg.installedQueries.put(e.getKey(), e.getValue());
            }
        }
        for(Map.Entry<QueryInfo, Date> e: revokedQueries.entrySet()){
            Date stamp = e.getValue();
            if(!queryMap.containsKey(e.getKey().name) || queryMap.get(e.getKey().name).before(stamp)){
                msg.revokedQueries.put(e.getKey(), e.getValue());
            }
        }
        for(Map.Entry<PathName, AttributesMap> e: res.entrySet()){
            Date stamp = path2ZMI.get(e.getKey()).timestamp;
            if(!zmiMap.containsKey(e.getKey()) || zmiMap.get(e.getKey()).before(stamp)){
                msg.zmiAttrs.put(e.getKey(), e.getValue());
                msg.zmiStamps.put(e.getKey(), stamp);
            }
        }
        Message.deserialize(ex.kryo, msg.serialize(ex.kryo));
        return msg;
    }


    synchronized public void setFallbackContacts(Set<ValueContact> s) throws RemoteException {
        fallbackContacts = s;
    }

    synchronized public Set<PathName> getAvailableZones() throws RemoteException {
        return myZMIs;
    }

    synchronized public AttributesMap getAttributesOfZone(PathName p) throws RemoteException {
        return path2ZMI.get(p).getAttributes();
    }

    synchronized public void setAttribute(PathName p, Attribute attr, Value val) throws RemoteException {
        path2ZMI.get(p).getAttributes().addOrChange(attr, val);
    }

    synchronized public boolean addQuery(String name, String query, String sign){
        QueryInfo q = new QueryInfo(name, query);
        if(!verify(q, Base64.getDecoder().decode(sign))){
            return false;
        };
        installedQueries.put(q, new Date());
        return true;
    }

    synchronized public boolean removeQuery(String name, String query, String sign){
        QueryInfo q = new QueryInfo(name, query);
        if(!verify(q, Base64.getDecoder().decode(sign))){
            return false;
        };
        revokedQueries.put(q, new Date());
        return true;
    }

    synchronized void getAllPaths(ZMI z, JSONArray arr){
        arr.put(getPath(z));
        for(ZMI t : z.getSons()){
            getAllPaths(t, arr);
        }
    }

    synchronized String getZMIData(String name){
        PathName p = new PathName(name);
        if(!path2ZMI.containsKey(p)){
            return "";
        }
        JSONObject obj = new JSONObject();
        ZMI z = path2ZMI.get(p);
        for(Map.Entry<Attribute, Value> e : z.getAttributes()){
            if(e.getValue() instanceof ValueInt){
                obj.put(e.getKey().getName(), ((ValueInt) e.getValue()).getValue());
            } else if(e.getValue() instanceof ValueDouble){
                obj.put(e.getKey().getName(), ((ValueDouble) e.getValue()).getValue());
            } else{
                obj.put(e.getKey().getName(), e.getValue().toString());
            }

        }
        return obj.toString();
    }

    synchronized String getQueries(){
        JSONObject obj = new JSONObject();
        for(Map.Entry<QueryInfo, Date> entry : installedQueries.entrySet()){
            if(!revokedQueries.containsKey(entry.getKey())
                    || revokedQueries.get(entry.getKey()).before(entry.getValue())){
                obj.put(entry.getKey().name, entry.getKey().query);
            }
        }
        return obj.toString();
    }

    synchronized String addContact(String path, String ip, String port){
        try{
            int s3 = Integer.parseInt(port);
            ValueContact c = new ValueContact(new PathName(path), new InetSocketAddress(ip, s3));
            fallbackContacts.add(c);
            return "OK";
        } catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public void start(){
        Spark.post("/install/", (request, response) -> {
            String name = request.queryParams("name");
            String query = request.queryParams("query");
            String base64sign = request.queryParams("sign");
            debug(name + ": " + query);
            debug("sign: " + base64sign);
            if(addQuery(name, query, base64sign)){
                return "OK";
            } else{
                return "";
            }
        });
        Spark.post("/uninstall/", (request, response) -> {
            String name = request.queryParams("name");
            String query = request.queryParams("query");
            String base64sign = request.queryParams("sign");
            if(removeQuery(name, query, base64sign)){
                return "OK";
            } else{
                return "";
            }
        });
        Spark.get("/zmis/", (request, response) -> {
            JSONArray arr = new JSONArray();
            getAllPaths(root, arr);
            return arr.toString();
        });
        Spark.post("/getzmidata/", (request, response) -> {
            String path = request.queryParams("path");
            return getZMIData(path);
        });
        Spark.get("/getqueries/", (request, response) -> {
            return getQueries();
        });
        Spark.post("/addcontact/", (request, response) -> {
            String path = request.queryParams("path");
            String ip = request.queryParams("ip");
            String port = request.queryParams("port");
            return addContact(path, ip, port);
        });
    }

}
