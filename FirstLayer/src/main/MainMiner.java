package main;
import java.rmi.RemoteException;

import org.apache.log4j.PropertyConfigurator;

import miner.jgroup.MinerJGroup;
import miner.rmi.MinerRMIImplementation;
import primitives.Buffer;

public class MainMiner {

	public static void main(String[] args) {
		
		PropertyConfigurator.configure("Log4j/log4j.properties");
		
		if( args.length == 3){
			int port = Integer.parseInt( args[0] );
			int idMiner = Integer.parseInt( args[2] );
			boolean leader = args[1].equals("true");
			Buffer buffer = new Buffer(1000);
			try {
				MinerJGroup mjg = new MinerJGroup( buffer, leader, idMiner );
				MinerRMIImplementation mri = new MinerRMIImplementation( buffer, port, mjg );
				mjg.setMri(mri);
				Thread miner1 = new Thread( mri );
				miner1.start();
				System.out.println( "RMI miner avviato" );
				//logger.info("RMI miner avviato");
				Thread miner2 = new Thread( mjg );
				miner2.start();
				System.out.println( "JGroup miner avviato" );
				//logger.info("JGroup miner avviato");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else{
			System.err.println("Run Configuration Error");
		}
		
	}

}
