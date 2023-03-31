package zgame.core.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zgame.core.utils.ZConfig;

/** An interface that defines this object as being able to save and write with JSON */
public interface Saveable{
	
	/**
	 * Save the necessary contents of this object the given {@link JsonObject}.
	 * Does nothing and returns obj by default, can override to save custom data to obj
	 *
	 * @param e The object to save to
	 * @return The modified version of e if the save was successful, null otherwise
	 */
	default JsonElement save(JsonElement e){
		return e;
	}
	
	/**
	 * Save the necessary contents of this object to a new {@link JsonObject}.
	 * This method simply calls {@link #save(JsonElement)} with a new object out of convenience, no need to implement this
	 *
	 * @return The saved object
	 */
	default JsonElement save(){
		return this.save(new JsonObject());
	}
	
	/**
	 * Load the necessary contents for this object from the given {@link JsonElement}.
	 * Does nothing and returns obj by default, can override to load custom data from obj
	 *
	 * @param e The object to load from
	 * @return e if the load was successful, null otherwise
	 * @throws ClassCastException If a property loaded is not a valid value for the type requested
	 * @throws IllegalStateException If the property loaded is a JsonArray but contains more than a single element
	 * @throws NullPointerException If a property returns null and is attempted to be accessed
	 */
	default JsonElement load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		return e;
	}
	
	/**
	 * Load the key from the given {@link JsonElement} as a JsonElement, and load it with {@link #load(JsonElement)}
	 * No need to implement this manually.
	 *
	 * @param key The key to get from the object
	 * @param e The object to check in
	 * @return e if the load was successful, null otherwise
	 * @throws ClassCastException If a property loaded is not a valid value for the type requested
	 * @throws IllegalStateException If the property loaded is a JsonArray but contains more than a single element
	 * @throws NullPointerException If a property returns null and is attempted to be accessed
	 */
	default JsonElement load(String key, JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var element = e.getAsJsonObject().get(key);
		return element == null ? null : this.load(element.getAsJsonObject());
	}
	
	/**
	 * Load a double from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static double d(String key, JsonObject obj, double d){
		return load(Double.class, key, obj, d);
	}
	
	/**
	 * Load an integer from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static int i(String key, JsonObject obj, int d){
		return load(Integer.class, key, obj, d);
	}
	
	/**
	 * Load a boolean from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static boolean b(String key, JsonObject obj, boolean d){
		return load(Boolean.class, key, obj, d);
	}
	
	/**
	 * Load a string from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static String s(String key, JsonObject obj, String d){
		return load(String.class, key, obj, d);
	}
	
	/**
	 * Load a JsonObject from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @return The loaded value, or an empty object if the load fails
	 */
	static JsonObject obj(String key, JsonObject obj){
		return load(JsonObject.class, key, obj, new JsonObject());
	}
	
	/**
	 * Load a JsonArray from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @return The loaded value, or an empty array if the load fails
	 */
	static JsonArray arr(String key, JsonObject obj){
		return load(JsonArray.class, key, obj, new JsonArray());
	}
	
	/**
	 * Load an enum from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value, or an empty array if the load fails
	 */
	static <T extends Enum<T>> T e(String key, JsonObject obj, Class<T> clazz, T d){
		var str = s(key, obj, null);
		try{
			return Enum.valueOf(clazz, str);
		}catch(IllegalArgumentException e){
			return d;
		}
	}
	
	/**
	 * Load a value from the given json object
	 *
	 * @param clazz The type of object to get
	 * @param key The name of the field in the json object
	 * @param obj The json object
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 * @param <T> The type of clazz
	 */
	@SuppressWarnings("unchecked")
	static <T> T load(Class<T> clazz, String key, JsonObject obj, T d){
		if(!obj.has(key)) return d;
		var value = obj.get(key);
		try{
			if(clazz == Double.class) return (T)Double.valueOf(value.getAsDouble());
			if(clazz == Integer.class) return (T)Integer.valueOf(value.getAsInt());
			if(clazz == Boolean.class) return (T)Boolean.valueOf(value.getAsBoolean());
			if(clazz == String.class) return (T)value.getAsString();
			if(clazz == JsonObject.class) return (T)value.getAsJsonObject();
			if(clazz == JsonArray.class) return (T)value.getAsJsonArray();
		}catch(ClassCastException e){
			ZConfig.error("Failed to load object of type", clazz, "for key", key, "from object", obj, "returning default:", d);
		}
		return d;
	}
	
}
