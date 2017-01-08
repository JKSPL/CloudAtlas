package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import java.io.ByteArrayInputStream;
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
    public void init(int port) throws SocketException {
        super.init();
        this.port = port;
        serverSocket = new DatagramSocket(port);
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
        try{
            while(true){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                    debug("gotcha");
                    Date d = new Date();
                    if(receivePacket.getLength() > 500){
                        continue;
                    }
                    MessageBlob m = MessageBlob.deserialize(ex.kryo, receivePacket.getData());
                    if(!buffer.containsKey(m.id)){
                        buffer.put(m.id, new TreeSet<>(Comparator.comparingInt(o -> o.part)));
                    }
                    buffer.get(m.id).add(m);
                    Message msg = null;
                    debug(Integer.toString(m.part) + "/" + Integer.toString(m.parts));
                    if(buffer.get(m.id).size() == m.parts){
                        debug("lololol");
                        msg = MessageBlob.combine(buffer.get(m.id), ex.kryo);
                        buffer.remove(m.id);
                        msg.stamp(d);
                        Module.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    wait(500);
                    continue;
                }
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
