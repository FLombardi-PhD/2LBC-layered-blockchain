package main;
import miner.jgroup.TimeoutManager;
import util.UtilParams;

public class MainTimeout {

	public static void main(String[] args) {
		if(args.length == 2){
			int firstPort = Integer.parseInt( args[0] );
			int lastPort = Integer.parseInt( args[1] );
			Thread manager = new Thread( new TimeoutManager(firstPort, lastPort, UtilParams.getROUND_TIME()*1000) );
	        manager.start();
		}
	}

}
