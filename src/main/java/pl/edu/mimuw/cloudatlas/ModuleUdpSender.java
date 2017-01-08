package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleUdpSender extends Module {
    public static int MSG_SEND_MESSAGE = 1;

    public static String name = "udpsender";
    private static ModuleUdpSender instance = new ModuleUdpSender();
    DatagramSocket dsocket;
    Thread timerThread;

    public static ModuleUdpSender getInstance(){
        return instance;
    }
    ModuleUdpSender() {
        super(name);
    }

    public void init(){
        super.init();
        try {
            dsocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(Message m) {
        if(m.messageType == MSG_SEND_MESSAGE){
            MessageOverNetwork tm = (MessageOverNetwork) m;
            String host = tm.host;
            int port = tm.port;
            tm.host = Util.p.getProperty("myaddress");
            tm.port = Integer.parseInt(Util.p.getProperty("server_port"));
            tm.m.stamp();
            byte [] out = tm.m.serialize(ex.kryo);
            List<MessageBlob> l = MessageBlob.divideIntoBlobs(out);
            try {
                debug("Sending to: " + host);
                debug("Port: " + Integer.toString(port));
                InetAddress address = InetAddress.getByName(host);
                for(MessageBlob mblob: l){
                    byte[] out2 = mblob.serialize(ex.kryo);
                    debug(" sending " + out2.length + " bytes");
                    DatagramPacket packet = new DatagramPacket(out2, out2.length, address, port);
                    dsocket.send(packet);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
    }
}
