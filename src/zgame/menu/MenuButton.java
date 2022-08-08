package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.input.mouse.ZMouseInput;

/** A {@link MenuText} which can be clicked to perform an action */
public class MenuButton<D>extends MenuText<D>{
	
	/** The color to display on top of the button as a highlight, by default happens when the mouse hovers over it */
	private ZColor highlightColor;
	
	/**
	 * Create a blank {@link MenuButton} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuButton(double x, double y, double w, double h){
		this(x, y, w, h, "");
	}
	
	/**
	 * Create a {@link MenuButton} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 */
	public MenuButton(double x, double y, double w, double h, String text){
		super(x, y, w, h, text);
		this.highlightColor = new ZColor(0, .2);
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		super.render(game, r);
		if(this.showHighlight(game)){
			r.setColor(this.getHighlightColor());
			r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
	}
	
	/**
	 * Call {@link #click()} if the mouse was released while on top of this button
	 */
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.mouseAction(game, button, press, shift, alt, ctrl);
		ZMouseInput mi = game.getMouseInput();
		if(!press && this.getBounds().contains(mi.x(), mi.y())){ this.click(game); }
	}
	
	/**
	 * A method that is called when this button is activated, i.e. clicked on.
	 * Override this method to perform an action when the button is clicked
	 * 
	 * @param game The {@link Game} which was used when the button was clicked
	 */
	public void click(Game<D> game){
		
	}
	
	/**
	 * @param The game used by this {@link MenuButton}
	 * @return true if a highlight on top of this button should render. By default renders when the mouse is over it
	 */
	public boolean showHighlight(Game<D> game){
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
	
}
