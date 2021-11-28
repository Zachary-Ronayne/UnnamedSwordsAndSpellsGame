package zgame.core.graphics.camera;

/**
 * An object that tracks a single axis zoom and position for a {@link GameCamera}
 */
public class CameraAxis{
	
	/** The position on the axis */
	private double pos;
	
	/** The factor determining the zoom level. Negative values zoom out, positive values zoom in, and zero means no zoom */
	private double zoomFactor;
	/** The zoom level of the camera on the x axis, in the range (0, infinity), i.e. the raw number used to scale on the axis */
	private double zoomLevel;
	/** The inverse of {@link #zoomLevel} */
	private double zoomLevelInverse;
	/** The value that determines how fast the camera zooms, i.e. zoomLevel = zoomPower ^ zoomFactor */
	private double zoomPower;
	
	/**
	 * Create a {@link CameraAxis} in a default state, no translation or zooming
	 */
	public CameraAxis(){
		this(0, 0, 2);
	}
	
	/**
	 * Create a {@link CameraAxis} in the given state
	 * 
	 * @param pos See {@link #pos}
	 * @param zoomFactor See {@link #zoomFactor}
	 * @param zoomPower See {@link #zoomPower}
	 */
	public CameraAxis(double pos, double zoomFactor, double zoomPower){
		this.init(pos, zoomFactor, zoomPower);
	}
	
	/** Reset the axis camera to a defaul state, i.e. no translation or zooming */
	public void reset(){
		this.init(0, 0, this.getZoomPower());
	}
	
	/**
	 * Initialize the axis positions to the given values
	 * 
	 * @param pos See {@link #pos}
	 * @param zoomFactor See {@link #zoomFactor}
	 * @param zoomPower See {@link #zoomPower}
	 */
	private void init(double pos, double zoomFactor, double zoomPower){
		this.setPos(pos);
		this.zoomFactor = 1;
		this.zoomLevel = 0;
		this.zoomPower = zoomPower;
		this.setZoomFactor(zoomFactor);
	}
	
	/** @return See {@link #pos} */
	public double getPos(){
		return this.pos;
	}
	
	/** @param x See {@link #pos} */
	public void setPos(double pos){
		this.pos = pos;
	}
	
	/** @return See {@link #zoomLevel} */
	public double getZoomLevel(){
		return this.zoomLevel;
	}
	
	/** @return See {@link #zoomLevelInverse} */
	public double getZoomLevelInverse(){
		return this.zoomLevelInverse;
	}
	
	/** @return See {@link #zoomFactor} */
	public double getZoomFactor(){
		return this.zoomFactor;
	}
	
	/** @param x See {@link #zoomFactor} */
	public void setZoomFactor(double zoomFactor){
		this.zoomFactor = zoomFactor;
		this.zoomLevel = Math.pow(this.getZoomPower(), this.getZoomFactor());
		this.zoomLevelInverse = 1 / this.zoomLevel;
	}
	
	/** @return See {@link #zoomPower} */
	public double getZoomPower(){
		return this.zoomPower;
	}
	
	/** @param x See {@link #zoomPower} */
	public void setZoomPower(double zoomPower){
		this.zoomPower = zoomPower;
	}
	
	/**
	 * Zoom on the axis, this will only adjust the zoom level, it will not adjust the position at all
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 */
	public void zoom(double zoom){
		this.setZoomFactor(this.getZoomFactor() + zoom);
	}
	
	/**
	 * Zoom in, then reposition the camera so that the given position is zoomed towards
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param p The position to base the zoom reposition
	 * @param size The size of the area to base the zoom reposition
	 */
	public void zoom(double zoom, double p, double size){
		// Find the given size, opposite zoomed by the current zoom level
		double baseSize = size * this.getZoomLevelInverse();
		// Find the percecntage of the given size which the given position takes up
		double perc = (p - this.getPos()) / size;
		// Perform the zoom
		this.zoom(zoom);
		// Based on the new zoom level, find the new size by using the normal zoom level
		double newSize = baseSize * this.getZoomLevel();
		// The new axis position is the given position, minus the amount of the percentage new size which is the same as the percentage of the old size
		this.setPos(p - newSize * perc);
	}
	
	/**
	 * Move this axis by the given amount
	 * 
	 * @param change The amount to move the axis by
	 */
	public void shift(double change){
		this.setPos(this.getPos() + change);
	}
	
	/**
	 * Convert a coordinate value in game space, to a coordinate in screen space coordinates
	 * 
	 * @param p The value to convert
	 * @return The value in screen coordinates
	 */
	public double gameToScreen(double p){
		return p * this.getZoomLevel() + this.getPos();
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in game space coordinates
	 * 
	 * @param p The value to convert
	 * @return The value in game coordinates
	 */
	public double screenToGame(double p){
		return (p - this.getPos()) * this.getZoomLevelInverse();
	}
	
	/**
	 * Convert a size in game space, to a size in screen space
	 * 
	 * @param p The value to convert
	 * @return The converted value
	 */
	public double sizeGameToScreen(double p){
		return p * this.getZoomLevel();
	}
	
	/**
	 * Convert a size in screen space, to a size in game space
	 * 
	 * @param p The value to convert
	 * @return The converted value
	 */
	public double sizeScreenToGame(double p){
		return p * this.getZoomLevelInverse();
	}
	
}
