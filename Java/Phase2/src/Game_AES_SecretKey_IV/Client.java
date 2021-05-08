package Game_AES_SecretKey_IV;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class Client {
	public static void main(String[] args) {
		try {
			Socket s = new Socket("localhost", 6000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			Scanner myInput = new Scanner(System.in);
			String input;
			
			
			// Generating Secret Key.
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			SecretKey key = keyGenerator.generateKey();
			dout.writeUTF(keyToString(key)); 
			System.out.println("Secret Key = " + key);
			
			// Generating IV.
			byte[] IV = { (byte) 204, 29, (byte) 207, (byte) 217 };
			//SecureRandom random = new SecureRandom();
			//random.nextBytes(IV);
			dout.writeUTF(Base64.getEncoder().encodeToString(IV)); 
			System.out.println("IV = " + Base64.getEncoder().encodeToString(IV));
			System.out.println("IV = " + IV);


			
			String str = (String) dis.readUTF();
			
			System.out.println(str);

			byte[] cipherText;
			for (int i = 0; i < 4; i++) {
				
				str = (String) dis.readUTF();
				System.out.println(str);

				input = myInput.next();
				input = input+"A";
				cipherText = encrypt(Base64.getDecoder().decode(input), key, IV);
				dout.writeUTF(Base64.getEncoder().encodeToString(cipherText));
			}

			str = (String) dis.readUTF();
			System.out.println(str);

			s.close();

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

	
	public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(IV);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] cipherText = cipher.doFinal(plaintext);
		return cipherText;
	}

	public static String decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(IV);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] decryptedText = cipher.doFinal(cipherText);
		return new String(decryptedText);
	}
	
	
}