package zgame.core.utils;

/**
 * A class containing interfaces for simple lambda functions
 */
public final class ZLambdaUtils{
	
	/** A simple lambda function that takes no parameters */
	public interface EmptyFunc{
		public void run();
	}
	
	/** A method that does nothing for convenience */
	public static void emptyMethod(){
	}
	
	/** A simple lambda function that takes no parameters and returns a boolean */
	public interface BooleanFunc{
		public boolean check();
	}
	
	/** A lambda that takes 2 integers and returns nothing */
	public interface TwoInt{
		public void run(int a, int b);
	}
	
	/** Cannot instantiate this class */
	private ZLambdaUtils(){
	};
}
