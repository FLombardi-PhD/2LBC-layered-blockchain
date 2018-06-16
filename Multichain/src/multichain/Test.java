package multichain;

import java.io.IOException;
import java.util.*;
import static multichain.Utility.*;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException{
		// TODO Auto-generated method stub
		/*int dec = 99;
		char c = (char) dec;
		System.out.println(c);
		
		StringBuilder strb = new StringBuilder();
		strb.append(c);
		strb.append(c);
		System.out.println(strb.toString());*/
		
		String command = "java -Djava.net.preferIPv4Stack=true org.jgroups.demos.Draw";
		ProcessBuilder processBuilder = new ProcessBuilder( new ArrayList<>(Arrays.asList(new String[] {"/bin/bash", "-c", command})) );
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println( getOutput(p) );
	}

}
