package zgame.core.file;

import com.google.gson.JsonObject;

/** An interface that defines this object as being able to save and write with JSON */
public interface Saveable{
	
	/**
	 * Save the necessary contents of this object the given {@link JsonObject}.
	 * 
	 * @param obj The object to save to
	 * @return true if the save was successful, false otherwise
	 */
	public boolean save(JsonObject obj);
	
	/**
	 * Load the necessary contents for this object from the given {@link JsonObject}.
	 * 
	 * @param obj The object to load from
	 * @return true if the load was successful, false otherwise
	 * 
	 * @throws ClassCastException If a property loaded is not a valid value for the type requested
	 * @throws IllegalStateException If the property loaded is a JsonArray but contains more than a single element
	 */
	public boolean load(JsonObject obj) throws ClassCastException, IllegalStateException;
	
}
