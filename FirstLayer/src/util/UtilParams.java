package util;

public class UtilParams {

	private static String JGROUP_NAME_CHANNEL = "JGroupChannel";
	private static String RMI_NAME_CHANNEL_MINER = "RMIChannelMiner";
	private static String RMI_NAME_CHANNEL_CLIENT = "RMIChannelClient";
	private static String IP_ADDRESS = "localhost";
	
	//WHAT IS THE RATE(OPERATIONS PER SECOND) OF THE TEST?
	private static int RATE = 100;
	
	private static int ROUND_TIME = 10; //IN SECONDS
	
	private static String BLOCKCHAIN_PATH_FILE = "./blockchain/BLOCKCHAIN";
	
	private static String PATH_LATENCY_FILE = "./blockchain/latency/rate"+RATE+"/";
	private static String NAME_LATENCY_FILE = "LATENCY-RATE" + RATE + "ROUND" + ROUND_TIME + ".csv";
	
	private static String LATENCY_GET_FILE = "./blockchain/latency/GET-LATENCY.csv";
	
	private static String PATH_THROUGHPUT_FILE = "./blockchain/throughput/rate"+RATE+"/";
	private static String NAME_THROUGHPUT_FILE = "THROUGHPUT-RATE" + RATE + "ROUND" + ROUND_TIME + ".csv";
	

	public static String getLATENCY_GET_FILE() {
		return LATENCY_GET_FILE;
	}

	public static String getPATH_THROUGHPUT_FILE() {
		return PATH_THROUGHPUT_FILE;
	}

	public static String getNAME_THROUGHPUT_FILE() {
		return NAME_THROUGHPUT_FILE;
	}

	private static String FINAL_LATENCY_FILE = "./blockchain/latency/rate"+RATE+"/FINAL_LATENCY-RATE" + RATE + "ROUND" + ROUND_TIME + ".csv";
	private static String FINAL_THROUGHPUT_FILE = "./blockchain/throughput/rate"+RATE+"/FINAL_THROUGHPUT-RATE" + RATE + "ROUND" + ROUND_TIME + ".csv";
	
	private static int REQUIRED_SIGNATURES = 3;
	private static int NUM_MINERS = 3;
	
	//LOWER AND UPPER BOUND OF NETWORK DELAY
	private static int MIN_NETWORK_DELAY = 5;
	private static int MAX_NETWORK_DELAY = 20;
	
	//HOW LONG IS THE TEST?
	private static int MINUTES = 5;
	private static int SECONDS = 0;
	
	public static String getFINAL_LATENCY_FILE() {
		return FINAL_LATENCY_FILE;
	}

	public static String getFINAL_THROUGHPUT_FILE() {
		return FINAL_THROUGHPUT_FILE;
	}

	public static int getROUND_TIME() {
		return ROUND_TIME;
	}
	
	public static int getDURATION_TEST_TIME() {
		if( getMINUTES() != 0 ){
			if( getSECONDS() != 0 ){
				return ( (getMINUTES() * 60 * 1000) + (getSECONDS() * 1000) );
			}else{
				return (getMINUTES() * 60 * 1000);
			}
		}
		else{
			if( getSECONDS() != 0 ){
				return ( (getSECONDS() * 1000) );
			}else{
				return 0;
			}
		}
	}
	public static int getMINUTES() {
		return MINUTES;
	}
	public static int getSECONDS() {
		return SECONDS;
	}
	public static int getRATE() {
		return RATE;
	}
	public static int getMIN_NETWORK_DELAY() {
		return MIN_NETWORK_DELAY;
	}
	public static int getMAX_NETWORK_DELAY() {
		return MAX_NETWORK_DELAY;
	}
	public static int getREQUIRED_SIGNATURES() {
		return REQUIRED_SIGNATURES;
	}
	public static String getJGROUP_NAME_CHANNEL() {
		return JGROUP_NAME_CHANNEL;
	}
	public static String getRMI_NAME_CHANNEL_MINER() {
		return RMI_NAME_CHANNEL_MINER;
	}
	public static String getIP_ADDRESS() {
		return IP_ADDRESS;
	}
	public static String getRMI_NAME_CHANNEL_CLIENT() {
		return RMI_NAME_CHANNEL_CLIENT;
	}
	public static String getBLOCKCHAIN_PATH_FILE() {
		return BLOCKCHAIN_PATH_FILE;
	}
	public static int getNUM_MINERS() {
		return NUM_MINERS;
	}

	public static String getPATH_LATENCY_FILE() {
		return PATH_LATENCY_FILE;
	}

	public static String getNAME_LATENCY_FILE() {
		return NAME_LATENCY_FILE;
	}

}
