package zusass;

import com.google.gson.JsonElement;

import zgame.core.file.Saveable;

/** A class to hold data representing the game state */
public class ZusassData implements Saveable{
	
	// Constants for accessing data
	/** The key for holding the object that stores general data about the game */
	public static final String GENERAL_DATA_KEY = "generalData";
	/** See {@link #highestRoomLevel} */
	public static final String HIGHEST_ROOM_LEVEL_KEY = "highestRoomLevel";
	
	/** The highest level the player has gotten to in the infinitely randomly generated rooms */
	private int highestRoomLevel;
	
	/** The path to the file which is currently loaded as the play state */
	private String loadedFile;
	
	/** true to save the game on actions like getting to a new level, exiting the game, and so on, false to turn off */
	private boolean autosave;
	
	/**
	 * Create a new object from the given json
	 * @param e The json element
	 */
	public ZusassData(JsonElement e){
		this.load(e);
	}
	
	/** Initialize this {@link ZusassData} to its default state */
	public ZusassData(){
		this.highestRoomLevel = 0;
		this.loadedFile = null;
		this.autosave = true;
	}
	
	@Override
	public boolean save(JsonElement e){
		var generalData = Saveable.newObj(GENERAL_DATA_KEY, e);
		generalData.addProperty(HIGHEST_ROOM_LEVEL_KEY, highestRoomLevel);
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var generalData = Saveable.obj(GENERAL_DATA_KEY, e);
		this.highestRoomLevel = Saveable.i(HIGHEST_ROOM_LEVEL_KEY, generalData, 1);
		return true;
	}
	
	/** @return See {@link #highestRoomLevel} */
	public int getHighestRoomLevel(){
		return this.highestRoomLevel;
	}
	
	/**
	 * Set the value of {@link #highestRoomLevel}, only if the given value is greater than the current value
	 *
	 * @param highestRoomLevel The potential new value for {@link #highestRoomLevel}
	 */
	public void updatedHighestRoomLevel(int highestRoomLevel){
		this.highestRoomLevel = Math.max(highestRoomLevel, this.getHighestRoomLevel());
	}
	
	/** @return See {@link #loadedFile} */
	public String getLoadedFile(){
		return this.loadedFile;
	}
	
	/** @param loadedFile See {@link #loadedFile} */
	public void setLoadedFile(String loadedFile){
		this.loadedFile = loadedFile;
	}
	
	/** @return See {@link #autosave} */
	public boolean isAutosave(){
		return this.autosave;
	}
	
	/** @param autosave See {@link #autosave} */
	public void setAutosave(boolean autosave){
		this.autosave = autosave;
	}
	
	/** If auto save is enabled, save the game, otherwise, do nothing */
	public void checkAutoSave(ZusassGame zgame){
		if(!this.isAutosave()) return;
		zgame.saveLoadedGame();
	}
	
}
