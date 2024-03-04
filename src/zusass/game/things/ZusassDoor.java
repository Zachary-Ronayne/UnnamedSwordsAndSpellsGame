package zusass.game.things;

import zgame.core.Game;
import zgame.things.still.Door;
import zgame.things.type.PositionedHitboxThing2D;
import zgame.things.type.bounds.HitBox2D;
import zgame.world.Room;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door<HitBox2D> implements ZThingClickDetector{
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public ZusassDoor(double x, double y){
		super(x, y, false);
	}
	
	/**
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 *
	 * @param zgame The game used by the tick method
	 * @return true if the door was entered, false otherwise
	 */
	@Override
	public boolean handleZPress(ZusassGame zgame){
		var player = zgame.getPlayer();
		return this.enterRoom(zgame.getCurrentRoom(), player, zgame);
	}
	
	@Override
	public boolean enterRoom(Room<HitBox2D> r, PositionedHitboxThing2D thing, Game game){
		return super.enterRoom(r, thing, game);
	}
	
	/** Convenience method that calls {@link #enterRoom(Room, PositionedHitboxThing2D, Game)} without a need to type cast */
	public boolean enterRoom(ZusassRoom r, PositionedHitboxThing2D thing, Game game){
		return this.enterRoom((Room<HitBox2D>)r, thing, game);
	}
}
