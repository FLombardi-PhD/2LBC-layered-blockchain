package miner.jgroup;

import primitives.BlockchainRound;
import primitives.Buffer;
import primitives.Operation;
import primitives.PendingTransaction;
import primitives.PendingWitnessTransaction;
import util.Method;
import util.UtilParams;
import util.Utility;
import primitives.FinalizedTransaction;
import primitives.FinalizedWitnessTransaction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.jgroups.*;

import miner.rmi.MinerRMIImplementation;

public class MinerJGroup extends ReceiverAdapter implements Runnable {
	
	// BUFFER UTILIZZATO PER RICEVERE OPERAZIONI DALL'INTERFACCIA CHE COMUNICA CON IL CLIENT
	private Buffer buffer;
	MinerRMIImplementation mri;
	
	// CANALE JGROUP
	private JChannel channel;
	
	//STRUTTURA DATI PER VERIFICARE QUANDO UN LEADER PUO' SCRIVERE
	private Hashtable<Long, Integer> roundLeader;
	
	//STRUTTURA DATI PER MEMORIZZARE BLOCKCHAIN
	private Hashtable<Long, BlockchainRound> blockchain;
	
	//STRUTTURA DATI PER VERIFICARE SE LE OPERAZIONI MANDATE DAL LEADER SONO STATE RICEVUTE IN PRECEDENZA
	private CopyOnWriteArrayList<Operation> pendingOperation;
	private Hashtable<Long, CopyOnWriteArrayList<FinalizedTransaction>> pendingTransaction;
	private Hashtable<Long, CopyOnWriteArrayList<FinalizedWitnessTransaction>> pendingWitnessTransaction;
	private Hashtable<Integer, Object[]> mapKeyRound;
	
	//STRUTTURA DATI PER MANTENERE TRACCIA DI TRANSAZIONI CREATE DURANTE QUEL ROUND, PER CALCOLARE HASH
	private Hashtable<Long, CopyOnWriteArrayList<FinalizedTransaction> > transactionCreatedInThisRound;
	
	//STRUTTURA DATI PER ASPETTARME FIRMA PER UNA TRANSAZIONE O PER UNA WITNESS
	private Hashtable<PendingTransaction, CopyOnWriteArrayList<byte[]>> transactionsSignaturesMap;
	private Hashtable<PendingWitnessTransaction, CopyOnWriteArrayList<byte[]>> witnessSignaturesMap;

	// VEDERE QUANTI MINER MI HANNO MANDATO LA FIRMA
	private Hashtable<PendingTransaction, Integer> receivedSignaturesMap;
	
	//STRUTTURA DATI PER MANTENERE CHIAVI PUBBLICHE DEGLI ALTRI MINER
	private Hashtable<Address, PublicKey> minersKeys;	
	
	//VEDERE SE POSSO SCRIVERE TRANSAZIONI RICEVUTE SU BLOCKCHAIN
	private long canWriteRound;
	
	//PRINTER PER FILE DI THROUGHPUT
	CSVPrinter csvFilePrinter;

	private boolean leader;
	private int lastLeader;
	private int idMiner;
	private Long round;

	private long numIdTransaction = 0;

	private KeyPair keyPair;
	private String blockchainPath;
	private int lastWritten = -1;
	Logger logger;

	// COSTRUTTORE
	
	private long tempoPerScrivere;
	
	public MinerJGroup(){}

