package zgame.settings;

import zgame.core.Game;

/** An object keeping tracking of values used by the game */
public class Settings{
	
	/** The {@link Game} using this {@link Settings} */
	private final Game game;
	
	/** The settings used by this object */
	private final Setting[] values;
	
	/**
	 * Create a new settings object for the given game
	 * @param game See {@link #game}
	 */
	public Settings(Game game){
		this.game = game;
		
		this.values = new Setting[SettingId.numIds()];
		/*
		 TODO figure out how to populate settings, make each enum have to implement a static method that adds its types to a central list or something,
		 	 then that list of enums is how this list of values is populated
		 */
	}
	
	/** @return See {@link #game} */
	public Game getGame(){
		return this.game;
	}
	
	/**
	 * Get an integer value of a setting
	 * @param setting The name of the setting to get the value of
	 * @return The setting's value
	 */
	public Integer get(IntTypeSetting setting){
		return (Integer)this.values[setting.getId()].get();
	}
	
}
