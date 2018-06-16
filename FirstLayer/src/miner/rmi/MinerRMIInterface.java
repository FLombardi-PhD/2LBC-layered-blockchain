package miner.rmi;

import java.rmi.*;
import java.security.PublicKey;

import primitives.Operation;

public interface MinerRMIInterface extends Remote{
	public String sendOperation( Operation t ) throws RemoteException;
	void register(int client, int port, PublicKey key) throws RemoteException;
}
