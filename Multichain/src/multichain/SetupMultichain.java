package multichain;

import java.io.IOException;
import java.util.ArrayList;
import static multichain.Utility.*;

public class SetupMultichain{

	public static void main(String[] args) throws IOException, InterruptedException {
		String add = getNewAddress();
        ArrayList<String> address = getAddresses();

        grantPermissions(add, "send,receive,issue");
        getPermissions();
        issueAsset(address.get(0), "asset1", "1000000000000", "0.01");
        issueAsset(address.get(1), "asset2", "5000000000", "0.01");
        
        revokePermissions(add, "issue");
        getPermissions();
        getTotalBalances();
	}

}
