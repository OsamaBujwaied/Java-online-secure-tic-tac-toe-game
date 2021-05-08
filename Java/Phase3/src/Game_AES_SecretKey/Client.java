package Game_AES_SecretKey;
 
import java.io.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.*;
import java.util.Scanner;
import AES.AES;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;


public class Client {
	public static void main(String[] args) {
		
	    String encryptedInput;
	    String decryptedStr;

		try {
			//Establish Connection
			Socket s = new Socket("localhost", 6000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			Scanner myInput = new Scanner(System.in);
			String input;
			
			//Secret Key Generator
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			SecretKey key = keyGenerator.generateKey();
			dout.writeUTF(keyToString(key)); 
			String secretKey = key.toString();
			System.out.println("Secret Key = " + key);
			
			//TicToc Initial Table
			String str = (String) dis.readUTF();
			System.out.println(str);

			// Game Starting
			for (int i = 0; i < 4; i++) {
				str = (String) dis.readUTF();
				decryptedStr = AES.decrypt(str, secretKey) ;
				System.out.println(decryptedStr);

				input = myInput.next();
			    encryptedInput = AES.encrypt(input, secretKey) ;
				dout.writeUTF(encryptedInput);
			}

			str = (String) dis.readUTF();
			System.out.println(str);

			s.close();
			myInput.close();
		} catch (Exception e) {
			System.out.println(e);
		}	
	}
	
	
	public static String keyToString(SecretKey secretKey) {
		byte encoded[] = secretKey.getEncoded();
		String encodedKey = Base64.getEncoder().encodeToString(encoded);
		return encodedKey;
	}
	public static SecretKey decodeKeyFromString(String keyStr) {
		byte[] decodedKey = Base64.getDecoder().decode(keyStr);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}

}

