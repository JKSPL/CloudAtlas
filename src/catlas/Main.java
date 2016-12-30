package catlas;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Properties;

//import org.apache.commons.cli.*;
public class Main {
    static Executor[] runningExecutors;
    static Thread[] threadsExecutors;
    static int executorsCo;
    static int executorIdx = 0;
    static HashSet<String> enabledModulesNames = new HashSet<String>();
    static String configFileName;
    static Properties p;
    static boolean readCommandLines(String[] args){
        Options options = new Options();
        Option optionExecutorsCo = Option.builder("e")
                .longOpt("executors")
                .desc("number of executors 1 - 20")
                .hasArg()
                .build();
        Option optionConfigFileName = Option.builder("c")
                .longOpt("config-file")
                .desc("config file name")
                .hasArg()
                .build();
        options.addOption(optionExecutorsCo);
        options.addOption(optionConfigFileName);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return false;
        }
        String [] a = cmd.getArgs();
        for(String arg : a){
            enabledModulesNames.add(arg);
        }
        executorsCo = Integer.parseInt(cmd.getOptionValue("e", "2"));
        configFileName = cmd.getOptionValue("c", "config.properties");
        if(executorsCo < 1 || executorsCo > 20){
            System.out.println("e must be 1-20");
            return false;
        }
        return true;
    }
    static Executor nextExecutor(){
        if(executorIdx == executorsCo){
            executorIdx = 0;
        }
        return runningExecutors[executorIdx++];
    }
    static boolean initModules(){
        if(enabledModulesNames.contains(ModuleTimer.name)){
            ModuleTimer.getInstance().init(nextExecutor());
        }
        if(enabledModulesNames.contains(ModuleCommunication.name)){
            try {
                ModuleCommunication.getInstance().init(nextExecutor(), Integer.parseInt(p.getProperty("server_port", "1234")));
            } catch (SocketException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    static boolean initExecutors(){
        System.out.println("Executors count set to " + Integer.toString(executorsCo));
        runningExecutors = new Executor[executorsCo];
        threadsExecutors = new Thread[executorsCo];
        for(int i = 0; i < executorsCo; i++) {
            runningExecutors[i] = new Executor(i);
            threadsExecutors[i] = new Thread(runningExecutors[i]);
        }
        return true;
    }
    static boolean readConfig(){
        p = new Properties();
        InputStream input = null;
        boolean flag = true;
        try {
            input = new FileInputStream(configFileName);
            p.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if(input != null){
                try{
                    input.close();
                } catch(IOException e){
                    e.printStackTrace();
                    flag = false;
                }
            }
        }
        return flag; 
    }
    public static void main(String[] args) {
        if(
                !readCommandLines(args) 
                || !readConfig() 
                || !initExecutors()
                || !initModules()
                ){
            return;
        }
        for(int i = 0; i < executorsCo; i++){
            threadsExecutors[i].start();
        }
        /*for(int i = 0; i < 1000; i++){
            MessageCallback m = new MessageCallback(ModuleCommunication.getInstance(), ModuleTimer.getInstance(), ModuleTimer.MSG_CALLBACK_SECONDS, new CallbackPrint(), 3000 + i * 500);
            MessageOverNetwork m2 = new MessageOverNetwork(m, "127.0.0.1", 8127, ModuleUdpSender.MSG_SEND_MESSAGE) ;
            Module.sendMessage(m2);
        }*/
    }
}
