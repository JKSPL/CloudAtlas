package pl.edu.mimuw.cloudatlas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

/**
 * Created by julek on 30-Dec-16.
 */
public class  ModuleUdpServer extends Module implements Runnable {
    static int MSG_DESTROY = 1;
    static String name = "udpserver";
    static ModuleUdpServer instance = new ModuleUdpServer();
    DatagramSocket serverSocket;
    byte[] receiveData = new byte[1500];
    HashMap<Integer, Set<MessageBlob>> buffer = new HashMap<>();
    Thread serverThread;
    int port;
    int destroyPeriod;
    public void init(int port) throws SocketException {
        super.init();
        this.port = port;
        serverSocket = new DatagramSocket(port);
        destroyPeriod = Integer.parseInt(Util.p.getProperty("destroyperiod", "1000"));
    }
    public static ModuleUdpServer getInstance(){
        return instance;
    }

    public void start(){
        serverThread = new Thread(ModuleUdpServer.getInstance());
        serverThread.start();
        debug("listening on port: " + Integer.toString(port));
    }

    ModuleUdpServer() {
        super(name);
    }

    @Override
    public void run() {
            while(true){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                    synchronized (this){
                        Date d = new Date();
                        if(receivePacket.getLength() > 500){
                            continue;
                        }
                        MessageBlob m = MessageBlob.deserialize(ex.kryo, receivePacket.getData());
                        if(!buffer.containsKey(m.id)){
                            buffer.put(m.id, new TreeSet<>(Comparator.comparingInt(o -> o.part)));
                            MessageInt mint = new MessageInt(getInstance(), getInstance(), MSG_DESTROY, m.id);
                            Message tm = new MessageCallback(getInstance(), ModuleTimer.getInstance(), ModuleTimer.MSG_CALLBACK_ONCE, new CallbackSendMessage(mint), destroyPeriod);
                        }
                        buffer.get(m.id).add(m);
                        Message msg = null;
                        //debug(m.id + ": " + Integer.toString(m.part) + "/" + Integer.toString(m.parts));
                        if(buffer.get(m.id).size() == m.parts){
                            msg = MessageBlob.combine(buffer.get(m.id), ex.kryo);
                            buffer.remove(m.id);
                            msg.stamp(d);
                            Module.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
    }
    @Override
    synchronized public void receiveMessage(Message m){
        if(m.messageType == MSG_DESTROY){
            MessageInt msg = (MessageInt)m;
            buffer.remove(id);
        }
    }
}
