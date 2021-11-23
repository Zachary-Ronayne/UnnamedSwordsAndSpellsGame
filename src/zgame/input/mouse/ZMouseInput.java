package zgame.input.mouse;

import java.util.HashMap;
import java.util.Map;

import zgame.GameWindow;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A class that handles tracking mouse input for a {@link zgame.GameWindow}
 * Coordinates are tracked as (0, 0) being the upper lefthand corner of the rendered screen of the {@link zgame.GameWindow},
 * then x represents the number of rendered pixels left of that corner,
 * and y represents the number of rendered pixels below that corner.
 * This means that coordinates do not necessarily exactly correspond to the pixels on the window
 */
public class ZMouseInput{
	
	/** The {@link GameWindow} using this {@link ZMouseInput} */
	private GameWindow window;
	
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
	
	/** The {@link Map} storing the state of every mouse button and its associated actions */
	private Map<Integer, ZMouseEvent> buttonsDown;
	
	/** Create a simple {@link ZMouseInput} and initialize every value */
	public ZMouseInput(GameWindow window){
		this.window = window;
		
		this.currentX = 0;
		this.currentY = 0;
		this.lastX = 0;
		this.lastX = 0;
		
		this.lastScroll = 0;
		this.scrollAmount = 0;
		
		this.buttonsDown = new HashMap<Integer, ZMouseEvent>();
	}
	
	/**
	 * The method called by GLFW as the mouse press callback
	 * 
	 * @param window The id of the GLFW window where the button was pressed
	 * @param button The mouse button which was pressed
	 * @param action The action of the button, i.e. up or down
	 * @param mods The additional buttons pressed, i.e. shift, alt, ctrl
	 */
	public void mousePress(long window, int button, int action, int mods){
		boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
		boolean alt = (mods & GLFW_MOD_ALT) != 0;
		boolean ctrl = (mods & GLFW_MOD_CONTROL) != 0;
		this.buttonsDown.put(button, new ZMouseEvent(this.x(), this.y(), button, this.getWindow(), shift, alt, ctrl, action == GLFW_PRESS));
	}
	
	/**
	 * The method called by GLFW as the mouse clicked
	 * 
	 * @param window The id of the GLFW window where the mouse was moved
	 * @param x The raw x pixel coordinate on the GLFW window
	 * @param y The raw y pixel coordinate on the GLFW window
	 */
	public void mouseMove(long window, double x, double y){
		this.lastX = this.currentX;
		this.lastY = this.currentY;
		this.currentX = this.window.windowToScreenX(x);
		this.currentY = this.window.windowToScreenY(y);
	}
	
	/**
	 * The method called by GLFW when the mouse wheel is moved
	 * 
	 * @param window The id of the GLFW window where the mouse was moved
	 * @param x The amount the scroll wheel was moved on the x axis, unused
	 * @param y The amount the scroll wheel was moved on the y axis, i.e. number of scrolls, 1 for scroll up, -1 for scroll down
	 */
	public void mouseWheelMove(long window, double x, double y){
		this.scrollAmount += y;
		this.lastScroll = y;
	}
	
	/** @return See {@link #window} */
	public GameWindow getWindow(){
		return this.window;
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
	
	/**
	 * Get a {@link ZMouseEvent} containing information about the desired button
	 * 
	 * @param button The ID of the button, these should be referred to useing GLFW mouse constants,
	 *        i.e. GLFW_MOUSE_BUTTON_1 through GLFW_MOUSE_BUTTON_8,
	 *        or GLFW_MOUSE_BUTTON_LEFT, GLFW_MOUSE_BUTTON_RIGHT, GLFW_MOUSE_BUTTON_MIDDLE
	 * @return The event, or null if no such event exists
	 */
	public ZMouseEvent buttonEvent(int button){
		return this.buttonsDown.get(button);
	}
	
	/**
	 * Determine if a particular mouse button is pressed
	 * 
	 * @param button The button to check, same conditions as {@link #buttonEvent(int)}
	 * @return true if the button is pressed, false otherwise. Will also return false if button represents an invalid button
	 */
	public boolean buttonDown(int button){
		ZMouseEvent e = this.buttonEvent(button);
		if(e == null) return false;
		return e.isPress();
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
