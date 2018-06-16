package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import primitives.FinalizedTransaction;

public interface ClientInterface extends Remote{
	public void getResponse(FinalizedTransaction t) throws RemoteException;
}
