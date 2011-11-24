package org.planetgammu.android.transauth;

import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Authutil {
	private byte[] encrypted;
	private byte[] decrypted;
	
	private static long bcdToLong(int[] input) {
		long output=0;
		for(int i:input) {
			output *= 10;
			output += i;
		}
		return(output);
	}
	/*
	 * from http://stackoverflow.com/questions/140131/
	 * convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java/140861#140861
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	public String parseMessage(byte[] message) throws Exception {
		int version;
		if (!(message[0] == 'T' && message[1] == 'A' && message[2] == 'M'))
			throw new Exception("parse failed");
		version = message[4] << 8 | message[3];
		// do something with version
		if (version != 2)
			throw new Exception("unsupported version" + version);
		String keyid = new String(Arrays.copyOfRange(message,5,8),"UTF-8");
		encrypted = Arrays.copyOfRange(message, 8, message.length);
		return(keyid);
	}
	private void decryptMessage(byte[] key) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		decrypted = cipher.doFinal(encrypted);
	}
	private long[] decodeMessage() throws Exception {
		if (!(decrypted[0] == 'D' && decrypted[1] == 'P' && decrypted[2] == 'D'))
			throw new Exception("bad DPD");
		int[] result = Dpd.unpack(Arrays.copyOfRange(decrypted, 3, decrypted.length));
		long account = bcdToLong(Arrays.copyOfRange(result,0,16));
		long amount = bcdToLong(Arrays.copyOfRange(result,16,27));
		long pin = bcdToLong(Arrays.copyOfRange(result, 27, 31));
		long[] i = {account,amount,pin};
		return(i);
	}
	public long[] decryptDecodeMessage(byte[] key) throws Exception {
		decryptMessage(key);
		return decodeMessage();
	}
}
