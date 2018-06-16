package primitives;

import java.security.PublicKey;
import java.util.LinkedList;
import java.util.TreeMap;

public class Buffer {
	
	LinkedList<Operation> coda;
	int maxSize;
	PublicKey client;
	TreeMap< Integer, PublicKey > keyMap;

	public Buffer(int maxSize){
		this.coda = new LinkedList<Operation>();
		this.maxSize = maxSize;
		this.keyMap = new TreeMap<Integer, PublicKey>();
	}
	
	public boolean isEmpty(){
		return coda.isEmpty();
	}

	public boolean isFull(){
		return coda.size() >= maxSize;
	}
	
	public synchronized void addOperation(Operation op){
		while( isFull() ){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		coda.addLast(op);
		notifyAll();
	}
	
	public synchronized Operation getOperation(){
		while( isEmpty() ){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Operation res = coda.removeFirst();
		notifyAll();
		return res;
	}
	
	public synchronized void setClientPublicKey( Integer c, PublicKey pk ){
		this.keyMap.put(c, pk);
	}
	
	public synchronized PublicKey getClientPublicKey( Integer c ){
		return this.keyMap.get(c);
	}
	
	public int getSizeBuffer(){
		return coda.size();
	}
}
