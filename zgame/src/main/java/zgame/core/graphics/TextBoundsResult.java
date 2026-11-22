package zgame.core.graphics;

/**
 * An object which keeps track of values for bounds containing text
 * @param text The text with newlines in a bounds
 * @param width The width of the bounds
 * @param height The height of the bounds
 */
public record TextBoundsResult(String text, double width, double height){
	/** @return See {@link #text} */
	@Override
	public String text(){
		return this.text;
	}
	
	/** @return See {@link #width} */
	@Override
	public double width(){
		return this.width;
	}
	
	/** @return See {@link #height} */
	@Override
	public double height(){
		return this.height;
	}
}
