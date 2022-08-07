package zusass;

import com.google.gson.JsonObject;

import zgame.core.file.Saveable;

/** A class to hold data representing the game state */
public class ZUSASSData implements Saveable{
	
	// Constants for accessing data
	/** The key for holding the object that stores general data about the game */
	public static final String GENERAL_DATA_KEY = "generalData";
	/** See {@link #highestRoomLevel} */
	public static final String HIGHEST_ROOM_LEVEL_KEY = "highestRoomLevel";
	
	/** The highest level the player has gotten to in the infinitely randomly generated rooms */
	private int highestRoomLevel;
	
	/** Initialize this {@link ZUSASSData} to it's default state */
	public ZUSASSData(){
		this.highestRoomLevel = 0;
	}
	
	@Override
	public JsonObject save(JsonObject obj){
		JsonObject generalData = new JsonObject();
		generalData.addProperty(HIGHEST_ROOM_LEVEL_KEY, highestRoomLevel);
		obj.add(GENERAL_DATA_KEY, generalData);
		return obj;
	}
	
	@Override
	public JsonObject load(JsonObject obj) throws ClassCastException, IllegalStateException{
		JsonObject generalData = obj.getAsJsonObject(GENERAL_DATA_KEY);
		this.highestRoomLevel = generalData.get(HIGHEST_ROOM_LEVEL_KEY).getAsInt();
		return obj;
	}
	
	/** @return See {@link #highestRoomLevel} */
	public int getHighestRoomLevel(){
		return this.highestRoomLevel;
	}
	
	/**
	 * Set the value of {@link #highestRoomLevel}, only if the given value is greater than the current value
	 * @param highestRoomLevel The potential new value for {@link #highestRoomLevel}
	 */
	public void updatedHighestRoomLevel(int highestRoomLevel){
		this.highestRoomLevel = Math.max(highestRoomLevel, this.getHighestRoomLevel());
	}
	
}
