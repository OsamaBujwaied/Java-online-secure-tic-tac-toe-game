package Game_AES;

import java.io.*;

import java.net.*;
import java.util.Scanner;

import AES_Ex1.AES;

public class Client {
	public static void main(String[] args) {
		
	    final String secretKey = "ThisIsSecretKey!";
	    String encryptedInput;
	    String decryptedStr;


		try {
			Socket s = new Socket("localhost", 6000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			Scanner myInput = new Scanner(System.in);
			String input;

			String str = (String) dis.readUTF();
			System.out.println(str);

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

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}