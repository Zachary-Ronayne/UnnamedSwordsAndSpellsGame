package zgame.menu.togglebutton;

/** An object holding a boolean and display value for a {@link BoolToggleButton} */
public class BoolToggleButtonValue implements ToggleButtonValue{
	
	/** the boolean field used by this button */
	private boolean isTrue;
	/** The text used to display this button */
	private String text;
	
	/**
	 * Create a new value
	 * @param isTrue See {@link #isTrue}
	 * @param text See {@link #text}
	 */
	public BoolToggleButtonValue(boolean isTrue, String text){
		this.isTrue = isTrue;
		this.text = text;
	}
	
	/** @return See {@link #isTrue} */
	public boolean isTrue(){
		return this.isTrue;
	}
	
	/** @param aTrue See {@link #isTrue} */
	public void setTrue(boolean aTrue){
		this.isTrue = aTrue;
	}
	
	@Override
	public String getText(){
		return this.text;
	}
	
	/** @param text See {@link #text} */
	public void setText(String text){
		this.text = text;
	}
}
