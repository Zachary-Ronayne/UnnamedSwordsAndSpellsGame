package zgame.core.input.mouse;

import zgame.core.input.ZButtonInput;
import zgame.core.window.GameWindow;

/**
 * A class that handles tracking mouse input for a {@link GameWindow} Coordinates are tracked as (0, 0) being the upper left hand corner of the rendered screen of the
 * {@link GameWindow}, then x represents the number of rendered pixels to the right of that corner, and y represents the number of rendered pixels below that corner. This
 * means that coordinates do not necessarily exactly correspond to the pixels on the window. So if the {@link GameWindow} has black bars on the sides, and the mouse is at the
 * right hand side of the left hand bar, then the x coordinate in this {@link ZMouseInput} would be zero, not the pixel coordinate of the bar position.
 */
public abstract class ZMouseInput extends ZButtonInput<ZMouseEvent>{
	
	/** The current x coordinate */
	private double currentX;
	/** The current y coordinate */
	private double currentY;
	/** The previous x coordinate */
	private double lastX;
	/** The previous y coordinate */
	private double lastY;
	
	/** The amount of distance the scroll wheel moved when it was last scrolled */
	private double lastScroll;
	/** The amount of distance the scroll wheel has moved since this value was last used */
	private double scrollAmount;
	
	/**
	 * Create a simple {@link ZMouseInput} and initialize every value
	 *
	 * @param window The {@link GameWindow} which uses this input object
	 */
	public ZMouseInput(GameWindow window){
		super(window);
		this.currentX = 0;
		this.currentY = 0;
		this.lastX = 0;
		this.lastY = 0;
		
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
	 * @param button The mouse button which is pressed
	 * @param press true if the button was pressed, false for released
	 * @param shift true if shift is pressed, false otherwise
	 * @param alt true if alt is pressed, false otherwise
	 * @param ctrl true if ctrl is pressed, false otherwise
	 */
	public void mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.buttonAction(button, press, shift, alt, ctrl);
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
		GameWindow w = this.getWindow();
		this.currentX = w.windowToScreenX(x);
		this.currentY = w.windowToScreenY(y);
	}
	
	/**
	 * The method called when the mouse wheel scrolls
	 *
	 * @param amount The amount the scroll wheel was moved
	 */
	public void mouseWheelMove(double amount){
		this.scrollAmount += amount;
		this.lastScroll = amount;
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
	
	/** @return The amount of distance the mouse has moved on the x axis since the previous input */
	public double dx(){
		return this.x() - this.lastX();
	}
	
	/** @return The amount of distance the mouse has moved on the y axis since the previous input */
	public double dy(){
		return this.y() - this.lastY();
	}
	
	/** @return See {@link #lastScroll} */
	public double lastScroll(){
		return this.lastScroll;
	}
	
	/** @return The scroll amount returned by {@link #useScrollAmount()} but this method does not reset the value */
	public double getScrollAmount(){
		return this.scrollAmount;
	}
	
	/**
	 * Get the amount of distance the scroll wheel has moved since the last time this method was called. After calling this method, this method will return 0 until the scroll
	 * wheel has been moved
	 *
	 * @return The scroll amount used, positive for scroll up, negative for scroll down
	 */
	public double useScrollAmount(){
		double s = this.scrollAmount;
		this.scrollAmount = 0;
		return s;
	}
	
	/** @return true if the left mouse button is down, false otherwise */
	public abstract boolean leftDown();
	
	/** @return true if the right mouse button is down, false otherwise */
	public abstract boolean rightDown();
	
	/** @return true if the middle mouse button is down, false otherwise */
	public abstract boolean middleDown();
	
}
