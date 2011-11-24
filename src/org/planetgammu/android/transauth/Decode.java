package org.planetgammu.android.transauth;

//import android.util.Base64;

public class Decode {
	public static String demoKey = "794d120fcffb6510d12d579379761168";
	//public static String demoMessage = "VEFNAgAwMDLkTsF4IBzqMTbpnPMLX+Ix";
	public static String demoMessage = "KRAU2AQAGAYDFZCOYF4CAHHKGE3OTHHTBNP6EMI";


	public static long[] doit(String message, String key) throws Exception {
		byte[] messageBinary, keyBinary;
		String keyid;

		//messageBinary = Base64.decode(message, Base64.DEFAULT);
		messageBinary = Base32String.decode(message);
		keyBinary = Authutil.hexStringToByteArray(key);
		
		Authutil auth = new Authutil();		
		keyid = auth.parseMessage(messageBinary);
		if (!(keyid.compareTo("002")==0))
			throw new Exception("Bad key number " + keyid);
		return(auth.decryptDecodeMessage(keyBinary));
	}
}
