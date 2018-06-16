package primitives;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

public class Transaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private long[] id;
	private Timestamp timestamp;
	private int idLeader;
	private long round;
	
	public Transaction(long[] id, Timestamp timestamp, int leaderId, long round) {
		this.id = id;
		this.idLeader = leaderId;
		this.timestamp = timestamp;
		this.round = round;
	}

	public long[] getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public int getIdLeader(){
		return this.idLeader;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public long getRound() {
		return round;
	}



	public void setRound(long round) {
		this.round = round;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(id);
		result = prime * result + idLeader;
		result = prime * result + (int) (round ^ (round >>> 32));
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		Transaction other = (Transaction) obj;
		if (!Arrays.equals(id, other.id))
			return false;
		if (idLeader != other.idLeader)
			return false;
		if (round != other.round)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return "Transaction [id=" + Arrays.toString(id) + ", timestamp=" + timestamp + ", idLeader=" + idLeader + ", round=" + round + "]";
		return "Transaction [id=" + Arrays.toString(id) + ", idLeader=" + idLeader + ", round=" + round + "]";
	}
	
}
