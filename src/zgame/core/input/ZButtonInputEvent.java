package zgame.core.input;

/**
 * A class which contains the basic universal information used by input devices
 */
public abstract class ZButtonInputEvent{
	
	/**
	 * The numerical value which represents the specific button or action that was activated during the event.
	 * This could be mouse buttons, keys on a keyboard, buttons on a video game controller, etc
	 */
	private int id;
	
	/** true if a shift key was held during this event, false otherwise */
	private boolean shiftDown;
	
	/** true if an alt key was held during this event, false otherwise */
	private boolean altDown;
	
	/** true if a ctrl key was held during this event, false otherwise */
	private boolean ctrlDown;
	
	/** true if this event was pressing a button down, false otherwise, i.e. a release of the button */
	private boolean press;
	
	/**
	 * Create a new {@link ZButtonInputEvent} with the given information
	 * 
	 * @param id See {@link #id}
	 * @param window See {@link #window}
	 * @param shiftDown See {@link #shiftDown}
	 * @param altDown See {@link #altDown}
	 * @param ctrlDown See {@link #ctrlDown}
	 * @param press See {@link #press}
	 */
	public ZButtonInputEvent(int id, boolean shiftDown, boolean altDown, boolean ctrlDown, boolean press){
		this.id = id;
		this.shiftDown = shiftDown;
		this.altDown = altDown;
		this.ctrlDown = ctrlDown;
		this.press = press;
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	/** @return See {@link #shiftDown} */
	public boolean isShiftDown(){
		return this.shiftDown;
	}
	
	/** @return See {@link #altDown} */
	public boolean isAltDown(){
		return this.altDown;
	}
	
	/** @return See {@link #ctrlDown} */
	public boolean isCtrlDown(){
		return this.ctrlDown;
	}

	/** @return See {@link #press} */
	public boolean isPress(){
		return this.press;
	}
	
}
