package zgame.core.utils;

/**
 * A utility for creating enums where each enum value is associated with a class
 *
 * @param <T> The type of class associated with each enum
 */
public interface ClassEnum<T>{
	/** @return See T in {@link ClassEnum} */
	Class<? extends T> getClazz();
	
	/** @return The unique name of this instance */
	String name();
	
	/**
	 * Get the type enum from the given class
	 *
	 * @param clazz The class
	 * @param values All possible enum values
	 * @param d The default value if none could be found
	 * @return The enum of clazz
	 */
	static <T> ClassEnum<T> fromClass(Class<? extends T> clazz, ClassEnum<T>[] values, ClassEnum<T> d){
		for(var v : values){
			if(v.getClazz() == clazz) return v;
		}
		return d;
	}
	
	/**
	 * Get the name of the type enum from the given class
	 *
	 * @param clazz The class
	 * @param values All possible enum values
	 * @param d The default value if none could be found
	 * @return The name of the enum of clazz, or the name of d if the given class cannot be found
	 */
	static <T> String name(Class<? extends T> clazz, ClassEnum<T>[] values, ClassEnum<T> d){
		var e = fromClass(clazz, values, d);
		return e.name();
	}
}
