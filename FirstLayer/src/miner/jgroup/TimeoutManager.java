package miner.jgroup;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import miner.rmi.MinerRMIInterfaceCambioLeader;
import util.UtilParams;
public class TimeoutManager implements Runnable{

	int i = 1;
	ArrayList<MinerRMIInterfaceCambioLeader> miList = new ArrayList<MinerRMIInterfaceCambioLeader>();
	int fp, lp;
	int timeout; 
	
	public TimeoutManager(int firstPort, int lastPort, int frequencyChangeLeader){
		this.fp = firstPort;
		this.lp = lastPort;
		this.timeout = frequencyChangeLeader;
	}
	
	public synchronized int getTimeout() {
		return timeout;
	}

	public synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		try {
			MinerRMIInterfaceCambioLeader mi;
			for(int port = fp; port <= lp; port++){
				Registry registry = LocateRegistry.getRegistry(UtilParams.getIP_ADDRESS(), port);
				mi = (MinerRMIInterfaceCambioLeader) registry.lookup( UtilParams.getRMI_NAME_CHANNEL_MINER() + port );
				this.miList.add( mi );
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		while(true){
			try {
				Thread.sleep( this.getTimeout() );
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for(MinerRMIInterfaceCambioLeader mri: miList){
				try {
					mri.sendCambioLeader();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public String toString() {
		return "TimeoutManager [i=" + i + ", miList=" + miList + ", fp=" + fp + ", lp=" + lp + ", timeout=" + timeout
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fp;
		result = prime * result + i;
		result = prime * result + lp;
		result = prime * result + ((miList == null) ? 0 : miList.hashCode());
		result = prime * result + timeout;
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
		TimeoutManager other = (TimeoutManager) obj;
		if (fp != other.fp)
			return false;
		if (i != other.i)
			return false;
		if (lp != other.lp)
			return false;
		if (miList == null) {
			if (other.miList != null)
				return false;
		} else if (!miList.equals(other.miList))
			return false;
		if (timeout != other.timeout)
			return false;
		return true;
	}

}
