package zusass.game;

import zgame.core.Game;
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
import zusass.game.things.LevelDoor;
import zusass.game.things.ZusassTags;
import zusass.game.things.entities.mobs.Npc;
import zusass.game.things.tiles.ZusassColorTiles;

import static zusass.game.stat.ZusassStat.*;

/** A {@link Room} which represents a randomly generated level for the infinite dungeons */
public class LevelRoom extends ZusassRoom{
	
	/** The number of tiles in a {@link LevelRoom} on the x axis */
	private static final int X_TILES = 9;
	/** The number of tiles in a {@link LevelRoom} on the y axis */
	private static final int Y_TILES = 5;
	/** The number of tiles in a {@link LevelRoom} on the z axis */
	private static final int Z_TILES = 7;
	
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
	
	/** The first color of the checkerboard pattern of this room */
	private ZColor checker1;
	/** The second color of the checkerboard pattern of this room */
	private ZColor checker2;
	
	/** A buffer used for drawing the level of this room */
	private DrawableBuffer3D levelTextBuffer;
	
	/**
	 * Create a new empty level
	 *
	 * @param level See {@link #level}
	 */
	public LevelRoom(int level){
		super(X_TILES, Y_TILES, Z_TILES);
		this.addTags(ZusassTags.IS_LEVEL);
		this.setLevel(level);
		this.getAllThings().addClass(Npc.class);
		this.setTileBoundaries();
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
		// Set up the tiles
		
		// Everything is air by default
		this.checker1 = new ZColor(0.2 + Math.random() * 0.5, 0.2 + Math.random() * 0.5, 0.2 + Math.random() * 0.5);
		this.checker2 = new ZColor(checker1.red() * 0.5, checker1.green() * 0.5, checker1.blue() * 0.5);
		for(int i = 0; i < X_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				for(int k = 0; k < Z_TILES; k++){
					this.setTile(i, j, k, BaseTiles3D.AIR);
				}
			}
		}
		ZusassColorTiles.setColors(this.checker1, this.checker2);
		
		// Make a floor and ceiling
		for(int i = 0; i < X_TILES; i++){
			for(int k = 0; k < Z_TILES; k++){
				this.setTile(i, 0, k, (i % 2 == 0) == (k % 2 == 0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
				this.setTile(i, Y_TILES - 1, k, (i % 2 == 0) != (k % 2 == 0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
			}
		}
		
		// Make east/west walls
		for(int i = 0; i < X_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				this.setTile(i, j, 0, (i % 2 == 0) != (j % 2 == 0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
				this.setTile(i, j, Z_TILES - 1, (i % 2 == 0) == (j % 2 == 0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
			}
		}
		
		// Make north/south walls
		for(int i = 0; i < Z_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				this.setTile(0, j, i, (i % 2 == 0) != (j % 2 == 0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
				this.setTile(X_TILES - 1, j, i, (i % 2 == 0) == (j % 2 == 0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
			}
		}
		
		// Add the door
		var levelDoor = new LevelDoor(X_TILES - 1.25, 1, Z_TILES - 3, this.getLevel() + 1, EAST);
		this.addThing(levelDoor);
		
		// Put tiles in front of the door, mostly for testing
		this.setTile(X_TILES - 3, 1, Z_TILES - 3, BaseTiles3D.SOLID_DARK);
		this.setTile(X_TILES - 3, 2, Z_TILES - 4, BaseTiles3D.SOLID_LIGHT);
		
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
	public void render(Game game, Renderer r){
		// Draw the main rendering
		super.render(game, r);
		
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