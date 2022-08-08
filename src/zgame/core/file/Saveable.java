package zgame.core.file;

import com.google.gson.JsonObject;

/** An interface that defines this object as being able to save and write with JSON */
public interface Saveable{
	
	/**
	 * Save the necessary contents of this object the given {@link JsonObject}.
	 * Does nothing and returns obj by default, can override to save custom data to obj
	 * 
	 * @param obj The object to save to
	 * @return The modified version of obj if the save was successful, null otherwise
	 */
	public default JsonObject save(JsonObject obj){
		return obj;
	}

	/**
	 * Save the necessary contents of this object to a new {@link JsonObject}.
	 * This method simply calls {@link #save(JsonObject)} with a new object out of convenience, no need to implement this
	 * 
	 * @return The saved object
	 */
	public default JsonObject save(){
		return this.save(new JsonObject());
	}
	
	/**
	 * Load the necessary contents for this object from the given {@link JsonObject}.
	 * Does nothing and returns obj by default, can override to load custom data from obj
	 * 
	 * @param obj The object to load from
	 * @return obj if the load was successful, null otherwise
	 * 
	 * @throws ClassCastException If a property loaded is not a valid value for the type requested
	 * @throws IllegalStateException If the property loaded is a JsonArray but contains more than a single element
	 * @throws NullPointerException If a property returns null and is attempted to be accessed
	 */
	public default JsonObject load(JsonObject obj) throws ClassCastException, IllegalStateException, NullPointerException{
		return obj;
	}

	/**
	 * Load the key from the given {@link JsonObject} as a JsonObject, and load it with {@link #load(JsonObject)}
	 * No need to implement this manually.
	 * 
	 * @param key The key to get from the object
	 * @param obj The object to check in
	 * @return The loaded object if the load was successful, null otherwise
	 */
	public default JsonObject load(String key, JsonObject obj) throws ClassCastException, IllegalStateException, NullPointerException{
		return this.load(obj.get(key).getAsJsonObject());
	}
	
}
