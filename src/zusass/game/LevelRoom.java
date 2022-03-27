package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZMathUtils;
import zgame.core.utils.ZRenderUtils;
import zgame.things.Room;

/** A {@link Room} which represents a randomly generated level for the infinite dungeons */
public class LevelRoom extends Room{
	
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
		super();
		this.setLevel(level);
		this.setWidth(1000);
		this.setHeight(500);
		
		this.checker1 = new ZColor(0.2 + Math.random() * 0.5, 0.2 + Math.random() * 0.5, 0.2 + Math.random() * 0.5);
		this.checker2 = new ZColor(checker1.red() * 0.5, checker1.green() * 0.5, checker1.blue() * 0.5);
		
		this.addThing(new LevelDoor(this.getLevel() + 1));
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
		// Draw a background
		ZRenderUtils.checkerboard(r, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 10, this.checker1, this.checker2);
		
		// Draw a makeshift level counter using a boolean way
		for(int i = 0; i < this.levelDisp.length; i++){
			r.setColor(new ZColor(0));
			r.drawRectangle(10 + 12 * i, 150, 10, 20);
			if(this.levelDisp[i]){
				r.setColor(new ZColor(1));
				r.drawRectangle(12 + 12 * i, 152, 6, 16);
			}
		}
		
		// Draw the main rendering
		super.render(game, r);
	}
	
}