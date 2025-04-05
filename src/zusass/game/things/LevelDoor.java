package zusass.game.things;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZArrayUtils;
import zgame.core.utils.ZMath;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.LevelRoom;
import zusass.game.ZusassRoom;

/** A {@link Door} used by the infinitely generating levels */
public class LevelDoor extends ZusassDoor{
	
	// TODO add a facing direction, just north, south, east, west
	
	/** The level of the room that this door will lead to */
	private final int level;
	
	/** A buffer holding the text to display the level of this door, initialized on the first frame of rendering */
	private TextBuffer levelTextBuffer;
	
	/**
	 * Create a new LevelDoor at the given location
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param level See {@link #level}
	 */
	public LevelDoor(double x, double y, double z, int level){
		super(x, y, z);
		this.level = level;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		if(this.levelTextBuffer != null) this.levelTextBuffer.destroy();
	}
	
	@Override
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing, Game game){
		ZusassGame zgame = (ZusassGame)game;
		
		// Generate the new room, then enter it
		var levelRoom = new LevelRoom(this.getLevel());
		this.setLeadRoom(levelRoom, 2, 1, 2);
		levelRoom.initRandom();
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
		
		if(this.levelTextBuffer == null){
			// TODO make an easier way of doing this centering without needing a menu?
			// TODO make an abstracted way of doing this for drawing text on a 3D face easily
			var levelText = "Level: " + this.getLevel();
			var font = r.getFont().size(90);
			this.levelTextBuffer = new TextBuffer(500, 500, ZArrayUtils.singleList(new TextOption(levelText, new ZColor(0.8))), font);
			this.levelTextBuffer.setTextX(this.levelTextBuffer.getWidth() * 0.5 - font.stringWidth(levelText) * 0.5);
			this.levelTextBuffer.redraw(r);
		}
		
		r.drawPlaneBuffer(this.getX(), this.getY() + this.getHeight() * 0.5, this.getZ() + this.getLength() * 0.5 + 0.001,
				this.getWidth() * 0.95, this.getWidth() * 0.95,
				ZMath.PI_BY_2 * 3, 0, 0, 0, 0, 0, this.levelTextBuffer.getTextureID());
	}
}
