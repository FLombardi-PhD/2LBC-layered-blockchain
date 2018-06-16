import java.io.*;
import java.net.*;

import org.apache.http.client.utils.*;

public class Client {

	static String protocol = "http://";
	static String ipAddressMiner = "152.78.64.25";
	static int portServlet = 8080;
	static String hostServlet = "ClientInterface/Servlet";
	static int NUM_REQUESTS = 100;
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		String funziona = "";
		for(int i = 0; i < NUM_REQUESTS; i++){
			funziona = requestOperation("key: " + i, "value: " + i);
			System.out.println(funziona);
		}

	}
	
	public static String requestOperation(String key, String value) throws IOException, URISyntaxException{
		URIBuilder b = new URIBuilder();
		b.setPath(protocol + ipAddressMiner + ":" + portServlet + "/" + hostServlet);
		b.addParameter("method", "Set");
		b.addParameter("key", key);
		b.addParameter("value", value);
		System.out.println( b.build().toURL().toString() );
		URL url = new URL( b.build().toURL().toString() );
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		BufferedReader read = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
		String temp = "";
		String res = "";
		while( (temp = read.readLine()) != null ){
			res += temp + "\n";
		}
		return res;
	}

}
