package catlas;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.PriorityQueue;

/**
 * Created by julek on 29-Dec-16.
 */
public class ModuleCommunication extends Module {
    static String name = "communication";
    static ModuleCommunication instance = new ModuleCommunication();
    Thread serverThread;
    public void init(int port) throws SocketException {
        super.init();
        ModuleUdpServer.getInstance().init(port);
        ModuleUdpSender.getInstance().init();
        serverThread = new Thread(ModuleUdpServer.getInstance());
        serverThread.start();
    }
    
    public static ModuleCommunication getInstance(){
        return instance;
    }
    
    ModuleCommunication() {
        super(name);
    }
}
