package zgame.menu;

/** A {@link MenuThing} that holds text */
public class MenuText extends MenuThing{

	/** The text held by this {@link MenuText} */
	private String text;

	/**
	 * Create a blank {@link MenuText} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuText(double x, double y, double w, double h){
		this(x, y, w, h, "");
	}
	
	/**
	 * Create a {@link MenuText} at the given position and size
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param text The text to display
	 */
	public MenuText(double x, double y, double w, double h, String text){
		super(x, y, w, h);
		this.text = text;
		this.setFill(this.getFill().solid());
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.text;
	}

	/** @param text See {@link #text} */
	public void setText(String text){
		this.text = text;
	}
	
}
