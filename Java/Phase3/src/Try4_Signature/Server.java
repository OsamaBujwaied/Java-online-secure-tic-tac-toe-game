package Try4_Signature;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;
import java.util.Random;
import java.lang.Math;
import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import AES.AES;
import Signature.RsaExample;


public class Server {	
	
	public static void main(String[] args) {

		String secretKey; 		
		
		try {
			//Establish Connection
			System.out.println("Waiting the client (Bob) to cennect.........");
			ServerSocket ss = new ServerSocket(6000);
			Socket s = ss.accept();// establishes connection
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream dis = new DataInputStream(s.getInputStream());
			
			
			//RSA
			int p_a, q_a, e_a, d_a, N_a;
			p_a = 3;
			q_a = 11;
			N_a = p_a*q_a;	//33
			e_a = 3;
			d_a = 7;
			
			int e_b, N_b; // (N_B, e_B) known to Alice
			N_b = 55;
			e_b = 7;

			
			//Diffie Hellman Key exchange
			int a, g, ga, gb,  m, k;
			g = 2;
			m = 2^2048 - 2^1984 - 1 + 2^64 *  ((2^1918) + 124476) ;
			Random rand = new Random();
			a = rand.nextInt(30);
			ga = ((int) Math.pow(g, a))% m;
			System.out.print("\na = "+a + " | g^a = "+ga);
			
			dout.writeInt(ga);
			gb = dis.readInt();
			System.out.print(" | g^b = "+gb);
			k = ((int) Math.pow(gb, a))% m;
			System.out.println(" | Diffie Hellman Key (g^ab) = "+k);
			a = 0; 								//Destroy a
			
			secretKey = Integer.toString(k);
			
			
			// Challenge
			int R_a, R_b;
			R_a = 10*rand.nextInt(10) +1;
			dout.writeInt(R_a);
			R_b = dis.readInt();
			System.out.print("\nR_a = "+R_a + ",\t R_b = "+R_b);
			if (R_b%5==2)System.out.println(",\tChallenge Verified");
			else System.out.println(",\tChallenge Refused"); 
			
			
			//Hash
			String hash = "Alice"+"Bob"+R_a+R_b+ga+gb+k;
			System.out.println("\nHashCode Generated by SHA-256 for:   \n" + hash + " = " + toHexString(getSHA(hash)));


			
			//Signature
			String Sa,Sb;
			KeyPair pair = generateKeyPair();
			Sa = sign(hash+"Alice", pair.getPrivate());
			dout.writeUTF(Sa);
			Sb = dis.readUTF();

			System.out.println("\nSignature Exchange \nSa = " + Sa + "\nSa = " + Sb);
			//System.out.println("Verifing Bob Signature Sb = " + verify(Sb, hash+"Bob" , pair.getPublic()));
			
		    BigInteger p = BigInteger.probablePrime(1000/2, rand);
			


			
			
			//Secret Key Generator
			SecretKey key = decodeKeyFromString(dis.readUTF());
			System.out.println("\nAES Secret Key = " + key);
			//secretKey = key.toString();


			//TicToc Initial Table
			char[] ch = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
			int random;
			dout.writeUTF("\n| 1 | 2 | 3 |\n| 4 | 5 | 6 |\n| 7 | 8 | 9 |\n" + " Input numbers ");
			
			
			// Game Starting (Start of session)
			for (int i = 0; i < 4 ;i++ ) {
				random = check(ch);
				ch[random] = 'O';
				
			    String encryptedString = AES.encrypt("\n| " + ch[0] + " | " + ch[1] + " | " + ch[2] + " |\n| " + ch[3] + " | " + ch[4] + " | "
						+ ch[5] + " |\n| " + ch[6] + " | " + ch[7] + " | " + ch[8] + " |\n"
						+ "Choice the place that you want to play in it >", secretKey) ;
				dout.writeUTF(encryptedString);

				String str = (String) dis.readUTF();
			    String decryptedString = AES.decrypt(str, secretKey) ;
				ch[Integer.parseInt(decryptedString) - 1] = 'X';

				System.out.println("Client input = "+str);

			}
			random = check(ch);
			ch[random] = 'O';
			dout.writeUTF("\n| " + ch[0] + " | " + ch[1] + " | " + ch[2] + " |\n| " + ch[3] + " | " + ch[4] + " | "
					+ ch[5] + " |\n| " + ch[6] + " | " + ch[7] + " | " + ch[8] + " |\n"
					+ "Final Result");
			
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
	
	
	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
		// Static getInstance method is called with hashing SHA
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		// digest() method called
		// to calculate message digest of an input
		// and return array of byte
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}
	public static String toHexString(byte[] hash) {
		// Convert byte array into signum representation
		BigInteger number = new BigInteger(1, hash);
		// Convert message digest into hex value
		StringBuilder hexString = new StringBuilder(number.toString(16));
		// Pad with leading zeros
		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}
		return hexString.toString();
	}
	

////////////////////////////    RSA     ///////////////////////////////
	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}	
	public static KeyPair getKeyPairFromKeyStore() throws Exception {
		// Generated with:
		// keytool -genkeypair -alias mykey -storepass s3cr3t -keypass s3cr3t -keyalg
		// RSA -keystore keystore.jks

		InputStream ins = RsaExample.class.getResourceAsStream("/keystore.jks");
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		keyStore.load(ins, "s3cr3t".toCharArray()); // Keystore password
		KeyStore.PasswordProtection keyPassword = // Key password
				new KeyStore.PasswordProtection("s3cr3t".toCharArray());
		KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("mykey", keyPassword);
		java.security.cert.Certificate cert = keyStore.getCertificate("mykey");
		PublicKey publicKey = cert.getPublicKey();
		PrivateKey privateKey = privateKeyEntry.getPrivateKey();
		return new KeyPair(publicKey, privateKey);
	}
	public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
		return Base64.getEncoder().encodeToString(cipherText);
	}
	public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(cipherText);
		Cipher decriptCipher = Cipher.getInstance("RSA");
		decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(decriptCipher.doFinal(bytes), UTF_8);
	}
	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(UTF_8));
		byte[] signature = privateSignature.sign();
		return Base64.getEncoder().encodeToString(signature);
	}
	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(UTF_8));
		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return publicSignature.verify(signatureBytes);
	}
//////////////////////////////            ///////////////////////////////////////////
	
}
