package primitives;

import java.sql.Timestamp;

public class PendingTransaction extends Transaction implements Comparable<PendingTransaction>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Operation operation;

	public PendingTransaction(long[] id, Timestamp timestamp, int leaderId, long round, Operation op) {
		super(id, timestamp, leaderId, round);
		this.operation = op;
	}
	
	@Override
	public int compareTo(PendingTransaction pt) {
		Operation o1 = this.getOperation();
		Operation o2 = pt.getOperation();
		if( o1.compareTo(o2) < 0){
			return -1;
		}
		else if( o1.compareTo(o2) > 0 ){
			return 1;
		}
		else{
			return this.getTimestamp().compareTo(pt.getTimestamp());
		}
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
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
		PendingTransaction other = (PendingTransaction) obj;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return super.toString() + " [operation=" + operation + "]";
		return super.toString() + " [operation=" + operation.getValue() + "]";
	}
	
	

}
