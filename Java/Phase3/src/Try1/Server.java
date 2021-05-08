package Try1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import java.lang.Math;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import AES.AES;


public class Server {	
	
	public static void main(String[] args) {

		String secretKey; 
		
		//int p1, q1, e1, d1, N;
				
		
		
		try {
			//Establish Connection
			System.out.println("Waiting the client to cennect.........");
			ServerSocket ss = new ServerSocket(6000);
			Socket s = ss.accept();// establishes connection
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream dis = new DataInputStream(s.getInputStream());
			
			//Diffie Hellman Key exchange
			int a, g, ga, gb,  m, k;
			g = 2;
			m = 13;
			Random rand = new Random();
			a = rand.nextInt(10);
			ga = ((int) Math.pow(g, a))% m;
			System.out.println("a = "+a + " | g^a = "+ga);
			
			dout.writeInt(ga);
			gb = dis.readInt();
			System.out.println("g^b = "+gb);
			k = ((int) Math.pow(gb, a))% m;
			System.out.println("Diffie Hellman Key (g^ab) = "+k);
			a = 0; 								//Destroy a
			
			secretKey = Integer.toString(k);

						

			//Secret Key Generator
			SecretKey key = decodeKeyFromString(dis.readUTF());
			System.out.println("Secret Key = " + key);
			//secretKey = key.toString();


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
