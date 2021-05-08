package AES_Ex1;

public class AES_example {
	public static void main(String[] args) 
	{
	    final String secretKey = "ThisIsSecretKey!";
	    
	    String originalString = "howtodoinjava.com";
	    System.out.println(originalString);
	    
	    String encryptedString = AES.encrypt(originalString, secretKey) ;
	    System.out.println(encryptedString);

	    String decryptedString = AES.decrypt(encryptedString, secretKey) ;
	    System.out.println(decryptedString);
	}
}
