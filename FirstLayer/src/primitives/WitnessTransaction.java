package primitives;

import java.io.Serializable;
import java.util.*;

public class WitnessTransaction implements Serializable{

	@Override
	public String toString() {
		return "WitnessTransaction [id = " + id + ", timestamp = " + timestamp + ", round = " + round + ", listHashes = "
				+ listHashes + ", idPreviousWitness = " + idPreviousWitness + ", signatures = " + signatures + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int timestamp;
	private int round;
	private ArrayList< byte[] > listHashes;
	private int idPreviousWitness;
	private ArrayList< byte[] > signatures;
	
	public WitnessTransaction(int id, int timestamp, int round, ArrayList<byte[]> listHashes, int idPreviousWitness, ArrayList<byte[]> signatures) {
		this.id = id;
		this.timestamp = timestamp;
		this.round = round;
		this.listHashes = listHashes;
		this.idPreviousWitness = idPreviousWitness;
		this.signatures = signatures;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public ArrayList<byte[]> getListHashes() {
		return listHashes;
	}

	public void setListHashes(ArrayList<byte[]> listHashes) {
		this.listHashes = listHashes;
	}

	public int getIdPreviousWitness() {
		return idPreviousWitness;
	}

	public void setIdPreviousWitness(int idPreviousWitness) {
		this.idPreviousWitness = idPreviousWitness;
	}

	public ArrayList<byte[]> getSignatures() {
		return signatures;
	}

	public void setSignatures(ArrayList<byte[]> signatures) {
		this.signatures = signatures;
	}
	
}
