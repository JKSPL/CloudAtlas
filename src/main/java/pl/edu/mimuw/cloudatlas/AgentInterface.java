package pl.edu.mimuw.cloudatlas;

import pl.edu.mimuw.cloudatlas.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Created by jks on 1/5/17.
 */
public interface AgentInterface extends Remote {
    public void setFallbackContacts(Set<ValueContact> s) throws RemoteException;
    public Set<PathName> getAvailableZones() throws RemoteException;
    public AttributesMap getAttributesOfZone(PathName p) throws RemoteException;
    public void setAttribute(PathName p, Attribute attr, Value val) throws RemoteException;
}
