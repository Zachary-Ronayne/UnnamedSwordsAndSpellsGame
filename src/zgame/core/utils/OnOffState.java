package zgame.core.utils;

/** A simple enum for tracking if something needs to be updated to on or off */
public enum OnOffState{
	/** Turn the action on */
	ENTER,
	/** Turn the action off */
	EXIT,
	/** Do not change the action */
	NOTHING;
	
	/** @return true if this state wants to make an update, false otherwise */
	public boolean shouldUpdate(){
		return this != NOTHING;
	}
	
	/** @return true if the state says to turn on, false otherwise */
	public boolean willEnter(){
		return this == ENTER;
	}
	
	/**
	 * Get the state corresponding to a boolean value
	 * 
	 * @param enter true to get the state for turning on, false for turning off
	 * @return {@link #ENTER} or {@link #EXIT} depending on enter
	 */
	public static OnOffState state(boolean enter){
		return enter ? ENTER : EXIT;
	}
}