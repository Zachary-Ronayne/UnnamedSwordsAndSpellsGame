package zgame.core.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
		return this.load(e.getAsJsonObject().get(key).getAsJsonObject());
	}
	
}
