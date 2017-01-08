package pl.edu.mimuw.cloudatlas;

import org.apache.commons.cli.*;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import spark.Spark;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;

//import org.apache.commons.cli.*;
public class Agent {
    static Executor[] runningExecutors;
    static Thread[] threadsExecutors;
    static int executorsCo;
    static int executorIdx = 0;
    static HashSet<String> enabledModulesNames = new HashSet<String>();
    static String configFileName;
    static int apiport;
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
        ModuleTimer.getInstance().init();
        try {
            ModuleCommunication.getInstance().init(Integer.parseInt(Util.p.getProperty("server_port", "1234")));
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        ModuleSystemInfo.getInstance().init();
        ModuleAgent.getInstance().init();
        ModuleUdpServer.getInstance().start();
        ModuleAgent.getInstance().start();
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
        Util.p = new Properties();
        InputStream input = null;
        boolean flag = true;
        try {
            input = new FileInputStream(configFileName);
            Util.p.load(input);
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
        apiport = Integer.parseInt(Util.p.getProperty("apiport"));
        Spark.port(apiport);
        return flag; 
    }
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
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
        Scanner console = new Scanner(System.in);

        while(true){
            String s0 = console.next();
            String s1 = console.next();
            String s2 = console.next();
            int s3 = Integer.parseInt(s2);
            ValueContact c = new ValueContact(new PathName(s0), new InetSocketAddress(s1, s3));
            ModuleAgent.getInstance().fallbackContacts.add(c);
        }
    }
}
