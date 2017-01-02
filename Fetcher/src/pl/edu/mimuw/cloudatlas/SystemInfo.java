package pl.edu.mimuw.cloudatlas;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by julek on 02-Jan-17.
 */
public interface SystemInfo extends Remote {
    double getCpuLoad() throws RemoteException;
    long getFreeDisk() throws RemoteException;
    long getTotalDisk() throws RemoteException;
    long getFreeRam() throws RemoteException;
    long getTotalRam() throws RemoteException;
    long getFreeSwap() throws RemoteException;
    long getTotalSwap() throws RemoteException;
    long getNumProcesses() throws RemoteException;
    long getNumCores() throws RemoteException;
    String getKernelVer() throws RemoteException;
    long getLoggedUsers() throws RemoteException;
    String[] getDnsNames() throws RemoteException;
}
