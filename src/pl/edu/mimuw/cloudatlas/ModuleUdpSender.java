package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleUdpSender extends Module {
    public static int MSG_SEND_MESSAGE = 1;

    public static String name = "udpsender";
    private static ModuleUdpSender instance = new ModuleUdpSender();
    
    Thread timerThread;
    
    public static ModuleUdpSender getInstance(){
        return instance;
    }
    ModuleUdpSender() {
        super(name);
    }

    @Override
    public void receiveMessage(Message m) {
        if(m.messageType == MSG_SEND_MESSAGE){
            MessageOverNetwork tm = (MessageOverNetwork) m;
            Kryo kryo = ex.kryo;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Output output = new Output(stream);
            kryo.writeClassAndObject(output, tm.m);
            output.close();
            byte[] res = stream.toByteArray();
            debug(" sending " + res.length + " bytes");
            try {
                InetAddress address = InetAddress.getByName(tm.host);
                DatagramPacket packet = new DatagramPacket(res, res.length, address, tm.port);
                DatagramSocket dsocket = new DatagramSocket();
                dsocket.send(packet);
                dsocket.close();
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
