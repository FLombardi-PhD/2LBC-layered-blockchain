package main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import client.Client;

public class MainClient {

	private static final String PROP_FILENAME = "miner.properties";
	private static final String HOSTS_DELIMITER = ",";
	
	public static void main(String[] args) {
		//TODO controllare questa linea; old code: if(args.length == 3){
		if(args.length == 1){	
			int ownPort = Integer.parseInt( args[0] );
			
			Properties prop = new Properties();
			InputStream inputStream = MainClient.class.getClassLoader().getResourceAsStream(PROP_FILENAME);
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				System.err.println("Some problem occur while reading property file '" + PROP_FILENAME + "'. It might be not found in the classpath");	
				e.printStackTrace();
			}
			
			String miners = prop.getProperty("miners");
			String[] hosts = miners.split(HOSTS_DELIMITER);
			
			MinerHost[] minerHosts = new MinerHost[hosts.length];
			for (int i = 0; i < hosts.length; ++i) {
				String[] arrayHostPort = hosts[i].split(":");
				minerHosts[i] = new MinerHost(arrayHostPort[0], Integer.parseInt(arrayHostPort[1]));
			}
			
			
			boolean set = Boolean.valueOf(prop.getProperty("set"));
			
			//TODO controllare questa linea; old code: int firstPort = Integer.parseInt( args[1] );
			//TODO controllare questa linea; old code: int lastPort = Integer.parseInt( args[2] );
			//TODO controllare questa linea; old code: boolean set = true; //true per set test, false per get test
			//TODO controllare questa linea; old code: Thread client = new Thread( new Client(ownPort, firstPort, lastPort, set) );
			
			Thread client = new Thread( new Client(ownPort, minerHosts, set) );
	        client.start();
		}
		else{
			System.err.println("Error: insufficient arguments.");
		}
        
	}
	
	public static class MinerHost {
		
		private String host;
		private int port;
				
		public MinerHost(String host, int port) {
			this.host = host;
			this.port = port;
		}
		
		public String getHost() {
			return host;
		}
		
		public int getPort() {
			return port;
		}
	}
}
