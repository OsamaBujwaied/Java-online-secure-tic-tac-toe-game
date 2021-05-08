package Game;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
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
				System.out.println(str);

				input = myInput.next();
				dout.writeUTF(input);
			}

			str = (String) dis.readUTF();
			System.out.println(str);

			s.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}