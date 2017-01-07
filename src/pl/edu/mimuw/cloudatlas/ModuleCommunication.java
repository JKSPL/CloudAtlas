package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.ValueContact;

import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by julek on 29-Dec-16.
 */
public class ModuleCommunication extends Module {
    static String name = "communication";
    static ModuleCommunication instance = new ModuleCommunication();
    public void init(int port) throws SocketException {
        super.init();
        ModuleUdpServer.getInstance().init(port);
        ModuleUdpSender.getInstance().init();
    }
    
    public static ModuleCommunication getInstance(){
        return instance;
    }
    
    ModuleCommunication() {
        super(name);
    }
}
