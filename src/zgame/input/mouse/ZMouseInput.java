package zgame.input.mouse;

import zgame.Game;
import zgame.GameWindow;
import zgame.input.ZButtonInput;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A class that handles tracking mouse input for a {@link zgame.GameWindow}
 * Coordinates are tracked as (0, 0) being the upper lefthand corner of the rendered screen of the {@link zgame.GameWindow},
 * then x represents the number of rendered pixels left of that corner,
 * and y represents the number of rendered pixels below that corner.
 * This means that coordinates do not necessarily exactly correspond to the pixels on the window
 */
public class ZMouseInput extends ZButtonInput<ZMouseEvent>{
	
	/** The current x coordinate */
	private double currentX;
	/** The current y coordinate */
	private double currentY;
	/** The previous x coordinate */
	private double lastX;
	/** The previous y coordinate */
	private double lastY;
	
	/** The amount of distance the scrollwheel moved when it was last scrolled */
	private double lastScroll;
	/** The amount of distance the scrollwheel has moved since this value was last used */
	private double scrollAmount;
	
	/** Create a simple {@link ZMouseInput} and initialize every value
	 * 
	 * @param game the Game which uses this input object
	 */
	public ZMouseInput(Game game){
		super(game);
		this.currentX = 0;
		this.currentY = 0;
		this.lastX = 0;
		this.lastX = 0;
		
		this.lastScroll = 0;
		this.scrollAmount = 0;
	}

	@Override
	public ZMouseEvent createEvent(int button, boolean shift, boolean alt, boolean ctrl, boolean press){
		return new ZMouseEvent(this.x(), this.y(), button, shift, alt, ctrl, press);
	}
	
	/**
	 * The method called when a mouse button is pressed
	 * 
	 * @param button The mouse button which was pressed
	 * @param action The action of the button, i.e. up or down
	 * @param mods The additional buttons pressed, i.e. shift, alt, ctrl
	 */
	public void mousePress(int button, int action, int mods){
		this.buttonPress(button, action, mods);
	}
	
	/**
	 * The method called when the mouse moves
	 * 
	 * @param x The raw x pixel coordinate on the GLFW window
	 * @param y The raw y pixel coordinate on the GLFW window
	 */
	public void mouseMove(double x, double y){
		this.lastX = this.currentX;
		this.lastY = this.currentY;
		GameWindow w = this.getGame().getWindow();
		this.currentX = w.windowToScreenX(x);
		this.currentY = w.windowToScreenY(y);
	}
	
	/**
	 * The method called when the mouse wheel scrolls
	 * 
	 * @param x The amount the scroll wheel was moved on the x axis, unused
	 * @param y The amount the scroll wheel was moved on the y axis, i.e. number of scrolls, 1 for scroll up, -1 for scroll down
	 */
	public void mouseWheelMove(double x, double y){
		this.scrollAmount += y;
		this.lastScroll = y;
	}
	
	/** @return The current x position of the mouse, in screen coordinates */
	public double x(){
		return this.currentX;
	}
	
	/** @return The current y position of the mouse, in screen coordinates */
	public double y(){
		return this.currentY;
	}
	
	/** @return The last x position of the mouse before its current position, in screen coordinates */
	public double lastX(){
		return this.lastX;
	}
	
	/** @return The last y position of the mouse before its current position, in screen coordinates */
	public double lastY(){
		return this.lastY;
	}
	
	/** @return See {@link #lastScroll} */
	public double lastScroll(){
		return this.lastScroll;
	}
	
	/** @return The scroll amount returned by {@link #useScrollAmount()} but, do not reset the value */
	public double getScrollAmount(){
		return this.scrollAmount;
	}
	
	/**
	 * Get the amount of distance the scroll wheel has moved since the last time this method was called.
	 * After calling this method, this method will return 0 until the scroll wheel has been moved
	 * 
	 * @return The scroll amount used, positive for scroll up, negative for scroll down
	 */
	public double useScrollAmount(){
		double s = this.scrollAmount;
		this.scrollAmount = 0;
		return s;
	}
	
	/** @return true if the left mouse button is down, false otherwise */
	public boolean leftDown(){
		return this.buttonDown(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	/** @return true if the right mouse button is down, false otherwise */
	public boolean rightDown(){
		return this.buttonDown(GLFW_MOUSE_BUTTON_RIGHT);
	}
	
	/** @return true if the middle mouse button is down, false otherwise */
	public boolean middleDown(){
		return this.buttonDown(GLFW_MOUSE_BUTTON_MIDDLE);
	}
	
}
