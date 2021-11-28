package zgame.core.input.mouse;

import zgame.core.input.ZButtonInputEvent;

/**
 * A {@link ZButtonInputEvent} which represents a mouse action, i.e. a mouse button press. 
 * See {@link ZMouseInput} for details about coordinates
 */
public class ZMouseEvent extends ZButtonInputEvent{

	/** The x pixel coordiate when the event happened */
	private double x;
	/** The y pixel coordiate when the event happened */
	private double y;

	/**
	 * Create a new {@link ZMouseEvent} with the given information
	 * 
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param id See {@link #getId()}
	 * @param shiftDown See {@link #isShiftDown()}
	 * @param altDown See {@link #isAltDown()}
	 * @param ctrlDown See {@link #isCtrlDown()}
	 * @param press See {@link #isPress()}
	 */
	public ZMouseEvent(double x, double y, int id, boolean shiftDown, boolean altDown, boolean ctrlDown, boolean press){
		super(id, shiftDown, altDown, ctrlDown, press);
		this.x = x;
		this.y = y;
	}

	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
}
