/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multichain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import static multichain.Utility.*;

/**
 *
 * @author edopc
 */
public class Transaction{
	
	public static void sendTransaction(String address, String nameAsset, String units) throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " sendassettoaddress " + address + " " + nameAsset + " " + units;
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    public static void sendTransactionWithMetadata(String address, String nameAsset, String units, String metadata) throws IOException, InterruptedException{
        String asset = "'{\"" + nameAsset + "\":" + units + "}'";
        String command = "multichain-cli " + chainName + " sendwithmetadata " + address + " " + asset + " " + metadata;
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    
    
    public static void main(String[] args) throws IOException, InterruptedException{
        
    	ArrayList<String> address = getAddresses();
        
        
        issueAsset(address.get(0), "asset5", "1000", "0.01");
        
        //sendTransaction(address.get(0), "asset5", "100");
        
        //sendTransactionWithMetadata(address.get(0), "asset5", "100", "ciao");
        
        //getTotalBalances();
        
        //getAllTransaction();
        
        getPermissions();
        
    }
}
