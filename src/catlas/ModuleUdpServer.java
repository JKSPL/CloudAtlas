package catlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleUdpServer extends Module implements Runnable {
    static String name = "udpserver";
    static ModuleUdpServer instance = new ModuleUdpServer();
    DatagramSocket serverSocket;
    byte[] receiveData = new byte[1500];
    public void init(Executor tex, int port) throws SocketException {
        super.init(tex);
        serverSocket = new DatagramSocket(port);
        debug("listening on port: " + Integer.toString(port));
    }

    public static ModuleUdpServer getInstance(){
        return instance;
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
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(receiveData);
                    Input input = new Input (inputStream);
                    Kryo kryo = ex.kryo;
                    Message m = (Message)kryo.readClassAndObject(input);
                    debug(Integer.toString(receivePacket.getLength()));
                    debug(m.getSender().name);
                    debug(m.getRecipient().name);
                    Module.sendMessage(m);
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
