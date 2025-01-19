package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.still.tiles.BaseTiles3D;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.things.LevelDoor;
import zusass.game.things.ZusassTags;
import zusass.game.things.entities.mobs.Npc;
import zusass.game.things.tiles.ZusassColorTiles;

/** A {@link Room} which represents a randomly generated level for the infinite dungeons */
public class LevelRoom extends ZusassRoom{
	
	/** The number of tiles in a {@link LevelRoom} on the x axis */
	private static final int X_TILES = 9;
	/** The number of tiles in a {@link LevelRoom} on the y axis */
	private static final int Y_TILES = 5;
	/** The number of tiles in a {@link LevelRoom} on the z axis */
	private static final int Z_TILES = 7;
	
	/**
	 * The numerical value of the level, i.e. level 1 is the easiest, level 2 is harder, etc.
	 * If this value is less than 1, it is set to 1
	 */
	private int level;
	
	/** The first color of the checkerboard pattern of this room */
	private ZColor checker1;
	/** The second color of the checkerboard pattern of this room */
	private ZColor checker2;
	
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
		var levelDoor = new LevelDoor(X_TILES - 3, 1, Z_TILES - 1.25, this.getLevel() + 1, this);
		this.addThing(levelDoor);
		
		// TODO add the actual enemy
//		// issue#25 if this is changed to add hundreds of enemies, the TPS tanks while not using all the computer's resources. Probably need to make tick looper account for time spent rendering
//		// Add enemies
//		Npc enemy = new Npc(400, 400, 60, 80);
//		enemy.setStat(ENDURANCE, 2 + 6 * (1 - (10 / (this.level + 10.0))));
//		enemy.setStat(STRENGTH, 10);
//		enemy.getStat(STRENGTH).addModifier(enemy.getUuid(), this.level, ModifierType.ADD);
//		enemy.setResourcesMax();
//		this.addThing(enemy);
	}
	
	/** @return true if this room is cleared and can be exited, false otherwise */
	public boolean isRoomCleared(){
		return this.getEnemiesRemainingCount() <= 0;
	}
	
	public int getEnemiesRemainingCount(){
		return this.getAllThings().get(Npc.class).size();
	}
	
	/** @return See {@link #checker1} */
	public ZColor getChecker1(){
		return this.checker1;
	}
	
	/** @return See {@link #checker2} */
	public ZColor getChecker2(){
		return this.checker2;
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
		ZusassColorTiles.setColors(this.checker1, this.checker2);
		
		// Draw the main rendering
		super.render(game, r);
		
		// TODO properly draw some text on the wall for the level counter
		// Draw a numerical level counter
//		r.setColor(.8, .8, .8);
//		r.setFontSize(32);
//		r.drawText(0, -2, Integer.toString(this.getLevel()));
	}
	
}