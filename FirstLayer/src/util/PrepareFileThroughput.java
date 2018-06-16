package util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class PrepareFileThroughput {

	private static TreeMap<String, LinkedList<Integer>> allThroughputs;
	private static TreeMap<Integer, Integer> finalThroughputs;
	private static int CAMPIONAMENTO = 1; //num of seconds
		
	public static void main(String[] args) {
		allThroughputs = new TreeMap< String, LinkedList<Integer> >();
		finalThroughputs = new TreeMap<Integer, Integer>();
		try {
			FileReader latency = new FileReader( UtilParams.getPATH_THROUGHPUT_FILE() + UtilParams.getNAME_THROUGHPUT_FILE() );
			CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
			CSVParser csvFileParser = new CSVParser(latency, csvFileFormat);
			List<CSVRecord> allRecords = csvFileParser.getRecords();
			for(int i = 1; i < allRecords.size(); i++){
				try{
					String key = allRecords.get(i).get(1).toString();
					if( key.length() < 8){
						key += ":00";
					}
					key = key.substring(0, 8);
					if( !allThroughputs.containsKey(key)){
						allThroughputs.put(key, new LinkedList<Integer>());
					}
					allThroughputs.get(key).add( 1 );
				}
				catch(StringIndexOutOfBoundsException e){
					System.out.println(allRecords.get(i).get(1).toString());
					System.out.println(allRecords.get(i).get(1).toString().length());
				}
				
			}
			csvFileParser.close();
			int id = 1;
			int counter = 0;
			int num = 0;
			for(String s : allThroughputs.keySet()){
				if(allThroughputs.get(s).size() > 50)
					System.out.println( s );
				counter += 1;
				
				for( Integer d : allThroughputs.get(s) ){
					num += d;
				}
				
				if(counter == CAMPIONAMENTO){
					finalThroughputs.put(id*CAMPIONAMENTO, num/CAMPIONAMENTO);
					id++;
					num = 0;
					counter = 0;
				}
			}
			if(counter != 0){
				finalThroughputs.put(id*CAMPIONAMENTO, num/CAMPIONAMENTO);
			}
				
			FileWriter fileWriter = new FileWriter( UtilParams.getFINAL_THROUGHPUT_FILE());
			CSVFormat csvFileFormat2 = CSVFormat.EXCEL.withDelimiter(';');
			CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat2);
				
			for( Integer i : finalThroughputs.keySet() ){
				Object[] array = { i.toString(), finalThroughputs.get(i).toString()};
				csvFilePrinter.printRecord(array);
			}
			csvFilePrinter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
