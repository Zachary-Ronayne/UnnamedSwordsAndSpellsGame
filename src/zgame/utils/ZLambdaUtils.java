package zgame.utils;

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

	/** Cannot instantiate this class */
	private ZLambdaUtils(){
	};
}
