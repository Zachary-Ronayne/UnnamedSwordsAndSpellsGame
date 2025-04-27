package zusass.game.things;

import zgame.things.ThingClickDetector3D;
import zgame.world.Room3D;
import zusass.game.ZusassRoom;

/** A {@link ThingClickDetector3D} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector3D{
	
	@Override
	default void handlePress(Room3D room){
		this.handleZusassPress((ZusassRoom)room);
	}
	
	/**
	 * Called when this thing in the Zusass game is clicked
	 *
	 * @param room The room where the click happened
	 */
	default void handleZusassPress(ZusassRoom room){}
}
