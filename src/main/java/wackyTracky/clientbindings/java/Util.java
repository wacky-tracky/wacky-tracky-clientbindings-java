package wackyTracky.clientbindings.java;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Util {
	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");

		return s.hasNext() ? s.next() : "";
	}

	public static final String hash(String cleartext) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Security.addProvider(new BouncyCastleProvider());
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String hashed = new BigInteger(1, md.digest(cleartext.getBytes("UTF-8"))).toString(16);
		return hashed;
	}

}
