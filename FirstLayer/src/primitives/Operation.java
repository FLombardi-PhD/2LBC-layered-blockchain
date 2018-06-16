package primitives;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.commons.lang3.SerializationUtils;

import util.Method;

public class Operation implements Serializable, Comparable<Operation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Timestamp id;
	private int idClient;
	private Method method;
	private OperationValue value;
	private byte[] sign;
	
	
	public Operation(Timestamp i, Method m, int c){
		this.setId(i);
		this.setMethod(m);
		this.setIdClient(c);
	}
	
	public Operation(Timestamp i, Method m, OperationValue v, int c){
		this.setId(i);
		this.setMethod(m);
		this.setValue(v);
		this.setIdClient(c);
	}
	
	public int getIdClient() {
		return idClient;
	}

	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}

	public OperationValue getValue() {
		return value;
	}

	public void setValue(OperationValue value) {
		this.value = value;
	}

	public Operation(Timestamp i, Method m, OperationValue v, byte[] s, int c){
		this.setId(i);
		this.setMethod(m);
		this.setValue(v);
		this.setSign(s);
		this.setIdClient(c);
	}

	@Override
	public String toString() {
		return "Operation [id=" + id + ", idClient=" + idClient + ", method=" + method + ", value=" + value + "]";
	}

	public Timestamp getId() {
		return id;
	}

	public void setId(Timestamp id) {
		this.id = id;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public byte[] getSign() {
		return sign;
	}

	public void setSign(byte[] sign) {
		this.sign = sign;
	}

	@Override
	public int compareTo(Operation o) {
		if( this.getId().getTime() == o.getId().getTime()){
			int hashThis =  Arrays.hashCode(SerializationUtils.serialize(this));
			int hashOther = Arrays.hashCode(SerializationUtils.serialize(o));
			if( hashThis < hashOther )
				return -1;
			else if( hashThis > hashOther )
				return 1;
			else
				return 0;
		}
		else{
			return this.getId().compareTo(o.getId());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + idClient;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + Arrays.hashCode(sign);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Operation other = (Operation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idClient != other.idClient)
			return false;
		if (method != other.method)
			return false;
		if (!Arrays.equals(sign, other.sign))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
	
}
