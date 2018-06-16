package miner.jgroup;

import java.security.PublicKey;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.SerializationUtils;

import primitives.Operation;
import util.Utility;
import primitives.FinalizedTransaction;
import primitives.FinalizedWitnessTransaction;

public class VerifyManager {
	
	public static boolean verifyTransaction(FinalizedTransaction t, PublicKey keyMiner, PublicKey keyClient){
		boolean res = false;
		Operation opT = t.getOperation();
		if( verifyOperation(opT, keyClient) == true ){
			FinalizedTransaction temp = new FinalizedTransaction(t.getId(), t.getIdLeader(), t.getTimestamp(), t.getRound(), opT, t.getACK());
			byte[] data = SerializationUtils.serialize(temp);
			CopyOnWriteArrayList<byte[]> signs = t.getSignatures();
			Iterator<byte[]> iterator = signs.iterator();
			while(iterator.hasNext()){
				res = Utility.verifySignatures(keyMiner, data, iterator.next());
				if( res == false )
					return false;
			}
			return res;
		}
		return false;
	}
	
	public static boolean verifyOperation(Operation o, PublicKey key){
		Operation tempOp = new Operation(o.getId(), o.getMethod(), o.getValue(), o.getIdClient());
		byte[] dataOpT = SerializationUtils.serialize(tempOp);
		return Utility.verifySignatures(key, dataOpT, o.getSign());
	}
	
	public static boolean verifyWitnessTransaction(FinalizedWitnessTransaction wt, PublicKey key){
		boolean res = false;
		FinalizedWitnessTransaction temp = new FinalizedWitnessTransaction(wt.getId(), wt.getIdLeader(), wt.getTimestamp(), wt.getRound(), wt.getHashRound(), true, null);
		byte[] data = SerializationUtils.serialize(temp);
		CopyOnWriteArrayList<byte[]> signs = wt.getSignatures();
		Iterator<byte[]> iterator = signs.iterator();
		while(iterator.hasNext()){
			res = Utility.verifySignatures(key, data, iterator.next());
			if( res == false )
				return false;
		}
		return res;
	}

}
