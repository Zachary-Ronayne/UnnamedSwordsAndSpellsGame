package zgame.core.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zgame.core.utils.ZConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

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
	 * @param e The json element
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static double d(String key, JsonElement e, double d){
		return load(Double.class, key, e, d);
	}
	
	/**
	 * Load an integer from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static int i(String key, JsonElement e, int d){
		return load(Integer.class, key, e, d);
	}
	
	/**
	 * Load a boolean from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static boolean b(String key, JsonElement e, boolean d){
		return load(Boolean.class, key, e, d);
	}
	
	/**
	 * Load a string from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 */
	static String s(String key, JsonElement e, String d){
		return load(String.class, key, e, d);
	}
	
	/**
	 * Load a JsonObject from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @return The loaded value, or an empty object if the load fails
	 */
	static JsonObject obj(String key, JsonElement e){
		return load(JsonObject.class, key, e, new JsonObject());
	}
	
	/**
	 * Load a new instance of a {@link Saveable} object from the given json element.
	 * This method assumes the given JsonElement is a JsonObject and that a key exists
	 *
	 * @param clazz The type of object to load. This class must implement a constructor which accepts one JsonElement as it's parameter
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @return The loaded value, or null if the load fails
	 * @param <T> The type of clazz
	 */
	static <T extends Saveable> T obj(String key, JsonElement e, Class<T> clazz){
		return obj(key, e, clazz, null);
	}
	/**
	 * Load a new instance of a {@link Saveable} object from the given json element.
	 * This method assumes the given JsonElement is a JsonObject and that a key exists
	 *
	 * @param clazz The type of object to load. This class must implement a constructor which accepts one JsonElement as it's parameter
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @param d A function which provides the default value if the load fails. Can be null to return nul by default
	 * @return The loaded value, or the result of d if the load fails
	 * @param <T> The type of clazz
	 */
	static <T extends Saveable> T obj(String key, JsonElement e, Class<T> clazz, Supplier<T> d){
		try{
			var cons = clazz.getConstructor(JsonElement.class);
			return cons.newInstance(e.getAsJsonObject().get(key));
		}catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException err){
			if(ZConfig.printErrors()){
				ZConfig.error("Cannot load object", clazz, "must implement a constructor which accepts one JsonElement");
				err.printStackTrace();
			}
		}catch(Exception err){
			ZConfig.error("Failed to load object of type", clazz, "for key", key, "from element", e, "returning null");
		}
		if(d == null) return null;
		return d.get();
	}
	
	/**
	 * Load a JsonArray from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @return The loaded value, or an empty array if the load fails
	 */
	static JsonArray arr(String key, JsonElement e){
		return load(JsonArray.class, key, e, new JsonArray());
	}
	
	/**
	 * Load an enum from the given json object
	 *
	 * @param key The name of the field in the json object
	 * @param e The json element
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value, or an empty array if the load fails
	 */
	static <T extends Enum<T>> T e(String key, JsonElement e, Class<T> clazz, T d){
		var str = s(key, e, null);
		try{
			return Enum.valueOf(clazz, str);
		}catch(IllegalArgumentException err){
			return d;
		}
	}
	
	/**
	 * Load a value from the given json object
	 *
	 * @param clazz The type of object to get
	 * @param key The name of the field in the json object
	 * @param e The json element to load
	 * @param d The default value to use if the value fails to load
	 * @return The loaded value
	 * @param <T> The type of clazz
	 */
	static <T> T load(Class<T> clazz, String key, JsonElement e, T d){
		if(!e.isJsonObject()) return d;
		var obj = e.getAsJsonObject();
		if(!obj.has(key)) return d;
		var value = obj.get(key);
		try{
			return JsonLoadMap.get(clazz, value);
		}catch(ClassCastException er){
			ZConfig.error("Failed to load object of type", clazz, "for key", key, "from object", obj, "returning default:", d);
		}
		return d;
	}
	
}
