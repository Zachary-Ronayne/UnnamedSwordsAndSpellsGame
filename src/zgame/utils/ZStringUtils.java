package zgame.utils;

/**
 * A class containing utility methods for manipulating strings
 */
public final class ZStringUtils{
	
	/**
	 * Print, to the main System.out, the string representation of a list of objects on one line using efficient string concatination
	 * 
	 * @param objs The list of objects to print together
	 */
	public static void print(Object ... objs){
		System.out.println(concat(objs));
	}

	/**
	 * Print, to the main System.out, the string representation of a list of objects on one line using efficient string concatination, with a space between each element
	 * 
	 * @param objs The list of objects to print together
	 */
	public static void prints(Object ... objs){
		System.out.println(concats(objs));
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatination
	 * 
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concat(Object ... objs){
		StringBuilder b = new StringBuilder();
		for(Object obj : objs) b.append(obj.toString());
		return b.toString();
	}
	
	/**
	 * Get a string representation of a list of objects concatenated together using efficient string concatination, with a space between each element
	 * 
	 * @param objs The list of objects to put together
	 * @return The resulting string
	 */
	public static String concats(Object ... objs){
		StringBuilder b = new StringBuilder();
		for(Object obj : objs){
			b.append(obj.toString());
			b.append(" ");
		}
		return b.toString();
	}
	
	/** Cannot instantiate this class */
	private ZStringUtils(){
	};
	
}
