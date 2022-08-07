package zgame.menu;

import zgame.core.Game;
import zgame.core.input.mouse.ZMouseInput;

/** A {@link MenuText} which can be clicked to perform an action */
public class MenuButton<D> extends MenuText<D>{
	
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
	}

	/**
	 * Call {@link #click()} if the mouse was released while on top of this button
	 */
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.mouseAction(game, button, press, shift, alt, ctrl);
		ZMouseInput mi = game.getMouseInput();
		if(this.getBounds().contains(mi.x(), mi.y())){
			this.click(game);
		}
	}

	/**
	 * A method that is called when this button is activated, i.e. clicked on.
	 * Override this method to perform an action when the button is clicked
	 * 
	 * @param game The {@link Game} which was used when the button was clicked
	 */
	public void click(Game<D> game){

	}

}
