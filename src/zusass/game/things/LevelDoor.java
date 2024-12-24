package zusass.game.things;

import zgame.core.Game;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.LevelRoom;
import zusass.game.ZusassRoom;

/** A {@link Door} used by the infinitely generating levels */
public class LevelDoor extends ZusassDoor{
	
	/** The level of the room that this door will lead to */
	private final int level;
	
	/** The room which contains this {@link LevelDoor} */
	private final ZusassRoom room;
	
	/**
	 * Create a new LevelDoor at the given location
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param level See {@link #level}
	 * @param room The room which contains this {@link LevelDoor}
	 */
	public LevelDoor(double x, double y, double z, int level, ZusassRoom room){
		super(x, y, z);
		this.level = level;
		this.room = room;
	}
	
	@Override
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing, Game game){
		ZusassGame zgame = (ZusassGame)game;
		
		// Generate the new room, then enter it
		var levelRoom = new LevelRoom(this.getLevel());
		this.setLeadRoom(levelRoom, 0, 0, 0);
		levelRoom.initRandom();
		// TODO figure out the proper way to get these coordinates
//		this.setRoomY(this.getLeadRoom().maxY() - thing.getHeight());
		this.setRoomY(1);
		boolean success = super.enterRoom(r, thing, game);
		
		// Update the highest level room the player has been in
		if(success){
			ZusassData d = zgame.getData();
			d.updatedHighestRoomLevel(this.getLevel());
			d.checkAutoSave(zgame);
		}
		return success;
	}
	
	@Override
	public boolean canEnter(EntityThing3D thing){
		return thing.canEnterRooms() && thing.hasTag(ZusassTags.CAN_ENTER_LEVEL_DOOR);
	}
	
	/** @return See {@link #level} */
	public int getLevel(){
		return this.level;
	}
	
	/** @return See {@link #room} */
	public ZusassRoom getRoom(){
		return this.room;
	}
	
	@Override
	public int getRenderPriority(){
		return -100;
	}
	
}
