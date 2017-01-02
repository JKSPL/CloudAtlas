package pl.edu.mimuw.cloudatlas;

import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by julek on 02-Jan-17.
 */
public class Fetcher {
    static Properties properties;
    static String configFileName;
    public static void main(String[] args){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        if(
                !readCommandLines(args)
                || !readConfig())
        {
            return;
        }
        UbuntuComputerSystemInfo object = new UbuntuComputerSystemInfo();
        object.start();
        try {
            SystemInfo stub = (SystemInfo) UnicastRemoteObject.exportObject(object, Integer.parseInt(properties.getProperty("port", "0")));
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("pl.edu.mimuw.cloudatlas.SystemInfo", stub);
            System.out.println("pl.edu.mimuw.cloudatlas.UbuntuComputerSystemInfo bound");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    static boolean readCommandLines(String[] args){
        Options options = new Options();
        Option optionConfigFileName = Option.builder("c")
                .longOpt("config-file")
                .desc("config file name")
                .hasArg()
                .build();
        options.addOption(optionConfigFileName);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return false;
        }
        configFileName = cmd.getOptionValue("c", "config.properties");
        return true;
    }
    
    static boolean readConfig(){
        properties = new Properties();
        InputStream input = null;
        boolean flag = true;
        try {
            input = new FileInputStream(configFileName);
            properties.load(input);
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
}
