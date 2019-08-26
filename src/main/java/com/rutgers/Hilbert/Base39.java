/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Hilbert;

/**
* This class defined the Base39 encoding.
*
* @author  Eduard Giber Renart
* @version 1.0
*/

public class Base39 {
	private static String characters = "0123456789abcdefghijklmnopqrstuvwxyz><=*";

	/**
	 * Encodes a decimal value to a Base39 <code>String</code>.
	 * 
	 * @param b10
	 *            the decimal value to encode, must be nonnegative.
	 * @return the number encoded as a Base39 <code>String</code>.
	 */
	public String encodeBase10(long b10) {
            if (b10 < 0) {
                    throw new IllegalArgumentException("b10 must be nonnegative");
            }
            String ret = "";
            while (b10 > 0) {
                    ret = characters.charAt((int) (b10 % 39)) + ret;
                    b10 /= 39;
            }
            return ret;

	}

	/**
	 * Decodes a Base39 <code>String</code> returning a <code>long</code>.
	 * 
	 * @param b62
	 *            the Base39 <code>String</code> to decode.
	 * @return the decoded number as a <code>long</code>.
	 * @throws IllegalArgumentException
	 *             if the given <code>String</code> contains characters not
	 *             specified in the constructor.
	 */
	public static long decodeBase39(String b62) {
            b62 = b62.toLowerCase();
            for (char character : b62.toCharArray()) {
                if (!characters.contains(String.valueOf(character))) {
                        throw new IllegalArgumentException("Invalid character(s) in string: " + character);
                }
            }
            long ret = 0;
            b62 = new StringBuffer(b62).reverse().toString();
            long count = 1;
            for (char character : b62.toCharArray()) {
                    ret += characters.indexOf(character) * count;
                    count *= 39;
            }
            return ret;
	}
        
        public static long maxDecodeBase39(int max_string) {
            long ret = 0;
            long count = 1;
            for(int i = 0; i < max_string; i++) {
                ret += characters.indexOf('z') * count;
                count *= 39;
            }
            return ret;
        }
}
