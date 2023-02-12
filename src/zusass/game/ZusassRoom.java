package zusass.game;

import zgame.core.utils.NotNullList;
import zgame.world.Room;
import zusass.game.things.entities.mobs.ZusassMob;
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
	 *
	 * @param xTiles The number of tiles on the x axis
	 * @param yTiles The number of tiles on the y axis
	 */
	public ZusassRoom(int xTiles, int yTiles){
		super(xTiles, yTiles);
		this.getAllThings().addClass(ZusassMob.class);
		
		this.player = null;
	}
	
	/** @return All the mobs which are in this {@link ZusassRoom} */
	public NotNullList<ZusassMob> getMobs(){
		return this.getAllThings().get(ZusassMob.class);
	}
	
	/** @return See {@link #player} */
	public ZusassPlayer getPlayer(){
		return player;
	}
	
	/** @param player See player. Note that this will not account for adding the player or removing the player from a room */
	public void setPlayer(ZusassPlayer player){
		this.player = player;
	}
	
}
