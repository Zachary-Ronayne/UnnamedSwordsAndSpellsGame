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
	public static void print(Object ... objs){
		System.out.println(concat(objs));
	}
	
	/**
	 * Print, to the main System.out, the string representation of a list of objects on one line using efficient string concatenation, with a space between each element
	 * 
	 * @param objs The list of objects to print together
	 */
	public static void prints(Object ... objs){
		System.out.println(concats(objs));
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatenation, with a space between each element
	 * 
	 * @param separator An object to put between each object in the string, but not after the last element
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concatSep(String separator, Object ... objs){
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
	public static String concat(Object ... objs){
		return concatSep("", objs);
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatenation, with a space between each element
	 * 
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concats(Object ... objs){
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
	 * @param arr The array to get the string
	 * @return The resulting string
	 */
	public static void printArr(Object[] arr){
		System.out.println(arrStr(arr));
	}
	
	/** Cannot instantiate this class */
	private ZStringUtils(){
	};
	
}
