package pl.edu.mimuw.cloudatlas;

import com.sun.management.OperatingSystemMXBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashSet;

import static java.lang.Thread.sleep;

/**
 * Created by julek on 02-Jan-17.
 */
public class    UbuntuComputerSystemInfo implements SystemInfo, Runnable {
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

    OperatingSystemMXBean osMXBean;
    long updateInterval;
    Thread updateThread;

    UbuntuComputerSystemInfo(){
        osMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        updateInterval = Integer.min(500, Integer.parseInt(Fetcher.properties.getProperty("updateinterval", "3000")));
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
        cpu_load = osMXBean.getSystemCpuLoad();
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
        num_processes = countLines(executeCommand("ps aux"));
        num_cores = osMXBean.getAvailableProcessors();
        kernel_ver = osMXBean.getVersion();
        logged_users = new HashSet<String>(Arrays.asList(executeCommand("users").replace("\n", "").split(" "))).size();
        dns_names =  executeCommand("hostname").split("\n");
        if(dns_names.length >= 3){
            String[] tdns_names = new String[3];
            for(int i = 0; i < 3; i++){
                tdns_names[i] = dns_names[i];
            }
            dns_names = tdns_names;
        }
    }
    int countLines(String str) {
        if(str == null || str.isEmpty())
        {
            return 0;
        }
        int lines = 1;
        int pos = 0;
        while ((pos = str.indexOf("\n", pos) + 1) != 0) {
            lines++;
        }
        return lines;
    }
    @Override
    public void run() {
        try{
            while(true){
                update();
                sleep(updateInterval);
            }
        } catch (InterruptedException e) {
            System.out.println("close");
            return;
        }
    }
    String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }
}
