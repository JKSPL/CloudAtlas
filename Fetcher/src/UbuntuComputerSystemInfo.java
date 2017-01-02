import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;

/**
 * Created by julek on 02-Jan-17.
 */
public class UbuntuComputerSystemInfo implements SystemInfo, Runnable {
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

    OperatingSystemMXBean osMXBean;
    long delay;
    Thread updateThread;
    
    UbuntuComputerSystemInfo(){
        osMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        maxDequeMembers = Integer.max(1, Integer.parseInt(Fetcher.properties.getProperty("cpuloadgatherunits", "3")));
        delay = Integer.min(500, Integer.parseInt(Fetcher.properties.getProperty("updateinterval", "3000")));
        updateThread = new Thread(this);
        update();
    }
    
    void start(){
        updateThread.start();
    }
    
    @Override
    synchronized public double getCpuLoad() {
        return cpu_load;
    }

    @Override
    synchronized public long getFreeDisk() {
        return free_disk;
    }

    @Override
    synchronized public long getTotalDisk() {
        return total_disk;
    }

    @Override
    synchronized public long getFreeRam() {
        return free_ram;
    }

    @Override
    synchronized public long getTotalRam() {
        return total_ram;
    }

    @Override
    synchronized public long getFreeSwap() {
        return free_swap;
    }

    @Override
    synchronized public long getTotalSwap() {
        return total_swap;
    }

    @Override
    synchronized public long getNumProcesses() {
        return num_processes;
    }

    @Override
    synchronized public long getNumCores() {
        return num_cores;
    }

    @Override
    synchronized public String getKernelVer() {
        return kernel_ver;
    }

    @Override
    synchronized public long getLoggedUsers() {
        return logged_users;
    }

    @Override
    synchronized public String[] getDnsNames() {
        return dns_names;
    }
    synchronized void update(){
        double tcpu_load = osMXBean.getSystemCpuLoad();
        if(loads.size() >= maxDequeMembers){
            cpu_load_sum -= loads.pop();
            cpu_load_sum += tcpu_load;
        }
        cpu_load = cpu_load_sum / tcpu_load; 
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

    @Override
    synchronized public void run() {
        try{
            while(true){
                update();
                wait(3000);
            }
        } catch (InterruptedException e) {
            System.out.println("close");
            return;
        }
    }
}
