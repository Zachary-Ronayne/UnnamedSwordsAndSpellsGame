package zgame.menu.format;

import zgame.menu.MenuThing;

/** A {@link MenuFormatter} which sets the size of the {@link MenuThing} using this object to a percent of its parent */
public class PercentFormatter implements MenuFormatter{
	
	/** The percent to set the width to, or null to not change it */
	private Double percentWidth;
	/** The percent to set the height to, or null value to not change it */
	private Double percentHeight;
	
	/** The percentage of the way into the parent to place the center of the thing on the x axis, or null to not change it */
	private Double centerX;
	/** The percentage of the way into the parent to place the center of the thing on the y axis, or null to not change it */
	private Double centerY;
	
	/**
	 * @param percentWidth See {@link #percentWidth}
	 * @param percentHeight See {@link #percentHeight}
	 */
	public PercentFormatter(Double percentWidth, Double percentHeight){
		this(percentWidth, percentHeight, null, null);
	}
	
	/**
	 * @param percentWidth See {@link #percentWidth}
	 * @param percentHeight See {@link #percentHeight}
	 */
	public PercentFormatter(Double percentWidth, Double percentHeight, Double centerX, Double centerY){
		this.setPercentWidth(percentWidth);
		this.setPercentHeight(percentHeight);
		this.setCenterX(centerX);
		this.setCenterY(centerY);
	}
	
	/** @return See {@link #percentWidth} */
	public Double getPercentWidth(){
		return this.percentWidth;
	}
	
	/** @param percentWidth See {@link #percentWidth} */
	public void setPercentWidth(Double percentWidth){
		this.percentWidth = percentWidth;
	}
	
	/** @return See {@link #percentHeight} */
	public Double getPercentHeight(){
		return this.percentHeight;
	}
	
	/** @param percentHeight See {@link #percentHeight} */
	public void setPercentHeight(Double percentHeight){
		this.percentHeight = percentHeight;
	}
	
	/** @return See {@link #centerX} */
	public Double getCenterX(){
		return this.centerX;
	}
	
	/** @param centerX See {@link #centerX} */
	public void setCenterX(Double centerX){
		this.centerX = centerX;
	}
	
	/** @return See {@link #centerY} */
	public Double getCenterY(){
		return this.centerY;
	}
	
	/** @param centerY See {@link #centerY} */
	public void setCenterY(Double centerY){
		this.centerY = centerY;
	}
	
	@Override
	public void onWidthChange(MenuThing thing, double width){
		var p = this.getPercentWidth();
		if(p != null) thing.setWidth(width * p);
		p = this.getCenterX();
		if(p != null) thing.setCenterRelX(width * p);
	}
	
	@Override
	public void onHeightChange(MenuThing thing, double height){
		var p = this.getPercentHeight();
		if(p != null) thing.setHeight(height * p);
		p = this.getCenterY();
		if(p != null) thing.setCenterRelY(height * p);
	}
}
