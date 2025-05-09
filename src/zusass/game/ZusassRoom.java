package zusass.game;

import zgame.core.utils.NotNullList;
import zgame.world.Room;
import zgame.world.Room3D;
import zusass.game.things.ZThingClickDetector;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link Room} used by the Zusass game */
public class ZusassRoom extends Room3D{
	
	/** Create a new room with nothing in it */
	public ZusassRoom(){
		this(0, 0, 0);
	}
	
	/**
	 * Create a new room of the given size
	 *
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 * @param zTiles The number of tiles on the z axis
	 */
	public ZusassRoom(int xTiles, int yTiles, int zTiles){
		super(xTiles, yTiles, zTiles);
		this.getAllThings().addClass(ZusassMob.class);
		this.getAllThings().addClass(ZThingClickDetector.class);
	}
	
	/** @return All the mobs which are in this {@link ZusassRoom} */
	public NotNullList<ZusassMob> getMobs(){
		return this.getAllThings().get(ZusassMob.class);
	}
	
}
