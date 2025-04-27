package zusass.game;

import zgame.core.graphics.AlphaMode;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableBuffer3D;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZArrayUtils;
import zgame.core.utils.ZMath;
import zgame.stat.modifier.ModifierType;
import zgame.things.still.tiles.BaseTiles3D;
import zgame.things.type.GameThing;

import static zgame.world.Direction3D.*;

import zgame.world.Room;
import zusass.ZusassGame;
import zusass.game.things.LevelDoor;
import zusass.game.things.ZusassTags;
import zusass.game.things.entities.mobs.Npc;
import zusass.game.things.tiles.ZusassTiles;

import java.util.Random;

import static zusass.game.stat.ZusassStat.*;

/** A {@link Room} which represents a randomly generated level for the infinite dungeons */
public class LevelRoom extends ZusassRoom{
	
	/** The size in pixels of the width of {@link #levelTextBuffer} */
	private static final int LEVEL_TEXT_BUFFER_WIDTH = 600;
	/** The size in pixels of the length of {@link #levelTextBuffer} */
	private static final int LEVEL_TEXT_BUFFER_LENGTH = 80;
	/** The size in game units of the width to display {@link #levelTextBuffer} */
	private static final int LEVEL_TEXT_DISPLAY_WIDTH = 2;
	
	/**
	 * The numerical value of the level, i.e. level 1 is the easiest, level 2 is harder, etc.
	 * If this value is less than 1, it is set to 1
	 */
	private int level;
	
	/** The tint color for this room */
	private ZColor levelTint;
	
	/** A buffer used for drawing the level of this room */
	private DrawableBuffer3D levelTextBuffer;
	
	/**
	 * Create a new empty level
	 *
	 * @param level See {@link #level}
	 */
	public LevelRoom(int level){
		super(0, 0, 0);
		this.addTags(ZusassTags.IS_LEVEL);
		this.setLevel(level);
		this.getAllThings().addClass(Npc.class);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.levelTextBuffer.destroy();
	}
	
