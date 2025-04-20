package zusass.game.things;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableBuffer3D;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZArrayUtils;
import zgame.core.utils.ZMath;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zgame.world.Direction3D;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.LevelRoom;
import zusass.game.ZusassRoom;

/** A {@link Door} used by the infinitely generating levels */
public class LevelDoor extends ZusassDoor{
	
	/** The level of the room that this door will lead to */
	private final int level;
	
	/** A buffer holding the text to display the level of this door, initialized on the first frame of rendering */
	private final DrawableBuffer3D levelTextBuffer;
	
	/**
	 * Create a new LevelDoor at the given location
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param level See {@link #level}
	 * @param direction See {@link #getFacingDirection()}
	 */
	public LevelDoor(double x, double y, double z, int level, Direction3D direction){
		super(x, y, z, direction);
		this.level = level;
		this.levelTextBuffer = new DrawableBuffer3D(null);
		this.updateLevelTextBufferPosition();
	}
	
	/** Recompute the values on {@link #levelTextBuffer} based on current coordinates and direction */
	private void updateLevelTextBufferPosition(){
		var direction = this.getFacingDirection();
		double xOffset = 0;
		double zOffset = 0;
		double longSide;
		double shortSide;
		boolean zAxis = direction == Direction3D.NORTH || direction == Direction3D.SOUTH;
		if(zAxis){
			longSide = this.getWidth();
			shortSide = this.getLength();
		}
		else{
			longSide = this.getLength();
			shortSide = this.getWidth();
		}
		double sideOffset = shortSide * 0.5 + 0.001;
		if(direction.isNegative()) sideOffset = -sideOffset;
		if(zAxis) zOffset = sideOffset;
		else xOffset = sideOffset;
		
		this.levelTextBuffer.setX(this.getX() + xOffset);
		this.levelTextBuffer.setY(this.getY() + this.getHeight() * 0.5);
		this.levelTextBuffer.setZ(this.getZ() + zOffset);
		double size = longSide * 0.95;
		this.levelTextBuffer.setWidth(size);
		this.levelTextBuffer.setLength(size);
		this.levelTextBuffer.setRotX(ZMath.PI_BY_2 * 3);
		this.levelTextBuffer.setRotY(0);
		this.levelTextBuffer.setRotZ(direction.getYaw());
		this.levelTextBuffer.setOpacity(1);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.levelTextBuffer.destroy();
	}
	
	@Override
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing, Game game){
		ZusassGame zgame = (ZusassGame)game;
		
		// Generate the new room, then enter it
		var levelRoom = new LevelRoom(this.getLevel());
		this.setLeadRoom(levelRoom, 2, 1, 2);
		levelRoom.initRandom(zgame);
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
	
	@Override
	public int getSortPriority(){
		return -100;
	}
	
	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
		
		if(this.levelTextBuffer.getBuffer() == null){
			var textB = new TextBuffer(500, 500,
					ZArrayUtils.singleList(new TextOption("Level: " + this.getLevel(), new ZColor(0.8))),
					r.getFont().size(90));
			textB.centerTextX();
			this.levelTextBuffer.setBuffer(textB);
		}
		
		this.levelTextBuffer.render(r);
	}
}
