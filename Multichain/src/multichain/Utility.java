/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multichain;

import java.io.*;
import java.math.*;
import java.util.*;

//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author edopc
 */
public class Utility {
    
public static String chainName = "test";
    
    public static String getChainName(){ return chainName; }
    
    public static String toHex(String arg) throws UnsupportedEncodingException {
        return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
    }
    
    public static String fromHexToString(String arg){
    	/*byte[] bytes = Hex.decodeHex(arg.toCharArray());
    	System.out.println( new String( bytes, "UTF-8" ) );
    	return new String( bytes, "UTF-8" );*/
        /*String str = "";
        StringBuilder strb = new StringBuilder();
        for(int i=0;i<arg.length();i+=2)
        {
            String s = arg.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            char c = (char) decimal;
			strb.append(c);
            System.out.println(":" + Integer.toString(decimal) );
        }       
        return strb.toString();*/
    	return "";
    }
    
    public static String getOutput(Process p) throws IOException{
        BufferedReader input = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
        BufferedReader error = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
        String line;
        String res = "";
        if(input.ready()){
            while( ( line = input.readLine() ) != null ){
                res += line + "\n";
            }
        }
        if(error.ready()){
            while( ( line = error.readLine() ) != null ){
                res += line + "\n";
            }
        }
        return( res );
    }
    
    public static void ListenForOperation(String key, String value) throws IOException, InterruptedException{
        System.out.println("Utility called");
        ArrayList<String> address = getAddresses();
        String s1 = key + ":" + value;
        String hex = toHex(s1);
        //String decode = fromHexToString(hex);
        System.out.println("s1: " + s1);
        System.out.println("hex: " + hex);
        //System.out.println("decode: " + decode);
        Transaction.sendTransactionWithMetadata(address.get(1), "asset1", "1", hex);
    }
    
    public static void getTotalBalances() throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " gettotalbalances";
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    public static void getPermissions() throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " listpermissions";
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    public static void getAllTransaction() throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " listwallettransactions";
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    public static String getNewAddress() throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " getnewaddress";
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        String output = getOutput(p);
        System.out.println( output );
        output = output.substring( output.lastIndexOf("}") + 3);
        return output.trim();
    }
    
    public static void grantPermissions(String add, String permissions) throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " grant " + add + " " + permissions;
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    public static void revokePermissions(String add, String permissions) throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " revoke " + add + " " + permissions;
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
    
    public static ArrayList<String> getAddresses() throws IOException, InterruptedException{
        
        String command = "multichain-cli " + chainName + " getaddresses";
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        
        String s = getOutput(p);
        System.out.println(s);
        
        String temp = s.substring( s.lastIndexOf("["));
        char[] c = temp.toCharArray();
        ArrayList<String> res = new ArrayList<>();
        
        boolean start = false;
        String addressTemp = "";
        
        for(int i = 0; i < c.length; i++){
            if(c[i] == '"'){
                if(start == true){
                    res.add(addressTemp);
                    addressTemp = "";
                    start = false;
                } 
                else{
                    i += 1;
                    start = true;
                }
            }
            if(start == true){ addressTemp += c[i]; }
        }
            return res;
    }
    
    public static void issueAsset(String address, String nameAsset, String units, String subdivision) throws IOException, InterruptedException{
        String command = "multichain-cli " + chainName + " issue " + address + " " + nameAsset + " " + units + " " + subdivision;
        ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
    }
}
