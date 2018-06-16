package util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class PrepareFileLatency {
	
	private static TreeMap<String, LinkedList<Double>> allLatencies;
	private static TreeMap<Integer, Double> finalLatencies;
	private static int CAMPIONAMENTO = 10; //num of seconds

	public static void main(String[] args) {
		allLatencies = new TreeMap< String, LinkedList<Double> >();
		finalLatencies = new TreeMap<Integer, Double>();
		try {
			FileReader latency = new FileReader( UtilParams.getPATH_LATENCY_FILE() + UtilParams.getNAME_LATENCY_FILE() );
			CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
			CSVParser csvFileParser = new CSVParser(latency, csvFileFormat);
			List<CSVRecord> allRecords = csvFileParser.getRecords();
			for(int i = 1; i < allRecords.size(); i++){
				if(allRecords.get(i).size() >= 2){
					String key = allRecords.get(i).get(2).toString();
					if( key.length() < 8)
						key += ":00";
					key = key.substring(0, 8);
					if( !allLatencies.containsKey(key))
						allLatencies.put(key, new LinkedList<Double>());
					allLatencies.get(key).add( Double.parseDouble(allRecords.get(i).get(1)));
				}
			}
			csvFileParser.close();
			int id = 1;
			int counter = 0;
			double num = 0.0;
			double den = 0.0;
			for(String s : allLatencies.keySet()){
				counter += 1;
				den += (double) allLatencies.get(s).size();
				for( Double d : allLatencies.get(s) ){
					num += d;
				}
				if(counter == CAMPIONAMENTO){
					finalLatencies.put(id*CAMPIONAMENTO, (num/den));
					id++;
					num = 0.0;
					den = 0.0;
					counter = 0;
				}
			}
			if(counter != 0){
				finalLatencies.put(id*CAMPIONAMENTO, (num/den));
			}
			
			FileWriter fileWriter = new FileWriter( UtilParams.getFINAL_LATENCY_FILE());
			CSVFormat csvFileFormat2 = CSVFormat.EXCEL.withDelimiter(';');
			CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat2);
			
			for( Integer i : finalLatencies.keySet() ){
				//System.out.println(finalLatencies.get(i).toString());
				Object[] array = { i.toString(), finalLatencies.get(i).toString().replace(".", ",")};
				csvFilePrinter.printRecord(array);
				//break;
			}
			csvFilePrinter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
