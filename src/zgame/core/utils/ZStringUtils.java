package zgame.core.utils;

/**
 * A class containing utility methods for manipulating strings
 */
public final class ZStringUtils{
	
	/**
	 * Print, to the main System.out, the string representation of a list of objects on one line using efficient string concatenation
	 *
	 * @param objs The list of objects to print together
	 */
	public static void print(Object... objs){
		System.out.println(concat(objs));
	}
	
	/**
	 * Print, to the main System.err, the string representation of a list of objects on one line using efficient string concatenation, as an error
	 *
	 * @param objs The list of objects to print together
	 */
	public static void error(Object... objs){
		System.err.println(concat(objs));
	}
	
	/**
	 * Print, to the main System.out, the string representation of a list of objects on one line using efficient string concatenation, with a space between each element
	 *
	 * @param objs The list of objects to print together
	 */
	public static void prints(Object... objs){
		System.out.println(concats(objs));
	}
	
	/**
	 * Print, to the main System.out, the string representation of a list of objects on one line using efficient string concatenation, with the given string between them
	 *
	 * @param sep The string to put between each object
	 * @param objs The objects to print
	 */
	public static void printSep(String sep, Object... objs){
		System.out.println(concatSep(sep, objs));
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatenation, with a space between each element
	 *
	 * @param separator An object to put between each object in the string, but not after the last element
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concatSep(String separator, Object... objs){
		StringBuilder b = new StringBuilder();
		
		for(int i = 0; i < objs.length; i++){
			Object obj = objs[i];
			b.append((obj == null) ? "null" : obj.toString());
			if(i + 1 != objs.length) b.append(separator);
		}
		return b.toString();
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatenation
	 *
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concat(Object... objs){
		return concatSep("", objs);
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatenation, with a space between each element
	 *
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concats(Object... objs){
		return concatSep(" ", objs);
	}
	
	/**
	 * Get a string representation of a list of the objects in the given array concatenated together using efficient string concatenation, formatted as [e1, e2, e3]
	 *
	 * @param arr The array to get the string
	 * @return The resulting string
	 */
	public static String arrStr(Object[] arr){
		StringBuilder b = new StringBuilder("[");
		b.append(concatSep(", ", arr));
		b.append("]");
		return b.toString();
	}
	
	/**
	 * Print a list of the objects in the given array concatenated together using efficient string concatenation, formatted as [e1, e2, e3]
	 *
	 * @param arr The array of values to print
	 */
	public static void printArr(Object[] arr){
		System.out.println(arrStr(arr));
	}
	
	/**
	 * Insert a string into another string
	 *
	 * @param s The string to insert to
	 * @param index The index in s to insert the string. This method assumes this is a valid index
	 * @param insert The string to insert
	 * @return The new string
	 */
	public static String insertString(String s, int index, String insert){
		return s.substring(0, index) + insert + s.substring(index);
	}
	
	/**
	 * Insert a character into a string
	 *
	 * @param s The string to insert to
	 * @param index The index in s to insert the string. This method assumes this is a valid index
	 * @param insert The character to insert
	 * @return The new string
	 */
	public static String insertString(String s, int index, char insert){
		return insertString(s, index, Character.toString(insert));
	}
	
	/**
	 * Remove the character at the given index of the given string
	 *
	 * @param s The string
	 * @param index the index to remove. This method assumes this is a valid index
	 * @return The new string
	 */
	public static String removeChar(String s, int index){
		return s.substring(0, index) + s.substring(index + 1);
	}
	
	/**
	 * Pad the beginning of the string so that it will be equal to the given length, if it is less than the given length
	 *
	 * @param s The string to pad
	 * @param pad The desired length of the string
	 * @return The padded string, pad("text", 10) should return "      text", note that pad("text", 1) will return "text"
	 */
	public static String pad(String s, int pad){
		StringBuilder sb = new StringBuilder("%");
		sb.append(pad);
		sb.append("s");
		return String.format(sb.toString(), s);
	}
	
	/** Cannot instantiate this class */
	private ZStringUtils(){
	}
	
}
