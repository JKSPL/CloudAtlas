package pl.edu.mimuw.cloudatlas;

import java.io.File;

import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;

import com.sun.management.OperatingSystemMXBean;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleSystemInfo extends Module {
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
    protected ModuleSystemInfo() {
        super(name);
    }
    
    public void init(){
        super.init();
        osMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        maxDequeMembers = Integer.max(1, Integer.parseInt(Util.p.getProperty("cpuloadgatherunits", "1")));
    }
    void update(){
        cpu_load = osMXBean.getSystemCpuLoad();
        if(loads.size() >= maxDequeMembers){
            cpu_load_sum -= loads.pop();
            cpu_load_sum += cpu_load;
        }
        File[] roots = File.listRoots();
        free_disk = 0;
        total_disk = 0;
        for(File root: roots){
            free_disk += root.getFreeSpace();
            total_disk += root.getTotalSpace();
        }
        
        free_ram = osMXBean.getFreePhysicalMemorySize();
        total_ram = osMXBean.getTotalPhysicalMemorySize();
        free_swap = osMXBean.getFreeSwapSpaceSize();
        total_swap = osMXBean.getTotalSwapSpaceSize();
//        num_processes;
        num_cores = osMXBean.getAvailableProcessors();
        kernel_ver = osMXBean.getVersion();
//        logged_users;
//        dns_names;
    }
    static ModuleSystemInfo getInstance(){
        return instance;
    }
}
