package pl.edu.mimuw.cloudatlas;

import java.io.File;

import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;
import pl.edu.mimuw.cloudatlas.model.*;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleSystemInfo extends Module {
    static int MSG_UPDATE_INFO = 1;
    static String name = "systeminfo";
    static ModuleSystemInfo instance = new ModuleSystemInfo();
    OperatingSystemMXBean osMXBean;
    double cpu_load;
    long free_disk;
    long total_disk;
    long free_ram;
    long total_ram;
    long free_swap;
    long total_swap;
    long num_processes;
    long num_cores;
    String kernel_ver;
    long logged_users;
    String[] dns_names;
    
    ArrayDeque<Double> loads = new ArrayDeque<>();
    int maxDequeMembers;
    Double cpu_load_sum = 0.0;
    int systemUpdatePeriod;
    protected ModuleSystemInfo(){
        super(name);
    }
    Registry registry;
    SystemInfo stub = null;


    public void init(){
        super.init();
        try {
            registry = LocateRegistry.getRegistry(Util.p.getProperty("agentserver", "localhost"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            stub = (SystemInfo) registry.lookup("SystemInfo");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        osMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        maxDequeMembers = Integer.max(1, Integer.parseInt(Util.p.getProperty("cpuloadgatherunits", "4")));
        systemUpdatePeriod = Integer.max(100, Integer.parseInt(Util.p.getProperty("systemupdateperiod", "1000")));
        Message m = new Message(ModuleTimer.getInstance(), getInstance(), MSG_UPDATE_INFO);
        CallbackSendMessage csm = new CallbackSendMessage(m);
        Module.sendMessage(new MessageCallback(getInstance(), ModuleTimer.getInstance(), ModuleTimer.MSG_CALLBACK_PERIODIC, csm, systemUpdatePeriod));
    }
    int i = 0;
    void update(){

        System.out.println("update started" + i);
        i++;

        double tcpu_load = 0;
        try {
            tcpu_load = stub.getCpuLoad();
            if(loads.size() >= maxDequeMembers){
                cpu_load_sum -= loads.pop();
            }
            cpu_load_sum += tcpu_load;
            loads.push(tcpu_load);
            cpu_load = cpu_load_sum / loads.size();
            free_disk = stub.getFreeDisk();
            total_disk = stub.getTotalDisk();
            free_ram = stub.getFreeRam();
            total_ram = stub.getTotalRam();
            free_swap = stub.getFreeSwap();
            total_swap = stub.getTotalSwap();
            num_processes = stub.getNumProcesses();
            num_cores = stub.getNumCores();
            kernel_ver = stub.getKernelVer();
            logged_users = stub.getLoggedUsers();
            dns_names = stub.getDnsNames();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("update finished");
    }
    @Override
    public void receiveMessage(Message m){
        if(m.messageType == MSG_UPDATE_INFO){
            update();
            MessageAttributes msg = new MessageAttributes(getInstance(), ModuleAgent.getInstance(), ModuleAgent.MSG_SYSTEM_DATA);
            msg.hm.put(new Attribute("cpu_load"), new ValueDouble(cpu_load));
            msg.hm.put(new Attribute("free_disk"), new ValueInt(free_disk));
            msg.hm.put(new Attribute("total_disk"), new ValueInt(total_disk));
            msg.hm.put(new Attribute("free_ram"), new ValueInt(free_ram));
            msg.hm.put(new Attribute("total_ram"), new ValueInt(total_ram));
            msg.hm.put(new Attribute("free_swap"), new ValueInt(free_swap));
            msg.hm.put(new Attribute("total_swap"), new ValueInt(total_swap));
            msg.hm.put(new Attribute("num_processes"), new ValueInt(num_processes));
            msg.hm.put(new Attribute("num_cores"), new ValueInt(num_cores));
            msg.hm.put(new Attribute("kernel_ver"), new ValueString(kernel_ver));
            msg.hm.put(new Attribute("logged_users"), new ValueInt(logged_users));
            List<Value> l = new ArrayList<>();
            for(String s : dns_names){
                l.add(new ValueString(s));
            }
            msg.hm.put(new Attribute("dns_names"), new ValueList(
                    new ValueList(l, TypePrimitive.STRING),
                    TypePrimitive.STRING));
            Module.sendMessage(msg);
        }
    }
    static ModuleSystemInfo getInstance(){
        return instance;
    }
}
