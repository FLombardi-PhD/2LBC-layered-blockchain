/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multichain;

import java.io.*;
import java.util.*;

public class StartMultichain extends Utility{
    
	public static void createMultichain( String chainName ) throws IOException, InterruptedException{
        String command = "multichain-util create " + chainName;
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
        startMultichain( chainName );
    }
    
    public static void startMultichain( String chainName ) throws IOException, InterruptedException{
        String command = "multichaind " + chainName +  " -daemon";
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        
    	ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", "multichain-cli " + chainName + " stop"})) );
        Process p = processBuilder.start();
        p.waitFor();
        
        processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", "rm -r ~/.multichain/" + chainName})) );
        p = processBuilder.start();
        p.waitFor();
        
        createMultichain( chainName );
        //startMultichain( bw, chainName );
        
        /*
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( p.getOutputStream() ) );
        bw.write("multichain-cli " + chainName + " stop");
        bw.newLine();
        bw.flush();
        
        bw.write("rm -r ~/.multichain/" + chainName);
        bw.newLine();
        bw.flush();
        
        bw.write("exit");
        bw.newLine();
        bw.flush();*/
        
        /*processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash"})) );
        p = processBuilder.start();
        bw = new BufferedWriter( new OutputStreamWriter( p.getOutputStream() ) );*/
        
        //createMultichain( chainName);
        //startMultichain( bw, chainName );
        
        /*bw.write("exit");
        bw.newLine();
        bw.flush();
        
        System.out.println( getOutput(p) );*/
    }
    
}
