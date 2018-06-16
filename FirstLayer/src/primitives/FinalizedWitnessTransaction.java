package primitives;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;

public class FinalizedWitnessTransaction extends PendingWitnessTransaction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean ACK;
	private CopyOnWriteArrayList< byte[] > signatures;
	
	public FinalizedWitnessTransaction(long[] id, int leader, Timestamp timestamp, long round, byte[] hash, boolean ack) {
		super(id, timestamp, leader, round, hash);
		this.setACK(ack);
		this.signatures = null;
	}
	
	public FinalizedWitnessTransaction(long[] id, int leader, Timestamp timestamp, long round, byte[] hash, boolean ack, CopyOnWriteArrayList<byte[]> signatures) {
		super(id, timestamp, leader, round, hash);
		this.setACK(ack);
		this.signatures = signatures;
	}
	
	public FinalizedWitnessTransaction(PendingWitnessTransaction pt, CopyOnWriteArrayList<byte[]> hashSet) {
		super(pt.getId(), pt.getTimestamp(), pt.getIdLeader(), pt.getRound(), pt.getHashRound());
		this.setACK(true);
		this.signatures = hashSet;
	}

	public CopyOnWriteArrayList<byte[]> getSignatures() {
		return signatures;
	}

	public void setSignatures(CopyOnWriteArrayList<byte[]> signatures) {
		this.signatures = signatures;
	}

	public boolean isACK() {
		return ACK;
	}

	public void setACK(boolean aCK) {
		ACK = aCK;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (ACK ? 1231 : 1237);
		result = prime * result + ((signatures == null) ? 0 : signatures.hashCode());
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
		FinalizedWitnessTransaction other = (FinalizedWitnessTransaction) obj;
		if (ACK != other.ACK)
			return false;
		if (signatures == null) {
			if (other.signatures != null)
				return false;
		} else if (!signatures.equals(other.signatures))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return super.toString() + " [ACK=" + ACK + ", signatures=" + signatures + "]";
		return super.toString() + " [signatures=" + signatures + "]";
	}
	
	
	
}
