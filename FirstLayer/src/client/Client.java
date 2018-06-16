package client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.PropertyConfigurator;

import com.sun.istack.internal.logging.Logger;

import main.MainClient.MinerHost;
import miner.rmi.*;
import primitives.FinalizedTransaction;
import primitives.Operation;
import primitives.OperationValue;
import util.Method;
import util.UtilParams;
import util.Utility;

public class Client implements Runnable, ClientInterface, Serializable, Comparable<Client>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int i = 1;
	private KeyPair key;
	private ArrayList<MinerRMIInterface> miList = new ArrayList<MinerRMIInterface>();
	private int rmiPort;
	//TODO controllare questa linea; old code: private int firstPort, lastPort;
	private MinerHost[] minerHosts;
	private int idClient;
	Logger logger;
	
	private boolean set;
	
	CSVPrinter csvFilePrinter;
	
	//chiave richiesta, timestamp 
	private Hashtable<Integer, Timestamp> timeRequestOperation;
	private Hashtable<Integer, Integer> responseRequestOperation;
	
	//CONSTRUCTOR
	
	//TODO controllare questa linea; old code: public Client(int ownPort, int firstPort, int lastPort, boolean set){
	public Client(int ownPort, MinerHost[] minerHosts, boolean set){	
		try {
			if(set){
				Path path = Paths.get( UtilParams.getPATH_LATENCY_FILE() );
				if( !Files.exists(path) ){
					if( !new File( UtilParams.getPATH_LATENCY_FILE() ).mkdirs() ){
						System.err.println("Creazione directory non andata a buon fine!");
					}
				}
				FileWriter fileWriter = new FileWriter( UtilParams.getPATH_LATENCY_FILE() + UtilParams.getNAME_LATENCY_FILE());
				CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
				this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
				this.csvFilePrinter.close();
			}
			else{
				FileWriter fileWriter = new FileWriter( UtilParams.getLATENCY_GET_FILE());
				CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
				this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
				this.csvFilePrinter.close();
				
				this.timeRequestOperation = new Hashtable<Integer, Timestamp>();
				this.responseRequestOperation = new Hashtable<Integer, Integer>();
			}
				
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.set = set;
		this.key = Utility.getKeyPair();
		this.rmiPort = ownPort;
		/*TODO controllare questa linea; old code: this.firstPort = firstPort;
		this.lastPort = lastPort; */
		this.minerHosts = minerHosts;
		this.idClient = Utility.getRandom(1, Integer.MAX_VALUE);
		PropertyConfigurator.configure("Log4j/log4j.properties");
		this.logger = Logger.getLogger(Client.class);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//TODO controllare questa linea; old code: result = prime * result + firstPort;
		result = prime * result + minerHosts[0].getPort();
		result = prime * result + i;
		result = prime * result + idClient;
		//TODO controllare questa linea; old code: result = prime * result + lastPort;
		result = prime * result + minerHosts[minerHosts.length-1].getPort();
		result = prime * result + ((miList == null) ? 0 : miList.hashCode());
		result = prime * result + rmiPort;
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		//TODO controllare questa linea; old code: if (firstPort != other.firstPort)
		if (minerHosts[0].getPort() != other.minerHosts[0].getPort())
			return false;
		if (i != other.i)
			return false;
		if (idClient != other.idClient)
			return false;
		////TODO controllare questa linea; old code: if (lastPort != other.lastPort)
		if (minerHosts[minerHosts.length-1].getPort() != other.minerHosts[minerHosts.length-1].getPort())
			return false;
		if (miList == null) {
			if (other.miList != null)
				return false;
		} else if (!miList.equals(other.miList))
			return false;
		if (rmiPort != other.rmiPort)
			return false;
		return true;
	}



	public PublicKey getPublicKey(){
		return getKey().getPublic();
	}
	
	private synchronized PrivateKey getPrivateKey(){
		return getKey().getPrivate();
	}
	
	//OTHER METHODS
	
	long aggiustamento = 0;

	@Override
	public void run() {
		try {
            ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry( this.rmiPort );
            registry.rebind( UtilParams.getRMI_NAME_CHANNEL_CLIENT() + this.rmiPort, stub);
            System.out.println("Server Client ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
		try {
			MinerRMIInterface mi;
			String nameRMIChannel;
			/*TODO controllare questa linea; old code: for(int port = firstPort; port <= lastPort; port++){
				Registry registry = LocateRegistry.getRegistry(UtilParams.getIP_ADDRESS(), port);
				nameRMIChannel = UtilParams.getRMI_NAME_CHANNEL_MINER() + port;
				mi = (MinerRMIInterface) registry.lookup( nameRMIChannel );
				mi.register(this.idClient, this.rmiPort, this.getPublicKey());
				this.miList.add( mi );
			}*/
			for(int i = 0; i < minerHosts.length; ++i){
				Registry registry = LocateRegistry.getRegistry(minerHosts[i].getHost(), minerHosts[i].getPort());
				nameRMIChannel = UtilParams.getRMI_NAME_CHANNEL_MINER() + minerHosts[i].getHost() + minerHosts[i].getPort();
				mi = (MinerRMIInterface) registry.lookup( nameRMIChannel );
				mi.register(this.idClient, this.rmiPort, this.getPublicKey());
				this.miList.add( mi );
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		int sleep = (1000 / UtilParams.getRATE());
		long startTime = System.currentTimeMillis();
		
		//while( i <= 500 ){
		while( (System.currentTimeMillis() - startTime) < UtilParams.getDURATION_TEST_TIME()){
			if(this.set){
				try {
					if( (sleep - aggiustamento) > 0 ) 
						Thread.sleep(sleep - aggiustamento);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			try {
				Long agg = System.currentTimeMillis();
				if(this.set){
					OperationValue value = new OperationValue(i, i);
					Timestamp id = new Timestamp( System.currentTimeMillis());
					Operation opTemp = new Operation(id, Method.SET, value, this.idClient);
					byte[] data = SerializationUtils.serialize(opTemp);
					byte[] sign = Utility.signObject( getPrivateKey(), data );
					Operation op = new Operation(id, Method.SET, value, sign, this.idClient);
					System.out.println( op.getValue().toString() );
					for(MinerRMIInterface mri: miList){
						mri.sendOperation(op);
					}
					i++;
				}
				else{
					OperationValue value = new OperationValue( Utility.getRandom(1, 11000) );
					Timestamp id = new Timestamp( System.currentTimeMillis() );
					Operation opTemp = new Operation(id, Method.GET, value, this.idClient);
					byte[] data = SerializationUtils.serialize(opTemp);
					byte[] sign = Utility.signObject( getPrivateKey(), data );
					Operation op = new Operation(id, Method.GET, value, sign, this.idClient);
					this.timeRequestOperation.put( value.getKey(), id);
					this.responseRequestOperation.put( value.getKey(), 0);
					System.out.println( op.getValue().toString() );
					for(MinerRMIInterface mri: miList){
						mri.sendOperation(op);
					}
					i++;
				}
				aggiustamento = System.currentTimeMillis() - agg;
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		System.out.println( "Numero operazioni richieste: " + i );
	}

	@Override
	public synchronized void getResponse(FinalizedTransaction t) throws RemoteException {
		
		Operation o = t.getOperation();
		long temp = System.currentTimeMillis();
		if(this.set){
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter( UtilParams.getPATH_LATENCY_FILE() + UtilParams.getNAME_LATENCY_FILE(), true);
				CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
				this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
				ZoneId zone1 = ZoneId.of("Europe/Rome");
				LocalTime lt = LocalTime.now(zone1);
				Object[] array = { o.getValue().toString(), String.valueOf( (System.currentTimeMillis() - o.getId().getTime() ) / 1000.0), lt.toString() };
				this.csvFilePrinter.printRecord(array);
				this.csvFilePrinter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			int key = o.getValue().getKey();
			try {
				if( !this.responseRequestOperation.containsKey(key)){
					this.responseRequestOperation.put(key, 1);
				}
				else{
					int value = this.responseRequestOperation.get(key);
					this.responseRequestOperation.put(key, value+1);
				}
				if( this.responseRequestOperation.get(key) >= UtilParams.getNUM_MINERS() ){
					FileWriter fileWriter = new FileWriter( UtilParams.getLATENCY_GET_FILE(), true);
					CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
					this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
					ZoneId zone1 = ZoneId.of("Europe/Rome");
					LocalTime lt = LocalTime.now(zone1);
					Object[] array = { o.getValue().toString(), String.valueOf( (System.currentTimeMillis() - this.timeRequestOperation.get( o.getValue().getKey() ).getTime() ) / 1000.0), lt.toString() };
					this.csvFilePrinter.printRecord(array);
					this.csvFilePrinter.close();
					this.responseRequestOperation.remove(key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		aggiustamento += System.currentTimeMillis() - temp;
	}

	public KeyPair getKeyPair() {
		return getKey();
	}

	public void setKeyPair(KeyPair key) {
		setKey(key);
	}

	@Override
	public int compareTo(Client o) {
		return this.getKeyPair().toString().compareTo(o.getKeyPair().toString());
	}

	@Override
	public String toString() {
		return "Client [i=" + i + ", keyPair=" + getKeyPair().toString() + "]" ;
	}

	public KeyPair getKey() {
		return key;
	}

	public void setKey(KeyPair key) {
		this.key = key;
	}
	
	

}
