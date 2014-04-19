package org.mauro.automation.microserver.sessions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.mauro.automation.LogUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class SessionManager {
	private static String dividerIn = "; ";
	private static final Logger log = LogUtils.loggerForThisClass();

	private static final String myKey = "keyAes";

	private static Cipher cipherEncript;
	private static Cipher cipherDecript;

	static {
		try {
			MessageDigest sha = MessageDigest.getInstance("md5"); // 128 bit
			byte[] key = sha.digest(myKey.getBytes("utf-8"));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

			cipherEncript = Cipher.getInstance("AES");
			cipherEncript.init(Cipher.ENCRYPT_MODE, secretKeySpec);

			cipherDecript = Cipher.getInstance("AES");
			cipherDecript.init(Cipher.DECRYPT_MODE, secretKeySpec);

		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			log.log(Level.SEVERE, "This method requires UTF-8 AND SHA256 AND AES support", e);
		}

	}

	public static SessionData fromCookie(HttpExchange t) {
		System.out.println("request uri: " + t.getRequestURI());
		List<String> list = t.getRequestHeaders().get("Cookie");
		if (list != null) {
			for (String c : list) {
				System.out.println("found cookie fromCookie: " + c);
			}
			System.out.println();
			if (list.size() > 0) {
				return new SessionData(false, stringToMap(list.get(0)));
			}
		}
		return new SessionData(true);
	}

	public static boolean setCookie(SessionData session, Headers responseHeaders) {
		// return mapToString(session.getValues())
		StringBuilder str = new StringBuilder();
		for (Entry<String, String> entry : session.values.entrySet()) {
			System.out.println("setto cookie: "+entry.getKey()+" "+entry.getValue() );
			try {
				str.append( URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8") );
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				log.log(Level.SEVERE, "UTF-8 is mandatory", e);
			}
		}
		String cookie = str.toString();
		try {
			byte[] encripted = cipherEncript.doFinal(cookie.getBytes("utf-8"));
			cookie = Base64.encodeBase64URLSafeString(encripted);
			
			responseHeaders.add("Set-Cookie", cookie + "; Path=/; ");
			return true;
		} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "This method requires UTF-8 and AES encoding support", e);
		}
		return false;
	}

	public static Map<String, String> stringToMap(String input) {
		Map<String, String> map = new HashMap<String, String>();

		String[] nameValuePairs = input.split(dividerIn);
		for (String nameValuePair : nameValuePairs) {

			try {
				byte[] decode = Base64.decodeBase64(nameValuePair);
				// System.out.println( "input" +nameValuePair+" "+decode.length );
				byte[] decripted = cipherDecript.doFinal(decode);
				nameValuePair = new String(decripted, "utf-8");
			} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
				log.log(Level.SEVERE, "This method requires UTF-8 and AES decoding support", e);
				return map;
			}

			String[] nameValue = nameValuePair.split("=");
			if (nameValue.length == 2)
				try {
					map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "");
					System.out.println("trovato: "+URLDecoder.decode(nameValue[0], "UTF-8")+" - "+ (nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "") );
				} catch (UnsupportedEncodingException e) {
					log.log(Level.SEVERE, "This method requires UTF-8 encoding support", e);
				}
		}

		return map;
	}
}
