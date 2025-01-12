package zusass.game.things;

import zgame.core.Game;
import zgame.things.ThingClickDetector2D;
import zgame.things.ThingClickDetector3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;
import zusass.ZusassGame;

/** A {@link ThingClickDetector2D} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector3D{
	
	@Override
	default boolean handlePress(Game game, Room3D room, HitBox3D clickerBounds, double clickAngleH, double clickAngleV){
		var zgame = (ZusassGame)game;
		if(!ThingClickDetector3D.super.handlePress(zgame, room, clickerBounds, clickAngleH, clickAngleV)) return false;
		return this.handleZusassPress(zgame, room);
	}
	
	/**
	 * Called when this thing in the Zusass game is clicked
	 * @param zgame The game where the click happened
	 * @param room The room where the click happened
	 *
	 * @return true if this was activated, false otherwise
	 */
	boolean handleZusassPress(ZusassGame zgame, Room3D room);
}
