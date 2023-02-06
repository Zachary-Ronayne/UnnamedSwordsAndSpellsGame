package zusass.game;

import zgame.core.utils.NotNullList;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** A {@link Room} used by the Zusass game */
public class ZusassRoom extends Room{
	
	/** The player which is in this room */
	private ZusassPlayer player;
	
	private final NotNullList<ZusassMob> mobs;
	
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
		this.mobs = new NotNullList<>();
		
		this.player = null;
	}
	
	@Override
	public void addThing(GameThing thing){
		// TODO abstract this out to add a list of any type in room
		super.addThing(thing);
		if(thing instanceof ZusassMob m) this.getMobs().add(m);
	}
	
	@Override
	public void tickRemoveThing(GameThing thing){
		super.tickRemoveThing(thing);
		if(thing instanceof ZusassMob m) this.getMobs().remove(m);
	}
	
	/** @return All the mobs which are in this {@link ZusassRoom} */
	public NotNullList<ZusassMob> getMobs(){
		return this.mobs;
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
	 * The return value of this method should equal this object, not another version or reference, i.e. (this == this.asLevel()) should evaluate to true
	 */
	public LevelRoom asLevel(){
		return null;
	}
	
}
