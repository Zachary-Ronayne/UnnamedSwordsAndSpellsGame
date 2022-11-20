package zgame.core.graphics.camera;

/**
 * An object that tracks a single axis zoom and position for a {@link GameCamera}
 */
public class CameraAxis{
	
	/** The position on the axis */
	private double pos;
	
	/** The factor determining the zoom level, not the value used to scale the camera. Negative values zoom out, positive values zoom in, and zero means no zoom */
	private double zoomFactor;
	/** The zoom level of the camera on the x axis, in the range (0, infinity), i.e. the raw number used to scale on the axis */
	private double zoomScale;
	/** The inverse of {@link #zoomScale} */
	private double zoomScaleInverse;
	/** The value that determines how fast the camera zooms, i.e. zoomScale = zoomPower ^ zoomFactor */
	private double zoomPower;
	
	/**
	 * Create a {@link CameraAxis} in a default state, no translation or zooming, and a {@link #zoomPower} of 2
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
	
	/** Reset the axis camera to a default state, i.e. no translation or zooming */
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
		this.zoomScale = 0;
		this.zoomPower = zoomPower;
		this.setZoomFactor(zoomFactor);
	}
	
	/** @return See {@link #pos} */
	public double getPos(){
		return this.pos;
	}
	
	/** @param pos See {@link #pos} */
	public void setPos(double pos){
		this.pos = pos;
	}
	
	/** @param p The amount to add to {@link #pos} */
	public void add(double p){
		this.setPos(this.getPos() + p);
	}
	
	/** @return See {@link #zoomScale} */
	public double getZoomScale(){
		return this.zoomScale;
	}
	
	/** @return See {@link #zoomScaleInverse} */
	public double getZoomScaleInverse(){
		return this.zoomScaleInverse;
	}
	
	/** @return See {@link #zoomFactor} */
	public double getZoomFactor(){
		return this.zoomFactor;
	}
	
	/** @param zoomFactor See {@link #zoomFactor} */
	public void setZoomFactor(double zoomFactor){
		this.zoomFactor = zoomFactor;
		this.zoomScale = Math.pow(this.getZoomPower(), this.getZoomFactor());
		this.zoomScaleInverse = 1 / this.zoomScale;
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
	 * Zoom in, then reposition the camera so that the given position is zoomed towards.
	 * After zooming, the position will be such that it is proportionally at the same location relative to the given p and size
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 * @param p The position to base the zoom reposition
	 * @param size The size of the area to base the zoom reposition
	 */
	public void zoom(double zoom, double p, double size){
		// Find the given size, opposite zoomed by the current zoom level
		double baseSize = size * this.getZoomScaleInverse();
		// Find the percentage of the given size which the given position takes up
		double perc = (p - this.getPos()) / size;
		// Perform the zoom
		this.zoom(zoom);
		// Based on the new zoom level, find the new size by using the normal zoom level
		double newSize = baseSize * this.getZoomScale();
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
		return p * this.getZoomScale() + this.getPos();
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in game space coordinates
	 * 
	 * @param p The value to convert
	 * @return The value in game coordinates
	 */
	public double screenToGame(double p){
		return (p - this.getPos()) * this.getZoomScaleInverse();
	}
	
	/**
	 * Convert a size in game space, to a size in screen space
	 * 
	 * @param p The value to convert
	 * @return The converted value
	 */
	public double sizeGameToScreen(double p){
		return p * this.getZoomScale();
	}
	
	/**
	 * Convert a size in screen space, to a size in game space
	 * 
	 * @param p The value to convert
	 * @return The converted value
	 */
	public double sizeScreenToGame(double p){
		return p * this.getZoomScaleInverse();
	}
	
}
