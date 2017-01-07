package pl.edu.mimuw.cloudatlas;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by jks on 1/3/17.
 */
public interface SigningInterface extends Remote {
    public byte[] signInstallQuery(String query, String name) throws RemoteException;
    public byte[] signUninstallQuery(String name) throws RemoteException;
}
