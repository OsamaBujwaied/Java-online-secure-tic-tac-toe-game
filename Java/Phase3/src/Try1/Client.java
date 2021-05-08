package Try1;
 
import java.io.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.*;
import java.util.Scanner;
import AES.AES;
import java.util.Base64;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;


public class Client {
	public static void main(String[] args) {
		
	    String encryptedInput;
	    String decryptedStr;
	    
	    String secretKey;

	    
		try {
			//Establish Connection
			Socket s = new Socket("localhost", 6000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			Scanner myInput = new Scanner(System.in);
			String input;
			
			//Diffie Hellman Key Exchange
			int b, g, ga, gb, m, k;
			g = 2;
			m = 13;
			Random rand = new Random();
			b = rand.nextInt(10);
			gb = ((int) Math.pow(g, b))% m;
			System.out.println("b = " + b+" | g^b = " + gb);
			
			ga = dis.readInt();
			System.out.println("g^a = "+ga);
			dout.writeInt(gb);
			k = ((int) Math.pow(ga, b))% m;
			System.out.println("Diffie Hellman Key (g^ba) = "+k);
			b = 0; 								//Destroy b
			
			secretKey = Integer.toString(k); //Diffe_Hellman key


			//Secret Key Generator
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			SecretKey key = keyGenerator.generateKey();
			dout.writeUTF(keyToString(key)); 
			//secretKey = key.toString();
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

