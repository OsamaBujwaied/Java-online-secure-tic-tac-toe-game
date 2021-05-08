package Game_AES_SecretKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import AES.AES;

public class Server {

	public static void main(String[] args) {

		try {
			//Establish Connection
			System.out.println("Waiting the client to cennect.........");
			ServerSocket ss = new ServerSocket(6000);
			Socket s = ss.accept();// establishes connection
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream dis = new DataInputStream(s.getInputStream());
			
			//Secret Key Generator
			SecretKey key = decodeKeyFromString(dis.readUTF());
			System.out.println("Secret Key = " + key);
			String secretKey = key.toString();

			//TicToc Initial Table
			char[] ch = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
			int random;
			dout.writeUTF("\n| 1 | 2 | 3 |\n| 4 | 5 | 6 |\n| 7 | 8 | 9 |\n" + " Input numbers ");
			
			// Game Starting
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

}
