package zgame.menu.format;

import zgame.menu.MenuThing;

/** A {@link MenuFormatter} which aligns the sides of an object to the sides of it's parent by a number of pixels */
public class PixelFormatter implements MenuFormatter{
	
	/** The pixels from the left side to align the object, or null to not align */
	private Double left;
	/** The pixels from the right side to align the object, or null to not align */
	private Double right;
	/** The pixels from the top to align the object, or null to not align */
	private Double top;
	/** The pixels from the bottom side to align the object, or null to not align */
	private Double bottom;
	
	/**
	 * Create a formatter with the same offset on all sides
	 * @param all The offset
	 */
	public PixelFormatter(double all){
		this(all, all, all, all);
	}
	
	/**
	 * Create a formatter with the same offsets
	 * @param left See {@link #left}
	 * @param right See {@link #right}
	 * @param top See {@link #top}
	 * @param bottom See {@link #bottom}
	 */
	public PixelFormatter(Double left, Double right, Double top, Double bottom){
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	/** @return See {@link #left} */
	public Double getLeft(){
		return this.left;
	}
	
	/** @param left See {@link #left} */
	public void setLeft(Double left){
		this.left = left;
	}
	
	/** @return See {@link #right} */
	public Double getRight(){
		return this.right;
	}
	
	/** @param right See {@link #right} */
	public void setRight(Double right){
		this.right = right;
	}
	
	/** @return See {@link #top} */
	public Double getTop(){
		return this.top;
	}
	
	/** @param top See {@link #top} */
	public void setTop(Double top){
		this.top = top;
	}
	
	/** @return See {@link #bottom} */
	public Double getBottom(){
		return this.bottom;
	}
	
	/** @param bottom See {@link #bottom} */
	public void setBottom(Double bottom){
		this.bottom = bottom;
	}
	
	@Override
	public void onWidthChange(MenuThing thing, double width){
		var l = this.getLeft();
		var r = this.getRight();
		if(l != null && r != null) {
			thing.setRelX(l);
			thing.setWidth(width - l - r);
		}
		else if(l == null && r != null) thing.setRelX(width - thing.getWidth() - r);
		else if(l != null) thing.setRelX(l);
	}
	
	@Override
	public void onHeightChange(MenuThing thing, double height){
		var t = this.getTop();
		var b = this.getBottom();
		if(t != null && b != null) {
			thing.setRelY(t);
			thing.setHeight(height - t - b);
		}
		else if(t == null && b != null) thing.setRelY(height - thing.getHeight() - b);
		else if(t != null) thing.setRelY(t);
	
	}
}
