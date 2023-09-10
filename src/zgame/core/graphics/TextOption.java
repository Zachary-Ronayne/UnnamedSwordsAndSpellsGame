package zgame.core.graphics;

/** A class holding data to draw pieces of text */
public class TextOption{
	/** The text to draw */
	private String text;
	/** The color to use for the text */
	private ZColor color;
	/** The way to render the alpha channel */
	private AlphaMode alpha;
	
	/**
	 * Create a new option for just drawing text
	 *
	 * @param text See {@link #text}
	 */
	public TextOption(String text){
		this(text, null, null);
	}
	
	/**
	 * Create the new option
	 *
	 * @param text See {@link #text}
	 * @param color See {@link #color}
	 * @param alpha See {@link #alpha}
	 */
	public TextOption(String text, ZColor color, AlphaMode alpha){
		this.text = text;
		this.color = color;
		this.alpha = alpha;
	}
	
	/** @return See {@link #text} */
	public String getText(){
		return this.text;
	}
	
	/** @param text See {@link #text} */
	public void setText(String text){
		this.text = text;
	}
	
	/** @return See {@link #color} */
	public ZColor getColor(){
		return this.color;
	}
	
	/** @param color See {@link #color} */
	public void setColor(ZColor color){
		this.color = color;
	}
	
	/** @return See {@link #alpha} */
	public AlphaMode getAlpha(){
		return this.alpha;
	}
	
	/** @param alpha See {@link #alpha} */
	public void setAlpha(AlphaMode alpha){
		this.alpha = alpha;
	}
}

