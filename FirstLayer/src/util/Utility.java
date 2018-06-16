package util;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.concurrent.TimeUnit;

public class Utility {
	
	public static int getRandom(int min, int max){
		int range = (max - min) + 1;     
		return (int)(Math.random() * range) + min;
	}
	
	public static KeyPair getKeyPair(){
		return getKeyPairGenerator().generateKeyPair();
	}
	
	public static KeyPairGenerator getKeyPairGenerator(){
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);
			return keyGen;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized static byte[] signObject(PrivateKey p, byte[] data){
		try {
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(p);
			dsa.update(data);
			return dsa.sign();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static boolean verifySignatures(PublicKey p, byte[] data, byte[] sign){
		try {
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initVerify(p);
			dsa.update(data);
			if(sign == null){
				System.out.println("null");
			}
			return dsa.verify(sign);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.err.println("Errore NosuchException 3");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.err.println("Errore InvalidKeyException");
			e.printStackTrace();
		} catch (SignatureException e) {
			System.err.println("Errore SignatureException");
			e.printStackTrace();
		}
		return false;
	}
	
	public static String calculateTime(long seconds) {
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
	    long hours = TimeUnit.SECONDS.toHours(seconds) -
	                 TimeUnit.DAYS.toHours(day);
	    long minute = TimeUnit.SECONDS.toMinutes(seconds) - 
	                  TimeUnit.DAYS.toMinutes(day) -
	                  TimeUnit.HOURS.toMinutes(hours);
	    long second = TimeUnit.SECONDS.toSeconds(seconds) -
	                  TimeUnit.DAYS.toSeconds(day) -
	                  TimeUnit.HOURS.toSeconds(hours) - 
	                  TimeUnit.MINUTES.toSeconds(minute);
        return ("Day " + day + " Hour " + hours + " Minute " + minute + " Seconds " + second);

    }
}
