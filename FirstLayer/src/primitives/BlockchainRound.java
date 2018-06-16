package primitives;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockchainRound {

	private CopyOnWriteArrayList< FinalizedTransaction > listTransaction;
	private FinalizedWitnessTransaction wt;
	private byte[] hashRound;
	
	public BlockchainRound(){
		this.listTransaction = new CopyOnWriteArrayList<>();
		this.wt = null;
		this.hashRound = null;
	}

	public CopyOnWriteArrayList<FinalizedTransaction> getListTransaction() {
		return listTransaction;
	}

	public void addTransaction(FinalizedTransaction t){
		this.getListTransaction().add(t);
	}
	
	public void setListTransaction(CopyOnWriteArrayList<FinalizedTransaction> listTransaction) {
		this.listTransaction = listTransaction;
	}

	public FinalizedWitnessTransaction getWt() {
		return wt;
	}

	public void setWt(FinalizedWitnessTransaction wt) {
		this.wt = wt;
	}

	public byte[] getHashRound() {
		return hashRound;
	}

	public void setHashRound(byte[] hashRound) {
		this.hashRound = hashRound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hashRound);
		result = prime * result + ((listTransaction == null) ? 0 : listTransaction.hashCode());
		result = prime * result + ((wt == null) ? 0 : wt.hashCode());
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
		BlockchainRound other = (BlockchainRound) obj;
		if (!Arrays.equals(hashRound, other.hashRound))
			return false;
		if (listTransaction == null) {
			if (other.listTransaction != null)
				return false;
		} else if (!listTransaction.equals(other.listTransaction))
			return false;
		if (wt == null) {
			if (other.wt != null)
				return false;
		} else if (!wt.equals(other.wt))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BlockchainRound [listTransaction=" + listTransaction + ", wt=" + wt + ", hashRound="
				+ Arrays.toString(hashRound) + "]";
	}
	
	
}
