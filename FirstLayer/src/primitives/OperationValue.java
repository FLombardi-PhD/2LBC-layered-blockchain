package primitives;

import java.io.Serializable;

public class OperationValue implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int key;
	private int value;
	
	public OperationValue(int k, int v){
		this.key = k;
		this.value = v;
	}
	
	public OperationValue(int k){
		this.key = k;
	}

	public int getKey() {
		return key;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + value;
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
		OperationValue other = (OperationValue) obj;
		if (key != other.key)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[key=" + key + ", value=" + value + "]";
	}
	
	
}
