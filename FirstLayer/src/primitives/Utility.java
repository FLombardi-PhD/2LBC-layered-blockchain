package primitives;

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

import org.apache.commons.lang3.SerializationUtils;

public class Utility {
	
	public static int PORT_NUMBER = 8808;
	public static String NAME_CHANNEL = "first-layer";
	public static String IP_ADDRESS = "localhost";

	public static String getIP_ADDRESS() {
		return IP_ADDRESS;
	}

	public static int getPORT_NUMBER() {
		return PORT_NUMBER;
	}

	public static String getNAME_CHANNEL() {
		return NAME_CHANNEL;
	}
	
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
			System.err.println("Errore NosuchException 1");
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
			System.err.println("Errore NosuchException 2");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.err.println("Errore InvalidKeyException");
			e.printStackTrace();
		} catch (SignatureException e) {
			System.err.println("Errore SignatureException");
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
	
	public static byte[] getByteTransaction(Transaction t){
		return SerializationUtils.serialize(t);
	}
	
	public static byte[] getByteOperation(Operation o){
		return SerializationUtils.serialize(o);
	}
}
