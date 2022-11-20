package zgame.core.utils;

/**
 * A class containing interfaces for simple lambda functions
 */
public final class ZLambda{
	
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
	
	/** A lambda that takes an argument for any type and returns nothing */
	public interface RunObject<T>{
		public void run(T obj);
	}
	
	/** Cannot instantiate this class */
	private ZLambda(){
	};
}