	/**
	 * Initialize the state of this level room by adding all the intended objects, i.e., tiles, mobs, etc.
	 */
	public void initRandom(){
		// Grab this game's seed
		long seed = ZusassGame.get().getData().getSeed();
		var random = new Random(seed * (1 + this.getLevel()));
		
		// Set up the tiles, slight room variation size for now, this will make the rooms up to 5 tiles longer on each axis, scaling up slowly
		var sizeScale = 5 * (1 - (1 / Math.log(0.4 * this.getLevel() + Math.E)));
		int xTiles = 7 + (int)(random.nextDouble() * sizeScale);
		int yTiles = 4 + (int)(random.nextDouble() * sizeScale * 0.25);
		int zTiles = 6 + (int)(random.nextDouble() * sizeScale);
		
		// Everything is air by default
		this.initTiles(xTiles, yTiles, zTiles, BaseTiles3D.AIR);
		this.setTileBoundaries();
		
		this.levelTint = new ZColor(0.2 + random.nextDouble() * 0.5, 0.2 + random.nextDouble() * 0.5, 0.2 + random.nextDouble() * 0.5);
		ZusassTiles.setLevelTint(this.levelTint);
		
		// Make a floor and ceiling
		for(int i = 0; i < xTiles; i++){
			for(int k = 0; k < zTiles; k++){
				this.setTile(i, 0, k, ZusassTiles.LEVEL_FLOOR_COLOR);
				this.setTile(i, yTiles - 1, k, ZusassTiles.LEVEL_CEILING_COLOR);
			}
		}
		
		// Make east/west walls
		for(int i = 0; i < xTiles; i++){
			for(int j = 0; j < yTiles; j++){
				this.setTile(i, j, 0, ZusassTiles.LEVEL_WALL_COLOR);
				this.setTile(i, j, zTiles - 1, ZusassTiles.LEVEL_WALL_COLOR);
			}
		}
		
		// Make north/south walls
		for(int i = 0; i < zTiles; i++){
			for(int j = 0; j < yTiles; j++){
				this.setTile(0, j, i, ZusassTiles.LEVEL_WALL_COLOR);
				this.setTile(xTiles - 1, j, i, ZusassTiles.LEVEL_WALL_COLOR);
			}
		}
		
		// Add the door
		var levelDoor = new LevelDoor(xTiles - 1.25, 1, zTiles - 3, this.getLevel() + 1, EAST);
		this.addThing(levelDoor);
		
		// Put tiles in front of the door, mostly for testing
		this.setTile(xTiles - 3, 1, zTiles - 3, ZusassTiles.GRAY_BRICK);
		this.setTile(xTiles - 3, 2, zTiles - 4, ZusassTiles.GRAY_BRICK);
		
		// issue#25 if this is changed to add hundreds of enemies, the TPS tanks while not using all the computer's resources. Probably need to make tick looper account for time spent rendering
		// Add enemies
		var enemy = new Npc(4, 1, 3, 0.15, 0.6);
		enemy.setStat(ENDURANCE, 2 + 6 * (1 - (10 / (this.level + 10.0))));
		enemy.setStat(STRENGTH, 10);
		enemy.setStat(INTELLIGENCE, 5 + 30 * (1 - (10 / (this.level + 10.0))));
		enemy.getStat(STRENGTH).addModifier(enemy.getUuid(), this.level, ModifierType.ADD);
		enemy.setResourcesMax();
		
		this.addThing(enemy);
		
		// Make and position a buffer for drawing some text for the level
		this.levelTextBuffer = new DrawableBuffer3D(null);
		this.levelTextBuffer.setX(1.25 + LEVEL_TEXT_DISPLAY_WIDTH * 0.5);
		this.levelTextBuffer.setY(1.5);
		this.levelTextBuffer.setZ(1.0001);
		this.levelTextBuffer.setWidth(LEVEL_TEXT_DISPLAY_WIDTH);
		this.levelTextBuffer.setLength(LEVEL_TEXT_DISPLAY_WIDTH * (double)LEVEL_TEXT_BUFFER_LENGTH / (double)LEVEL_TEXT_BUFFER_WIDTH);
		this.levelTextBuffer.setRotX(ZMath.PI_BY_2 * 3);
		this.levelTextBuffer.setRotZ(NORTH.getYaw());
	}
	
	/** @return true if this room is cleared and can be exited, false otherwise */
	public boolean isRoomCleared(){
		return this.getEnemiesRemainingCount() <= 0;
	}
	
	public int getEnemiesRemainingCount(){
		return this.getAllThings().get(Npc.class).size();
	}
	
	/** @return See {@link #level} */
	public int getLevel(){
		return this.level;
	}
	
	/** @param level See {@link #level} */
	private void setLevel(int level){
		this.level = Math.max(1, level);
	}
	
	@Override
	public boolean canLeave(GameThing thing){
		// Players cannot leave the room if it is not cleared
		return !thing.hasTag(ZusassTags.MUST_CLEAR_LEVEL_ROOM) || this.isRoomCleared();
	}
	
	@Override
	public void render(Renderer r){
		// Draw the main rendering
		super.render(r);
		
		// Draw a numerical level counter
		if(this.levelTextBuffer != null){
			if(this.levelTextBuffer.getBuffer() == null){
				var textB = new TextBuffer(LEVEL_TEXT_BUFFER_WIDTH, LEVEL_TEXT_BUFFER_LENGTH){
					@Override
					public void draw(Renderer r){
						r.setColor(new ZColor(0.3, 0.5));
						r.fill();
						super.draw(r);
					}
				};
				textB.setFont(r.getFont().size(60));
				textB.setOptions(ZArrayUtils.singleList(new TextOption("Current Level: " + this.getLevel(), new ZColor(0.8), AlphaMode.NORMAL)));
				textB.setTextX(15);
				textB.centerTextY();
				this.levelTextBuffer.setBuffer(textB);
			}
			this.levelTextBuffer.render(r);
		}
	}
	
}