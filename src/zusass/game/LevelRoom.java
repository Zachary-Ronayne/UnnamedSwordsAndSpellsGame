package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZMathUtils;
import zgame.things.Room;
import zgame.things.tiles.Tile;
import zusass.game.tiles.ZUSASSColorTiles;

/** A {@link Room} which represents a randomly generated level for the infinite dungeons */
public class LevelRoom extends Room{
	
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
	private ZColor checker1;
	/** The second color of the checkerboard pattern of this room */
	private ZColor checker2;
	
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
				this.getTiles()[i][j] = new Tile(i, j, (i0 == j0) ? ZUSASSColorTiles.BACK_COLOR : ZUSASSColorTiles.BACK_COLOR_DARK);
			}
		}
		this.setLevel(level);
		
		this.addThing(new LevelDoor(this.getLevel() + 1, this));
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
	
	/** @param See {@link #level} */
	private void setLevel(int level){
		this.level = Math.max(1, level);
		
		// Represent the level number as a boolean array
		this.levelDisp = ZMathUtils.intToBoolArr(this.level);
	}
	
	@Override
	public void render(Game game, Renderer r){
		ZUSASSColorTiles.setColors(this.checker1, this.checker2);

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
	
}