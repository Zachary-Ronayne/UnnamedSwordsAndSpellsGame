package zusass.game.things;

import zgame.core.Game;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zgame.things.still.Door3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door3D implements ZThingClickDetector{
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 */
	public ZusassDoor(double x, double y, double z){
		super(x, y, z, 0.5, 1, 0.1);
	}
	
	/**
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 *
	 * @param zgame The game used by the tick method
	 * @return true if the door was entered, false otherwise
	 */
	@Override
	public boolean handleZusassPress(ZusassGame zgame){
		var player = zgame.getPlayer();
		return this.enterRoom(zgame.getCurrentRoom(), player, zgame);
	}
	
	@Override
	public boolean enterRoom(Room3D r, EntityThing3D thing, Game game){
		return super.enterRoom(r, thing, game);
	}
	
	/** Convenience method that calls {@link #enterRoom(Room3D, EntityThing3D, Game)} without a need to type cast */
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing, Game game){
		return this.enterRoom((Room3D)r, thing, game);
	}
}
