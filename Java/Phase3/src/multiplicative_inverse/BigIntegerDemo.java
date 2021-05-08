package multiplicative_inverse;

import java.math.*;

//https://www.tutorialspoint.com/java/math/biginteger_modinverse.htm 

public class BigIntegerDemo {

   public static void main(String[] args) {

      // create 3 BigInteger objects
      BigInteger bi1, bi2, bi3;

      // Two numbers are relatively prime if they have no
      // common factors, other than 1.
      bi1 = new BigInteger("7");
      bi2 = new BigInteger("20");

      // perform modInverse operation on bi1 using bi2
      bi3 = bi1.modInverse(bi2);

      String str = bi1 + "^-1 mod " + bi2 + " is " +bi3;

      // print bi3 value
      System.out.println( str );
   }
}