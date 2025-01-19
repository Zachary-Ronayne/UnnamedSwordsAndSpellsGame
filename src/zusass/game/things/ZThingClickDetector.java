package zusass.game.things;

import zgame.core.Game;
import zgame.things.ThingClickDetector2D;
import zgame.things.ThingClickDetector3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;

/** A {@link ThingClickDetector2D} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector3D{
	
	@Override
	default void handlePress(Game game, Room3D room){
		this.handleZusassPress((ZusassGame)game, (ZusassRoom)room);
	}
	
	/**
	 * Called when this thing in the Zusass game is clicked
	 *
	 * @param zgame The game where the click happened
	 * @param room The room where the click happened
	 */
	void handleZusassPress(ZusassGame zgame, ZusassRoom room);
}
