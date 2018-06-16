package miner.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MinerRMIInterfaceCambioLeader extends Remote{
	public String sendCambioLeader() throws RemoteException;
}
