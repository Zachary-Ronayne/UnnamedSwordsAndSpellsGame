package zgame.core.utils;

/**
 * A class containing interfaces for simple lambda functions
 */
public final class ZLambda{
	
	/** A simple lambda function that takes no parameters */
	public interface EmptyFunc{
		void run();
	}
	
	/** A method that does nothing for convenience */
	public static void emptyMethod(){
	}
	
	/** A simple lambda function that takes no parameters and returns a boolean */
	public interface BooleanFunc{
		boolean check();
	}
	
	/** Cannot instantiate this class */
	private ZLambda(){
	}
}
