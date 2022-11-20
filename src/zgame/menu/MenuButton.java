package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.ZRect;

/** A {@link MenuText} which can be clicked to perform an action */
public class MenuButton extends MenuText{
	
	/** The default value of {@link #doubleClickThreshold} */
	public static final long DEFAULT_DOUBLE_CLICK_THRESHOLD = 500;
	
	/** The color to display on top of the button as a highlight, by default happens when the mouse hovers over it */
	private ZColor highlightColor;
	
	/** The last time this button was clicked, or -1 if the button has never been clicked */
	private long lastClick;
	
	/** The amount of time, in milliseconds that can pass between clicks for it to count as a double click */
	private long doubleClickThreshold;
	
	/**
	 * Create a blank {@link MenuButton} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param game The game associated with this thing
	 */
	public MenuButton(double x, double y, double w, double h, Game game){
		this(x, y, w, h, "", game);
	}
	
	/**
	 * Create a {@link MenuButton} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 * @param game The game associated with this thing
	 */
	public MenuButton(double x, double y, double w, double h, String text, Game game){
		super(x, y, w, h, text, game);
		this.highlightColor = new ZColor(0, .2);
		
		this.lastClick = -1;
		this.doubleClickThreshold = DEFAULT_DOUBLE_CLICK_THRESHOLD;
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		if(this.showHighlight(game)){
			r.setColor(this.getHighlightColor());
			r.drawRectangle(bounds);
		}
	}
	
	/**
	 * Call {@link #click()} if the mouse was released while on top of this button
	 */
	@Override
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.mouseAction(game, button, press, shift, alt, ctrl);
		ZMouseInput mi = game.getMouseInput();
		if(!press && this.getBounds().contains(mi.x(), mi.y())){
			if(System.currentTimeMillis() - this.getLastClick() <= this.getDoubleClickThreshold()) this.doubleClick(game);
			this.click(game);
			this.lastClick = System.currentTimeMillis();
		}
	}
	
	/**
	 * A method that is called when this button is activated, i.e. clicked on.
	 * Override this method to perform an action when the button is clicked
	 * 
	 * @param game The {@link Game} which was used when the button was clicked
	 */
	public void click(Game game){
		
	}
	
	/**
	 * A method that is called when this button is double clicked, i.e. clicked once, then clicked again, usually after a short time.
	 * Time can be changed based on {@link #doubleClickThreshold}
	 * Override this method to perform an action when the button is double clicked
	 * 
	 * @param game The {@link Game} which was used when the button was double clicked
	 */
	public void doubleClick(Game game){
		
	}
	
	/**
	 * @param The game used by this {@link MenuButton}
	 * @return true if a highlight on top of this button should render. By default renders when the mouse is over it
	 */
	public boolean showHighlight(Game game){
		return this.getBounds().contains(game.mouseSX(), game.mouseSY());
	}
	
	/** @return See {@link #highlightColor} */
	public ZColor getHighlightColor(){
		return highlightColor;
	}
	
	/** @param highlightColor See {@link #highlightColor} */
	public void setHighlightColor(ZColor highlightColor){
		this.highlightColor = highlightColor;
	}
	
	/** @return See {@link #highlightColor} */
	public long getLastClick(){
		return this.lastClick;
	}
	
	/** @return See {@link #doubleClickThreshold} */
	public long getDoubleClickThreshold(){
		return this.doubleClickThreshold;
	}
	
	/** @param doubleClickThreshold See {@link #doubleClickThreshold} */
	public void setDoubleClickThreshold(long doubleClickThreshold){
		this.doubleClickThreshold = doubleClickThreshold;
	}
	
}
