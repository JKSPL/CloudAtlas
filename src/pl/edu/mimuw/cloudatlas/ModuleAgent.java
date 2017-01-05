package pl.edu.mimuw.cloudatlas;

import org.w3c.dom.Attr;
import pl.edu.mimuw.cloudatlas.Module;
import pl.edu.mimuw.cloudatlas.model.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.jar.Attributes;

import static pl.edu.mimuw.cloudatlas.interpreter.Main.executeQueries;

/**
 * Created by jks on 1/4/17.
 */
public class ModuleAgent extends Module implements AgentInterface {
    static int MSG_SYSTEM_DATA = 1;
    static int MSG_COMPUTE_QUERIES = 2;
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

    PathName myPath;
    String myAddress;
    ValueContact myContact;
    Set<ValueContact> fallbackContacts;
    int computeQueriesPeriod;
    ZMIPicker picker;

    void restoreMyInfo(){
        if(root == null){
            root = new ZMI();
            root.getAttributes().add("name", new ValueString(null));
        }

        ZMI cur = root;
        myZones.clear();
        myZones.add(cur);
        for(String s: myPath.getComponents()){
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
            }
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

    public void init()
    {
        super.init();

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

        fallbackContacts = new HashSet<>();
        myZones = new ArrayList<>();
        myPath = new PathName(Util.p.getProperty("myname"));
        myAddress = Util.p.getProperty("myaddress");
        computeQueriesPeriod = Integer.parseInt(Util.p.getProperty("computequeriesperiod"));
        try {
            myContact = new ValueContact(myPath, InetAddress.getByName(myAddress));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        for(String s : myPath.getComponents()){
            debug(s);
        }
        restoreMyInfo();

        Message m = new Message(getInstance(), getInstance(), MSG_COMPUTE_QUERIES);
        Message tm = new MessageCallback(getInstance(), ModuleTimer.getInstance(), ModuleTimer.MSG_CALLBACK_PERIODIC, new CallbackSendMessage(m), computeQueriesPeriod);
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
            e.printStackTrace();
            return false;
        }
    }

    @Override
    synchronized public void receiveMessage(Message m) {
        if(m.messageType == MSG_SYSTEM_DATA){
            MessageAttributes tm = (MessageAttributes) m;
            Set<Map.Entry<Attribute, Value>> s = tm.hm.entrySet();
            for(Map.Entry<Attribute, Value> entry : s){
                myLeaf.getAttributes().addOrChange(entry);
            }
            myLeaf.pokeTimeStamp();
        } else if(m.messageType == MSG_COMPUTE_QUERIES){
            executeQuery("SELECT random(3, unfold(contacts)) AS contacts");
            for(ZMI z : myZones){
                z.pokeTimeStamp();
            }
        }
    }

    @Override
    synchronized public void setFallbackContacts(Set<ValueContact> s) throws RemoteException {
        fallbackContacts = s;
    }

    @Override
    synchronized public Set<PathName> getAvailableZones() throws RemoteException {
        return myZMIs;
    }

    @Override
    synchronized public AttributesMap getAttributesOfZone(PathName p) throws RemoteException {
        return path2ZMI.get(p).getAttributes();
    }

    @Override
    synchronized public void setAttribute(PathName p, Attribute attr, Value val) throws RemoteException {
        path2ZMI.get(p).getAttributes().addOrChange(attr, val);
    }
}
