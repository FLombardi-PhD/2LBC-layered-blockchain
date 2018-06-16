package miner.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.Hashtable;
import java.util.TreeMap;

import client.ClientInterface;
import miner.jgroup.MinerJGroup;
import primitives.Buffer;
import primitives.FinalizedTransaction;
import primitives.Operation;
import util.UtilParams;

public class MinerRMIImplementation implements MinerRMIInterface, Runnable, MinerRMIInterfaceCambioLeader {
	
	Buffer buffer;
	TreeMap< Integer, PublicKey > keyMap;
	PublicKey keyClient;
	int port;
	MinerJGroup minerJGroup;
	private Hashtable<Integer, Integer> ciList = new Hashtable<>();
	
	//CONSTRUCTOR
	
	public MinerRMIImplementation() throws RemoteException {}
	
	public MinerRMIImplementation(Buffer buffer, int p, MinerJGroup mjg) throws RemoteException {
		this.minerJGroup = mjg;
		this.buffer = buffer;
		this.port = p;
		this.keyMap = new TreeMap<>();
	}
	
	//OTHER METHODS

	@Override
	public synchronized String sendOperation(Operation o) throws RemoteException {
		buffer.addOperation(o);
		return "Operation executed correctly";
	}

	@Override
	public void run() {
		try {
            MinerRMIInterface stub = (MinerRMIInterface) UnicastRemoteObject.exportObject(this, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry( this.port );
            registry.rebind( UtilParams.getRMI_NAME_CHANNEL_MINER() + this.port, stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
	}

	@Override
	public void register(int client, int port, PublicKey keyClient) throws RemoteException {
		System.out.println( "Nuovo Client connesso con Id: " + client );
		if(!this.keyMap.containsKey(client)){
			this.keyMap.put(client, keyClient);
			buffer.setClientPublicKey(client, keyClient);
		}
		if( !this.ciList.containsKey(client) ){
			this.ciList.put(client, port);
		}
	}

	@Override
	public String sendCambioLeader() throws RemoteException {
		this.minerJGroup.cambioLeader();
		return null;
	}
	
	public void sendResponseToClient(FinalizedTransaction t, int idClient) {
		Registry registry;
		try {
			int portClient = this.ciList.get( idClient );
			registry = LocateRegistry.getRegistry(UtilParams.getIP_ADDRESS(), portClient);
			ClientInterface mi = (ClientInterface) registry.lookup( UtilParams.getRMI_NAME_CHANNEL_CLIENT() + portClient);
			mi.getResponse(t);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

}
