package zusass.game.things;

import zgame.core.Game;
import zgame.core.utils.ZRect;
import zgame.things.still.Door;
import zusass.ZusassGame;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door implements ZThingClickDetector{
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public ZusassDoor(double x, double y){
		super(x, y, false);
	}
	
	@Override
	public ZRect getThingBounds(){
		return this.getBounds();
	}
	
	/**
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 *
	 * @param game The game used by the tick method
	 * @return true if the door was entered, false otherwise
	 */
	@Override
	public boolean handlePress(Game game){
		var zgame = (ZusassGame)game;
		if(!ZThingClickDetector.super.handlePress(zgame)) return false;
		
		var player = zgame.getPlayer();
		return this.enterRoom(zgame.getCurrentRoom(), player, zgame);
	}
	
}
