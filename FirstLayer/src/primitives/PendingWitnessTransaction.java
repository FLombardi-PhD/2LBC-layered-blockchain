package primitives;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

public class PendingWitnessTransaction extends Transaction implements Serializable, Comparable<PendingWitnessTransaction>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] hashRound;
	
	public PendingWitnessTransaction(long[] id, Timestamp timestamp, int idLeader, long round, byte[] hashRound) {
		super(id, timestamp, idLeader, round);
		this.hashRound = hashRound;
	}
	
	public byte[] getHashRound() {
		return hashRound;
	}

	public void setHashRound(byte[] hashRound) {
		this.hashRound = hashRound;
	}

	@Override
	public int compareTo(PendingWitnessTransaction o) {
		return this.getTimestamp().compareTo( o.getTimestamp() );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(hashRound);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PendingWitnessTransaction other = (PendingWitnessTransaction) obj;
		if (!Arrays.equals(hashRound, other.hashRound))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return super.toString() + " [hashRound=" + Arrays.toString(hashRound) + "]";
		return super.toString();
	}

	
}
