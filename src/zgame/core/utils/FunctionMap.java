package zgame.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** A map of functions to classes */
public class FunctionMap{
	
	/**
	 * A mapping of the functions to call on certain object types. This essentially replaces manually defining abstract functions in a class, allowing a specific
	 * function to be called for a specific type
	 */
	private final Map<Class<?>, Consumer<?>> mappedFuncs;
	
	/** Create a new map */
	public FunctionMap(){
		this.mappedFuncs = new HashMap<>();
	}
	
	/**
	 * Add a new function for {@link #mappedFuncs}
	 * @param clazz The class of the type of the object accepted by the function
	 * @param func The function
	 * @param <T> The type of clazz
	 */
	public <T> void addFunc(Class<T> clazz, Consumer<T> func){
		mappedFuncs.put(clazz, func);
	}
	
	/**
	 * Call a function from {@link #mappedFuncs}.
	 * Does nothing if no function exists
	 *
	 * @param clazz The class of the type of the object accepted by the function
	 * @param thing The object to pass to the function
	 * @param <T> The type of clazz
	 */
	@SuppressWarnings("unchecked")
	public <T> void func(Class<T> clazz, T thing){
		var func = (Consumer<T>)this.mappedFuncs.get(clazz);
		if(func == null) return;
		func.accept(thing);
	}
}
