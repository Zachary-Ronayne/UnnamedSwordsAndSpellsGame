package zusass.game;

import zgame.world.Room;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** A {@link Room} used by the Zusass game */
public class ZusassRoom extends Room{

	/** The player which is in this room */
	private ZusassPlayer player;

	/** Create a new room with nothing in it */
	public ZusassRoom(){
		this(0, 0);
	}

	/**
	 * Create a new room of the given size
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 */
	public ZusassRoom(int xTiles, int yTiles){
		super(xTiles, yTiles);
		this.player = null;
	}

	/** @return See {@link #player} */
	public ZusassPlayer getPlayer(){
		return player;
	}

	/** @param player See player. Note that this will not account for adding the player or removing the player from a room */
	public void setPlayer(ZusassPlayer player){
		this.player = player;
	}

	/**
	 * @return This object, as a {@link LevelRoom}, or null if it cannot be a {@link LevelRoom}
	 *         The return value of this method should equal this object, not another version or reference, i.e. (this == this.asLevel()) should evaluate to true
	 */
	public LevelRoom asLevel(){
		return null;
	}

}
