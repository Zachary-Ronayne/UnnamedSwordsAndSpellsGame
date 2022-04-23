package zgame.core.utils;

/** A class containing misc methods for math stuff */
public final class ZMathUtils{
	
	/**
	 * Convert an integer to an array of boolean values representing it's binary
	 * 
	 * @param n The number to convert
	 * @return The array. true = 1, false = 0,
	 *         Index 0 is the most significant bit, the last index is the least significant bit,
	 *         No trailing zeros are ever used.
	 *         Should only be used for integers greater than or equal to zero
	 */
	public static boolean[] intToBoolArr(int n){
		int cnt = 0;
		int l = n;
		do{
			cnt++;
			l /= 2;
		}while(l > 0);
		boolean[] arr = new boolean[cnt];
		l = n;
		for(int i = 0; i < cnt; i++){
			arr[arr.length - i - 1] = l % 2 == 1;
			l /= 2;
		}
		return arr;
	}

	/**
	 * Find the minimum of a list of numbers
	 * @param nums The numbers. This is assumed to have at least one element
	 * @return The minimum number
	 */
	public static double min(double ... nums){
		double n = nums[0];
		for(int i = 1; i < nums.length; i++) n = Math.min(n, nums[i]);
		return n;
	}

	/**
	 * Find the maximum of a list of numbers
	 * 
	 * @param nums The numbers. This is assumed to have at least one element
	 * @return The maximum number
	 */
	public static double max(double ... nums){
		double n = nums[0];
		for(int i = 1; i < nums.length; i++) n = Math.max(n, nums[i]);
		return n;
	}
	
	/** Cannot instantiate {@link ZMathUtils} */
	private ZMathUtils(){
	}
	
}
