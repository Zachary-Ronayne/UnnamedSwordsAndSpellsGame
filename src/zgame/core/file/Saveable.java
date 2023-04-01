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
	 * @return true if the save was successful, false otherwise
	 */
	default boolean save(JsonElement e){
		return true;
	}
	
	/**
	 * Save the necessary contents of this object to a new {@link JsonObject}.
	 * This method simply calls {@link #save(JsonElement)} with a new object out of convenience, no need to implement this
	 *
	 * @return true if the save was successful, false otherwise
	 */
	default boolean save(){
		return this.save(new JsonObject());
	}
	
	/**
	 * Load the necessary contents for this object from the given {@link JsonElement}.
	 * Does nothing and returns true, can override to load custom data from obj
	 *
	 * @param e The object to load from
	 * @return true if the load succeeded, false otherwise
	 * @throws ClassCastException If a property loaded is not a valid value for the type requested
	 * @throws IllegalStateException If the property loaded is a JsonArray but contains more than a single element
	 * @throws NullPointerException If a property returns null and is attempted to be accessed
	 */
	default boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		return true;
	}
	
	/**
	 * Load the key from the given {@link JsonElement} as a JsonElement, and load it with {@link #load(JsonElement)}
	 * No need to implement this manually.
	 *
	 * @param key The key to get from the object
	 * @param e The object to check in
	 * @return true if the load succeeded, false otherwise
	 * @throws ClassCastException If a property loaded is not a valid value for the type requested
	 * @throws IllegalStateException If the property loaded is a JsonArray but contains more than a single element
	 * @throws NullPointerException If a property returns null and is attempted to be accessed
	 */
	default boolean load(String key, JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var element = e.getAsJsonObject().get(key);
		return this.load(element.getAsJsonObject());
	}
	
	/**
	 * Save the given object to the given json element using the given key
	 * @param key The key to save under
	 * @param e The element to save to
	 * @param s The object to save
	 * @return e as a json object
	 */
	static JsonObject save(String key, JsonElement e, Saveable s){
		var obj = e.getAsJsonObject();
		var newObj = newObj(key, e);
		s.save(newObj);
		return obj;
	}
	
	/**
	 * Save the given iterable object as an array to the given json element using the given key
	 * @param key The key to save under
	 * @param e The element to save to
	 * @param it The iterator of savable objects to save
	 * @return The array
	 */
	static <T extends Saveable> JsonArray saveArr(String key, JsonElement e, Iterable<T> it){
		var arr = newArr(key, e);
		for(var i : it) {
			var newObj = new JsonObject();
			i.save(newObj);
			arr.add(newObj);
		}
		return arr;
	}
	
	/**
	 * Create a new json object and store it in the given json element as an object with the given key
	 * @param key The key of the new object
	 * @param e The object to store the new object in
	 * @return The new object
	 */
	static JsonObject newObj(String key, JsonElement e){
		var newObj = new JsonObject();
		e.getAsJsonObject().add(key, newObj);
		return newObj;
	}
	
	/**
	 * Create a new json object and store it in the given json array
	 * @param arr The array to put the object into
	 * @return The new object
	 */
	static JsonObject newObj(JsonArray arr){
		var newObj = new JsonObject();
		arr.add(newObj);
		return newObj;
	}
	
	/**
	 * Create a new json array and store it in the given json element as an object with the given key
	 * @param key The key of the new object
	 * @param e The object to store the new object in
	 * @return The new object
	 */
	static JsonArray newArr(String key, JsonElement e){
		var obj = e.getAsJsonObject();
		var newArr = new JsonArray();
		obj.add(key, newArr);
		return newArr;
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
				ZConfig.error("Cannot load object. ", clazz, " must implement a constructor which accepts one JsonElement");
				err.printStackTrace();
			}
		}catch(Exception err){
			ZConfig.error("Failed to load object of type ", clazz, " for key ", key, " from element ", e, " returning null");
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
			ZConfig.error("Failed to load object of type ", clazz, " for key ", key, " from object ", obj, " returning default: ", d);
		}
		return d;
	}
	
}
