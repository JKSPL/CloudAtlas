package catlas;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Created by julek on 30-Dec-16.
 */
public class ModuleSystemInfo extends Module {
    static String name = "systeminfo";
    static ModuleSystemInfo instance = new ModuleSystemInfo();
    OperatingSystemMXBean osMXBean;
    File f;
    protected ModuleSystemInfo() {
        super(name);
    }
    
    public void init(){
        f = new File("dummy.txt");
        super.init();
        osMXBean = ManagementFactory.getOperatingSystemMXBean();
    }
    double getCpuLoad(){
        return osMXBean.getSystemLoadAverage();
    }
    String getVersion(){
        return osMXBean.getVersion();
    }
    long getProcessorCount(){
        return osMXBean.getAvailableProcessors();
    }
    long getTotalSpace(){
        return f.getTotalSpace();
    }
    long getFreeSpace(){
        return f.getFreeSpace();
    }
    static ModuleSystemInfo getInstance(){
        return instance;
    }
}
