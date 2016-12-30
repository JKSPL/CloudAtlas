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
    public void init(Executor tex, int port) throws SocketException {
        ModuleUdpServer.getInstance().init(Main.nextExecutor(), port);
        ModuleUdpSender.getInstance().init(Main.nextExecutor());
        serverThread = new Thread(ModuleUdpServer.getInstance());
        serverThread.start();
        super.init(tex);
    }
    
    public static ModuleCommunication getInstance(){
        return instance;
    }
    
    ModuleCommunication() {
        super(name);
    }
}
