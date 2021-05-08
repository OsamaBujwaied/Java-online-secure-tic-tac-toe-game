package Game_AES_SecretKey_IV;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Server {

	public static void main(String[] args) {

		try {
			System.out.println("Waiting the client to cennect.........");
			ServerSocket ss = new ServerSocket(6000);
			Socket s = ss.accept();// establishes connection

			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream dis = new DataInputStream(s.getInputStream());

			char[] ch = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
			int random;

			// Get Secret Key.
			SecretKey key = decodeKeyFromString(dis.readUTF());
			System.out.println("Secret Key = " + key);
			// Get IV.
			//byte[] IV = { (byte) 204, 29, (byte) 207, (byte) 217 };
			byte[] IV = Base64.getDecoder().decode(dis.readUTF());
			System.out.println("IV = " + Base64.getEncoder().encodeToString(IV));
			System.out.println("IV = " + IV);

			
			dout.writeUTF("\n| 1 | 2 | 3 |\n| 4 | 5 | 6 |\n| 7 | 8 | 9 |\n" + "Input numbers ");

			byte[] cipherText;
			String plainText;
			for (int i = 0; i < 4; i++) {
				random = check(ch);
				ch[random] = 'O';

				dout.writeUTF("\n| " + ch[0] + " | " + ch[1] + " | " + ch[2] + " |\n| " + ch[3] + " | " + ch[4] + " | "
						+ ch[5] + " |\n| " + ch[6] + " | " + ch[7] + " | " + ch[8] + " |\n"
						+ "Choice the place that you want to play in it >");

				
				cipherText = Base64.getDecoder().decode(dis.readUTF());
				plainText = decrypt(cipherText, key, IV);
				plainText = plainText.substring(0, 1);
				ch[Integer.parseInt(plainText) - 1] = 'X';
				System.out.println("Client input = " + plainText);
			}
			
			random = check(ch);
			ch[random] = 'O';
			dout.writeUTF("\n| " + ch[0] + " | " + ch[1] + " | " + ch[2] + " |\n| " + ch[3] + " | " + ch[4] + " | "
					+ ch[5] + " |\n| " + ch[6] + " | " + ch[7] + " | " + ch[8] + " |\n" + "Final Result");

			ss.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static int check(char[] ch) {
		Random rand = new Random();
		int n = rand.nextInt(9);
		for (int i = 0; i < 9; i++) {
			if (ch[n] != ' ') {
				i = 0;
				n = rand.nextInt(9);
			}
		}
		return n;
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
		System.out.println("ivSpec = " + ivSpec);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] decryptedText = cipher.doFinal(cipherText);
		return new String(decryptedText);
	}
	
	
}
