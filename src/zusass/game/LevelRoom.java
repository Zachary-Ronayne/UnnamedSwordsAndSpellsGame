package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZMath;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.game.things.LevelDoor;
import zusass.game.things.entities.mobs.Npc;
import zusass.game.things.tiles.ZusassColorTiles;
import zusass.utils.ZusassConvert;

/** A {@link Room} which represents a randomly generated level for the infinite dungeons */
public class LevelRoom extends ZusassRoom{
	
	/** The number of tiles in a {@link LevelRoom} on the x axis */
	private static final int X_TILES = 15;
	/** The number of tiles in a {@link LevelRoom} on the y axis */
	private static final int Y_TILES = 8;
	
	/**
	 * The numerical value of the level, i.e. level 1 is the easiest, level 2 is harder, etc.
	 * If this value is less than 1, it is set to 1
	 */
	private int level;
	
	/** An array used for displaying the level number in a boolean way */
	private boolean[] levelDisp;
	
	/** The first color of the checkerboard pattern of this room */
	private final ZColor checker1;
	/** The second color of the checkerboard pattern of this room */
	private final ZColor checker2;
	
	/** true if this room has its completion requirements satisfied, and the player is able to leave the room */
	private boolean roomCleared;
	/** The number of enemies still in the room */
	private int enemiesRemaining;
	
	/**
	 * Create a new randomly generated level
	 *
	 * @param level See {@link #level}
	 */
	public LevelRoom(int level){
		super(X_TILES, Y_TILES);
		
		// Set up the tiles
		this.checker1 = new ZColor(0.2 + Math.random() * 0.5, 0.2 + Math.random() * 0.5, 0.2 + Math.random() * 0.5);
		this.checker2 = new ZColor(checker1.red() * 0.5, checker1.green() * 0.5, checker1.blue() * 0.5);
		for(int i = 0; i < X_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				boolean i0 = i % 2 == 0;
				boolean j0 = j % 2 == 0;
				this.setTile(i, j, (i0 == j0) ? ZusassColorTiles.BACK_COLOR : ZusassColorTiles.BACK_COLOR_DARK);
			}
		}
		this.setLevel(level);
		
		// Add the door
		this.addThing(new LevelDoor(this.getLevel() + 1, this));
		
		// Add enemies
		Npc enemy = new Npc(400, 400, 60, 80);
		enemy.getWalk().setWalkSpeedMax(100 + 100 * (1 - (10 / (level + 10.0))));
		enemy.getStats().setMaxHealth(10 + level * 3);
		enemy.getStats().setStrength(10 + level);
		enemy.healToMaxHealth();
		this.addThing(enemy);
	}
	
	@Override
	public void addThing(GameThing thing){
		super.addThing(thing);
		// When adding an npc, keep track of that
		if(ZusassConvert.toNpc(thing) != null){
			this.enemiesRemaining++;
			this.roomCleared = false;
		}
	}
	
	@Override
	public void tickRemoveThing(GameThing thing){
		super.tickRemoveThing(thing);
		// When removing an npc, keep track of that
		if(ZusassConvert.toNpc(thing) != null){
			this.enemiesRemaining--;
			if(this.enemiesRemaining <= 0) this.roomCleared = true;
		}
	}
	
	/** @return See {@link #roomCleared} */
	public boolean isRoomCleared(){
		return this.roomCleared;
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
		
		// Represent the level number as a boolean array
		this.levelDisp = ZMath.intToBoolArr(this.level);
	}
	
	@Override
	public void render(Game game, Renderer r){
		ZusassColorTiles.setColors(this.checker1, this.checker2);
		
		// Draw the main rendering
		super.render(game, r);
		
		// Draw the actual level counter
		r.setColor(.8, .8, .8);
		r.setFontSize(32);
		r.drawText(0, -2, Integer.toString(this.getLevel()));
		
		// Draw a level counter using a boolean way
		for(int i = 0; i < this.levelDisp.length; i++){
			r.setColor(new ZColor(0));
			r.drawRectangle(10 + 12 * i, 150, 10, 20);
			if(this.levelDisp[i]){
				r.setColor(new ZColor(1));
				r.drawRectangle(12 + 12 * i, 152, 6, 16);
			}
		}
	}
	
	@Override
	public LevelRoom asLevel(){
		return this;
	}
	
}