	public MinerJGroup(Buffer buffer, boolean l, int idMiner) {
		
		try {
			Object[] FILE_HEADER = {"idTransaction","date"};
			Path path = Paths.get( UtilParams.getPATH_THROUGHPUT_FILE() );
			if( !Files.exists(path) ){
				if( !new File( UtilParams.getPATH_THROUGHPUT_FILE() ).mkdirs() ){
					System.err.println("Creazione directory non andata a buon fine!");
				}
			}
			FileWriter fileWriter = new FileWriter( UtilParams.getPATH_THROUGHPUT_FILE() + UtilParams.getNAME_THROUGHPUT_FILE());
			CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
			this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			this.csvFilePrinter.printRecord(FILE_HEADER);
			this.csvFilePrinter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//INIZIALIZZO BLOCKCHAIN
		this.blockchain = new Hashtable<Long, BlockchainRound>();
		this.blockchain.put(1l, new BlockchainRound());
		this.pendingTransaction = new Hashtable<Long, CopyOnWriteArrayList<FinalizedTransaction>>();
		this.pendingTransaction.put( 1l, new CopyOnWriteArrayList<FinalizedTransaction>());
		this.pendingWitnessTransaction = new Hashtable<Long, CopyOnWriteArrayList<FinalizedWitnessTransaction>>();
		this.pendingWitnessTransaction.put( 1l, new CopyOnWriteArrayList<FinalizedWitnessTransaction>());
		
		this.mapKeyRound = new Hashtable<Integer, Object[]>();
		this.buffer = buffer;
		this.logger = Logger.getLogger( MinerJGroup.class );
		setIdMiner(idMiner);
		this.roundLeader = new Hashtable<Long, Integer>();
		this.roundLeader.put(1l, 1);
		setLastLeader(1);
		setLeader(l);
		setRound(1l);
		setNumIdTransaction(1);
		this.canWriteRound = 1l;

		this.pendingOperation = new CopyOnWriteArrayList<Operation>();
		
		this.transactionCreatedInThisRound = new Hashtable<Long, CopyOnWriteArrayList<FinalizedTransaction>>();
		this.transactionCreatedInThisRound.put( 1l, new CopyOnWriteArrayList<FinalizedTransaction>());
		
		this.transactionsSignaturesMap = new Hashtable<PendingTransaction, CopyOnWriteArrayList<byte[]>>();
		this.witnessSignaturesMap = new Hashtable<PendingWitnessTransaction, CopyOnWriteArrayList<byte[]>>();
		this.receivedSignaturesMap = new Hashtable<PendingTransaction, Integer>();
		this.minersKeys = new Hashtable<Address, PublicKey>();
		
		this.keyPair = Utility.getKeyPair();
		this.addLastWritten();

		this.blockchainPath = UtilParams.getBLOCKCHAIN_PATH_FILE() + this.getIdMiner() + ".txt";
		
		try {
			PrintWriter writer = new PrintWriter(this.blockchainPath, "UTF-8");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			start();
		} catch (Exception e) {
			System.err.println("Errore generico nella creazione del canale");
			e.printStackTrace();
		}
	}

	private void start() {
		try {
			this.channel = new JChannel();
		} catch (Exception e) {
			System.err.println("Errore nella creazione del canale");
			e.printStackTrace();
		}
		this.channel.setReceiver(this);
		try {
			channel.connect( UtilParams.getJGROUP_NAME_CHANNEL() );
		} catch (Exception e) {
			System.err.println("Errore nella connessione al canale");
			e.printStackTrace();
		}
		// eventLoop();
		// channel.close();
	}

	@Override
	public void run() {
		while (true) {
			Operation o = getBuffer().getOperation();
			this.checkForOperations( o );
		}
	}
	
	private synchronized void checkForOperations(Operation o){
		if( o.getMethod() == Method.SET ){
			if( isLeader() ){
				this.createTransaction(o);
			}
			else{
				this.pendingOperation.add(o);
			}
		}
		else{
			returnValue( o );
		}
	}
	
	private synchronized void returnValue( Operation o ){
		System.out.println( "Chiamato con operazione: " + o.getValue());
		int chiave = o.getValue().getKey();
		if( this.mapKeyRound.containsKey( chiave ) ){
			Long round = (Long) this.mapKeyRound.get( chiave )[1];
			long[] idTransaction = (long[]) this.mapKeyRound.get( chiave )[0];
			for( FinalizedTransaction t : this.blockchain.get(round).getListTransaction() ){
				if( Arrays.equals(t.getId(), idTransaction) ){
					this.mri.sendResponseToClient(t, o.getIdClient());
					break;
				}
			}
		}
	}

	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
		byte[] data = SerializationUtils.serialize(keyPair.getPublic());
		Message msg = new Message(null, data);
		try {
			channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void writePendingTransactionRound(long round){
		System.out.println("Metodo chiamato con round: " + round);
		if( this.pendingTransaction.containsKey(round)){
			for( FinalizedTransaction t : this.pendingTransaction.get(round) ){
				this.writeOnFile(t);
				if( t.getIdLeader() == this.idMiner )
					this.getMri().sendResponseToClient( t , t.getOperation().getIdClient() );
			}
			this.pendingTransaction.remove(round);
			this.writePendingWitnessTransactionRound(round);
		}
	}
	
	private synchronized void writePendingWitnessTransactionRound(long round){
		System.out.println("Metodo witness chiamato con round: " + round);
		if( this.pendingWitnessTransaction.containsKey(round)){
			for( FinalizedWitnessTransaction wt : this.pendingWitnessTransaction.get(round) ){
				this.writeOnFile(wt);
			}
			this.pendingWitnessTransaction.remove(round);
		}
	}
	
	@Override
	public void receive(Message msg) {
		try {
			//SIMULARE RITARDO DI RETE
			Thread.sleep(Utility.getRandom(UtilParams.getMIN_NETWORK_DELAY(), UtilParams.getMAX_NETWORK_DELAY()));
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		// SE IL MESSAGGIO LO MANDO IO, LO IGNORO
		if (msg.src() != channel.getAddress()) {
			
			Object o = SerializationUtils.deserialize(msg.getBuffer());
			//PRIMO IF UTILIZZATO PER LO SCAMBIO DI CHIAVI PUBBLICHE TRA MINER
			if (o.getClass().toString().equals(this.keyPair.getPublic().getClass().toString())) {
				PublicKey pk = (PublicKey) o;
				if (!minersKeys.containsKey(msg.getSrc())) {
					minersKeys.put(msg.getSrc(), pk);
					byte[] data = SerializationUtils.serialize(keyPair.getPublic());
					Message msg2 = new Message(null, data);
					try {
						channel.send(msg2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} 
			//HO RICEVUTO UNA TRANSAZIONE, NON UNA CHIAVE PUBBLICA
			else if(o.getClass().toString().equals(FinalizedTransaction.class.toString())){
				FinalizedTransaction t = SerializationUtils.deserialize(msg.getBuffer());
				PendingTransaction pt = new PendingTransaction(t.getId(), t.getTimestamp(), t.getIdLeader(), t.getRound(), t.getOperation());
				if( this.roundLeader.containsKey( t.getRound()) && this.roundLeader.get( t.getRound() ) == t.getIdLeader() ){
					//LA TRANSAZIONE E' STATA CREATA DA ME, CONTINUO A GESTIRLA IO
					if ( t.getIdLeader() == this.getIdMiner() ) {
						//AGGIUNGO FIRMA E CONTROLLO SE LE HO RICEVUTE TUTTE
						this.addSignAndSend(t);
					} else {
						//E' UNA TRANSAZIONE COMPLETA DI FIRME, LA POSSO SCRIVERE SU BLOCKCHAIN
						if (t.getSignatures().size() >= UtilParams.getREQUIRED_SIGNATURES()) {
							// AGGIUNGI A BLOCKCHAIN E ESEGUI OPERAZIONE ASSOCIATA
							if(this.canWriteRound >= t.getRound()){
								if( this.writeOnFile(t) ){
									this.removeTransactionFromPending(pt);
								}
								else{
									System.out.println("Scrittura su file non andata a buon fine");
								}
							}
							else{
								this.pendingTransaction.get(t.getRound()).add(t);
							}
							
						} else {
							// VERIFICALA, FIRMALA E MANDALA AL LEADER
							//this.writtenTransactionMap.put(pt, false);
							Address a = msg.getSrc();
							if (VerifyManager.verifyTransaction(t, minersKeys.get(a), buffer.getClientPublicKey(t.getOperation().getIdClient()))) {
								FinalizedTransaction temp;
								if( this.pendingOperation.contains( t.getOperation() ) ){
									temp = new FinalizedTransaction(t.getId(), t.getIdLeader(), t.getTimestamp(), t.getRound(), t.getOperation(), true);
								}else{
									temp = new FinalizedTransaction(t.getId(), t.getIdLeader(), t.getTimestamp(), t.getRound(), t.getOperation(), false);
								}
								// FIRMO TRANSAZIONE
								this.signTransaction(temp);
								// MANDO TRANSAZIONE FIRMATA AL LEADER
								this.sendTransaction(a, temp);
								//LA RIMUOVO DALLA PENDING
								if( this.pendingOperation.contains( t.getOperation() ) )
									this.pendingOperation.remove(t.getOperation());
							}
							//LA TRANSAZIONE NON E' CORRETTA, QUINDI LA FIRMO CON FALSE
							else{
								FinalizedTransaction temp = new FinalizedTransaction(t.getId(), t.getIdLeader(), t.getTimestamp(), t.getRound(), t.getOperation(), false);
								// FIRMO TRANSAZIONE
								this.signTransaction(temp);
								// MANDO TRANSAZIONE FIRMATA AL LEADER
								this.sendTransaction(a, temp);
								//LA RIMUOVO DALLA PENDING
								if( this.pendingOperation.contains( t.getOperation() ) )
									this.pendingOperation.remove( t.getOperation() );
							}
						}
					}
				}
			}
			//HO RICEVUTO UNA WITNESS TRANSACTION
			else{
				
				FinalizedWitnessTransaction wt = SerializationUtils.deserialize(msg.getBuffer());
				
				if ( wt.getIdLeader() == this.getIdMiner() ) {
					//AGGIUNGO FIRMA E CONTROLLO SE LE HO RICEVUTE TUTTE
					this.addSignAndSend(wt);
				}
				else{
					if (wt.getSignatures().size() >= UtilParams.getREQUIRED_SIGNATURES()) {
						// AGGIUNGI A BLOCKCHAIN E ESEGUI OPERAZIONE ASSOCIATA
						if(this.canWriteRound >= wt.getRound()){
							if( this.writeOnFile(wt) ){
								this.witnessSignaturesMap.remove(wt);
							}
							else{
								System.out.println("Scrittura su file non andata a buon fine");
							}
						}
						else{
							if( !this.pendingWitnessTransaction.containsKey(wt.getRound()) )
								this.pendingWitnessTransaction.put(wt.getRound(), new CopyOnWriteArrayList<FinalizedWitnessTransaction>());
							this.pendingWitnessTransaction.get(wt.getRound()).add(wt);
						}
					}
					else{
						Address a = msg.getSrc();
						FinalizedWitnessTransaction temp;
						//VERIFICO LA TRANSAZIONE
						if (VerifyManager.verifyWitnessTransaction(wt, minersKeys.get(a) ) ) {
							temp= new FinalizedWitnessTransaction(wt.getId(), wt.getIdLeader(), wt.getTimestamp(), wt.getRound(),wt.getHashRound(), true);
						}
						//LA TRANSAZIONE NON E' CORRETTA, QUINDI LA FIRMO CON FALSE
						else{
							temp= new FinalizedWitnessTransaction(wt.getId(), wt.getIdLeader(), wt.getTimestamp(), wt.getRound(),wt.getHashRound(), false);
						}
						// FIRMO TRANSAZIONE
						this.signWitnessTransaction(temp);
						// MANDO TRANSAZIONE FIRMATA AL LEADER
						this.sendWitnessTransaction(a, temp);
					}
				}
				
				this.blockchain.get(wt.getRound()).setWt(wt);
				this.blockchain.get(wt.getRound()).setHashRound(wt.getHashRound());
			}
		}
	}

	// AUXILIAR METHODS
	private synchronized void addSignAndSend(FinalizedTransaction t){
		
		PendingTransaction pt = new PendingTransaction(t.getId(), t.getTimestamp(), t.getIdLeader(), t.getRound(), t.getOperation());
		if (this.transactionsSignaturesMap.containsKey(pt)) {
			//AGGIUNGO FIRMA E MANDO TRANSAZIONE IN CASO DI ALMENO K FIRME RICEVUTE
			byte[] signClient = t.getSignatures().get(0);
			//AGGIUNGO FIRMA RICEVUTA ALLA TRANSAZIONE PENDING
			this.transactionsSignaturesMap.get(pt).add( signClient );
			// System.out.println( this.mappa.size() );
			
			//SE HO RICEVUTO TUTTE LE FIRME
			if (this.transactionsSignaturesMap.get(pt).size() >= UtilParams.getREQUIRED_SIGNATURES()) {
				//CREO LA TRANSAZIONE FINALIZZATA
				FinalizedTransaction finalized = new FinalizedTransaction(pt, true, this.transactionsSignaturesMap.get(pt));
				//MANDO IN BROADCAST LA TRANSAZIONE FINALIZZATA
				this.sendTransaction(null, finalized);
				
				try {
					long finalizedRound = finalized.getRound();
					if( this.canWriteRound >= finalizedRound){
						if( this.pendingTransaction.containsKey(finalizedRound) && !this.pendingTransaction.get(finalizedRound).isEmpty()){
							this.writePendingTransactionRound(finalizedRound);
							if( this.pendingWitnessTransaction.containsKey(finalizedRound) && !this.pendingWitnessTransaction.get(finalizedRound).isEmpty() )
								this.writePendingWitnessTransactionRound(finalizedRound);
						}
						if( this.writeOnFile(finalized) ){
							this.getMri().sendResponseToClient( finalized, finalized.getOperation().getIdClient() );
							this.removeTransactionFromPending(pt);
						}
						else{
							System.err.println("Errore imprevisto di scrittura su file!");
						}
					}
					else{
						//System.out.println("transazione non scritta: " + Arrays.toString(finalized.getId()) + " : " + this.canWriteRound);
						if( !this.pendingTransaction.containsKey(finalized.getRound()))
							this.pendingTransaction.put(finalized.getRound(), new CopyOnWriteArrayList<FinalizedTransaction>());
						this.pendingTransaction.get(finalized.getRound()).add(finalized);
					}
					
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.err.println("NullPointerException");
				}
			}
			
		}		
	}
	
	private synchronized void addSignAndSend( FinalizedWitnessTransaction wt ){
		
		PendingWitnessTransaction pt = new PendingWitnessTransaction(wt.getId(), wt.getTimestamp(), wt.getIdLeader(), wt.getRound(), wt.getHashRound());
		if (this.witnessSignaturesMap.containsKey(pt)) {

			byte[] signClient = wt.getSignatures().get(0);
			//AGGIUNGO FIRMA RICEVUTA ALLA TRANSAZIONE PENDING
			this.witnessSignaturesMap.get(pt).add( signClient );
			
			//SE HO RICEVUTO TUTTE LE FIRME
			if (this.witnessSignaturesMap.get(pt).size() >= UtilParams.getREQUIRED_SIGNATURES()) {
				//CREO LA TRANSAZIONE FINALIZZATA
				FinalizedWitnessTransaction finalized = new FinalizedWitnessTransaction(pt, this.witnessSignaturesMap.get(pt));
				
				//MANDO IN BROADCAST LA TRANSAZIONE FINALIZZATA
				this.sendWitnessTransaction(null, finalized);
				
				try {
					if(this.canWriteRound >= finalized.getRound()){
						if( this.writeOnFile(finalized) ){
							this.witnessSignaturesMap.remove(pt);
						}
						else{
							System.err.println("Errore imprevisto di scrittura su file!");
						}
					}
					else{
						if( !this.pendingWitnessTransaction.containsKey(finalized.getRound()) )
							this.pendingWitnessTransaction.put(finalized.getRound(), new CopyOnWriteArrayList<FinalizedWitnessTransaction>());
						this.pendingWitnessTransaction.get(finalized.getRound()).add(finalized);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.err.println("NullPointerException");
				}
			}
			
		}		
	}
	
	private synchronized void signTransaction(FinalizedTransaction t) {
		byte[] dataSerialized = SerializationUtils.serialize(t);
		byte[] signature = Utility.signObject(this.keyPair.getPrivate(), dataSerialized);
		t.setSignatures(new CopyOnWriteArrayList<byte[]>());
		t.getSignatures().add(signature);
	}
	
	private synchronized void signWitnessTransaction(FinalizedWitnessTransaction wt) {
		byte[] dataSerialized = SerializationUtils.serialize(wt);
		byte[] signature = Utility.signObject(this.keyPair.getPrivate(), dataSerialized);
		wt.setSignatures(new CopyOnWriteArrayList<byte[]>());
		wt.getSignatures().add(signature);
	}

	private synchronized void sendTransaction(Address address, FinalizedTransaction t) {
		try {
			// MANDO LA TRANSAZIONE A TUTTI PER ESSERE FIRMATA
			byte[] dataSerialized = SerializationUtils.serialize(t);
			Message msg = new Message(address, dataSerialized);
			channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void sendWitnessTransaction(Address address, FinalizedWitnessTransaction wt) {
		try {
			// MANDO LA TRANSAZIONE A TUTTI PER ESSERE FIRMATA
			byte[] dataSerialized = SerializationUtils.serialize(wt);
			Message msg = new Message(address, dataSerialized);
			channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized boolean writeOnFile(FinalizedTransaction t) {
		if(t.getIdLeader() == this.getIdMiner()){
			try {
				FileWriter fileWriter = new FileWriter( UtilParams.getPATH_THROUGHPUT_FILE() + UtilParams.getNAME_THROUGHPUT_FILE(), true);
				CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
				this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
				ZoneId zone1 = ZoneId.of("Europe/Rome");
				LocalTime lt = LocalTime.now(zone1);
				Object[] array = { Arrays.toString(t.getId()), lt.toString() };
				this.csvFilePrinter.printRecord(array);
				this.csvFilePrinter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			this.addLastWritten();
			Files.write(Paths.get(this.blockchainPath), (t.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
			this.blockchain.get( t.getRound() ).addTransaction(t);
			Object[] value = {t.getId(), t.getRound()};
			this.mapKeyRound.put(t.getOperation().getValue().getKey(), value);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private synchronized boolean writeOnFile(FinalizedWitnessTransaction wt) {
		try {
			if( isLeader() )
				logger.info( "Tempo dopo il quale posso scrivere: " + ( (System.currentTimeMillis() - this.tempoPerScrivere) / 1000.0 ));
			Files.write(Paths.get(this.blockchainPath), (wt.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
			this.canWriteRound = wt.getRound()+1;
			if( this.pendingTransaction.containsKey(wt.getRound()+1) && this.pendingTransaction.get(wt.getRound()+1).size() >= 1)
				this.writePendingTransactionRound(wt.getRound()+1);
			else if( this.pendingWitnessTransaction.containsKey(wt.getRound()+1) && this.pendingWitnessTransaction.get(wt.getRound()+1).size() >= 1)
				this.writePendingWitnessTransactionRound(wt.getRound()+1);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;		
	}

	private synchronized void removeTransactionFromPending(PendingTransaction pt) {
		this.transactionsSignaturesMap.remove(pt);
		this.receivedSignaturesMap.remove(pt);
		this.pendingOperation.remove(pt.getOperation());
	}

	private synchronized void createTransaction(Operation o) {
		boolean res = VerifyManager.verifyOperation(o, buffer.getClientPublicKey(o.getIdClient()));
		if (res) {
			// CREO TRANSAZIONE
			Timestamp time = new Timestamp(System.currentTimeMillis());
			long[] id = { getRound(), this.getNumIdTransaction() };
			//System.out.println(Arrays.toString(id) );
			FinalizedTransaction t = new FinalizedTransaction( id, getIdMiner(), time, getRound(), o, true);
			this.transactionCreatedInThisRound.get( t.getRound() ).add(t);
			this.addNumIdTransaction();
			// FIRMO TRANSAZIONE E AGGIUNGO FIRMA AD ESSA
			this.signTransaction(t);
			// METTO TRANSAZIONE NELLA MIA LISTA DI PENDING
			CopyOnWriteArrayList<byte[]> sign = new CopyOnWriteArrayList<byte[]>();
			sign.add(t.getSignatures().get(0));
			PendingTransaction pt = new PendingTransaction(t.getId(), t.getTimestamp(), t.getIdLeader(), t.getRound(), t.getOperation());
			this.transactionsSignaturesMap.put(pt, sign);
			//this.writtenTransactionMap.put(pt, false);
			this.receivedSignaturesMap.put(pt, 1);
			this.sendTransaction(null, t);
		}
		else{
			//SE L'OPERAZIONE NON E' FIRMATA CORRETTAMENTE, LA IGNORA
		}
	}
	
	private synchronized byte[] createHashRound( long round ){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for( FinalizedTransaction tx : this.transactionCreatedInThisRound.get(round)){
			try {
				outputStream.write( SerializationUtils.serialize(tx));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.transactionCreatedInThisRound.remove(round);
		return outputStream.toByteArray();
	}

	public synchronized void cambioLeader() {
		System.out.println("Cambio Leader");
		if(this.isLeader()){
			byte[] hashRound = this.createHashRound( getRound() );
			long[] id = { getRound(), this.getNumIdTransaction() };
			Timestamp timestamp = new Timestamp( System.currentTimeMillis() );
			FinalizedWitnessTransaction wt = new FinalizedWitnessTransaction(id, getIdMiner(),timestamp, this.getRound(), hashRound, true);
			//FIRMA WITNESS
			this.signWitnessTransaction(wt);
			//AGGIUNGI ALLA LISTA DI PENDING PER ASPETTARE FIRME
			PendingWitnessTransaction pwt = new PendingWitnessTransaction(wt.getId(), wt.getTimestamp(), wt.getIdLeader(), wt.getRound(), wt.getHashRound());
			CopyOnWriteArrayList<byte[]> cowl = new CopyOnWriteArrayList<byte[]>();
			cowl.add(wt.getSignatures().get(0));
			this.witnessSignaturesMap.put( pwt, cowl );
			//MANDA IN BROADCAST
			this.sendWitnessTransaction( null, wt );
		}
		//SETTA NUOVO LEADER
		if (getLastLeader() == UtilParams.getNUM_MINERS())
			setLastLeader(1);
		else
			setLastLeader(getLastLeader() + 1);
		if (getLastLeader() == getIdMiner()){
			setLeader(true);
			this.tempoPerScrivere = System.currentTimeMillis();
			//logger.info( "Taglia pending transaction: " + this.pendingTransaction.get(getRound()).size() );
		}
		else
			setLeader(false);
		this.roundLeader.put(getRound() + 1l, getLastLeader());
		//AUMENTA NUMERO DI ROUND E ID TRANSAZIONE
		this.setRound(getRound() + 1l);
		this.setNumIdTransaction( 1 );
		
		this.blockchain.put( getRound(), new BlockchainRound());
		this.transactionCreatedInThisRound.put( getRound(), new CopyOnWriteArrayList<FinalizedTransaction>());
		this.pendingTransaction.put( getRound(), new CopyOnWriteArrayList<FinalizedTransaction>());
	}

	// GETTERS AND SETTERS

	private synchronized Buffer getBuffer() {
		return buffer;
	}

	private int getIdMiner() {
		return idMiner;
	}

	private void setIdMiner(int idMiner) {
		this.idMiner = idMiner;
	}

	private int getLastLeader() {
		return lastLeader;
	}

	private void setLastLeader(int lastLeader) {
		this.lastLeader = lastLeader;
	}

	private boolean isLeader() {
		return leader;
	}

	private void setLeader(boolean leader) {
		this.leader = leader;
	}

	private long getRound() {
		return round;
	}

	private void setRound(long l) {
		this.round = l;
	}

	public MinerRMIImplementation getMri() {
		return mri;
	}

	public void setMri(MinerRMIImplementation mri) {
		this.mri = mri;
	}
	
	private synchronized void setNumIdTransaction(int numIdTransaction) {
		this.numIdTransaction = numIdTransaction;
	}

	private synchronized long getNumIdTransaction() {
		return numIdTransaction;
	}

	private synchronized void addNumIdTransaction() {
		this.numIdTransaction = this.getNumIdTransaction() + 1;
	}
	
	private synchronized int getLastWritten() {
		return lastWritten;
	}

	private synchronized void addLastWritten() {
		this.lastWritten = getLastWritten() + 1;
	}
}
