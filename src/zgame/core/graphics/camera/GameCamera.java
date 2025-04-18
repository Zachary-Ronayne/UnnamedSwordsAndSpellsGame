package zgame.core.graphics.camera;

import zgame.core.graphics.Renderer;
import zgame.core.utils.ZRect2D;
import zgame.core.window.GameWindow;

/**
 * A class used by {@link Renderer} to track the location of where objects should be drawn, based on their position in the game.
 * All positions are in game coordinates
 */
public class GameCamera{
	
	/** The x axis of the camera */
	private final CameraAxis x;
	
	/** The y axis of the camera */
	private final CameraAxis y;
	
	/** The x axis anchor position for panning */
	private double anchorX;
	/** The y axis anchor position for panning */
	private double anchorY;
	/** true if an anchor is set, false otherwise. If this value is false, panning cannot occur */
	private boolean anchored;
	
	/**
	 * Create a {@link GameCamera} in a default state, no translation or zooming
	 */
	public GameCamera(){
		this.x = new CameraAxis();
		this.y = new CameraAxis();
		this.releaseAnchor();
	}
	
	/** Reset the camera state to having no translations or zooming */
	public void reset(){
		this.x.reset();
		this.y.reset();
		this.releaseAnchor();
	}
	
	/**
	 * Apply OpenGL transformations to position objects to where the camera should be, based on the given {@link GameWindow}
	 * This translation will treat {@link #x} and {@link #y} positions as the upper left hand corner of the screen
	 *
	 * @param window The {@link GameWindow}
	 */
	public void transform(GameWindow window){
		// Find the distance the camera must travel, in OpenGL coordinates
		double x = window.sizeScreenToGlX(this.getX().getPos());
		double y = -window.sizeScreenToGlY(this.getY().getPos());
		Renderer r = window.getRenderer();
		
		// OpenGL transformations occur in reverse order
		// Lastly, translate the camera to its actual position
		r.translate(x, y);
		// Third, adjust the camera back so that the upper left hand corner is in the correct position
		r.translate(-1, 1);
		// Second, scale to the appropriate size, based on the axis zoom levels
		r.scale(this.getX().getZoomScale(), this.getY().getZoomScale());
		// First, adjust the camera so that the upper left hand corner, i.e. game coordinates (0, 0) is the center to use for scaling
		r.translate(1, -1);
	}
	
	/**
	 * Set the anchor position for panning. Once set, all calls to {@link #pan(double, double)} will be relative to this position until it is set again
	 * If an anchor is already set, this method does nothing
	 *
	 * @param x The x position of the anchor
	 * @param y The y position of the anchor
	 */
	public void setAnchor(double x, double y){
		if(this.isAnchored()) return;
		this.anchorX = this.getX().getPos() - x;
		this.anchorY = this.getY().getPos() - y;
		this.anchored = true;
	}
	
	/** Release the anchor position so that a new one can be set. After this method is called, panning cannot occur until a new anchor is set */
	public void releaseAnchor(){
		this.anchored = false;
	}
	
	/**
	 * Move the camera based on the anchor position.
	 * If no anchor is set, this method does nothing
	 *
	 * @param x The position to move on the x axis, relative to the anchor
	 * @param y The position to move on the y axis, relative to the anchor
	 */
	public void pan(double x, double y){
		if(!this.isAnchored()) return;
		this.getX().setPos(x + this.getAnchorX());
		this.getY().setPos(y + this.getAnchorY());
	}
	
	/** @return See {@link #anchorX} */
	public double getAnchorX(){
		return this.anchorX;
	}
	
	/** @return See {@link #anchorY} */
	public double getAnchorY(){
		return this.anchorY;
	}
	
	/** @return See {@link #anchored} */
	public boolean isAnchored(){
		return this.anchored;
	}
	
	/**
	 * Zoom in on both axis. This method does not adjust camera position
	 *
	 * @param zoom The factor to zoom in by, which will be added to {@link CameraAxis#zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
	 */
	public void zoom(double zoom){
		this.getX().zoom(zoom);
		this.getY().zoom(zoom);
	}
	
	/**
	 * Move both axes by the given amount
	 *
	 * @param x The amount to move the x axis
	 * @param y The amount to move the y axis
	 */
	public void shift(double x, double y){
		this.getX().shift(x);
		this.getY().shift(y);
	}
	
	/**
	 * Set the position of both axes
	 *
	 * @param x The new x axis position
	 * @param y The new y axis position
	 */
	public void setPos(double x, double y){
		this.getX().setPos(x);
		this.getY().setPos(y);
	}
	
	/**
	 * Convert an x coordinate value in game space, to a coordinate in screen space coordinates
	 *
	 * @return The value in screen coordinates
	 */
	public double gameToScreenX(double x){
		return this.getX().gameToScreen(x);
	}
	
	/**
	 * Convert a y coordinate value in game space, to a coordinate in screen space coordinates
	 *
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double gameToScreenY(double y){
		return this.getY().gameToScreen(y);
	}
	
	/**
	 * Convert the bounds of a rectangle in game coordinates to the bounds in screen coordinates
	 *
	 * @param r The bounds
	 * @return The converted bounds
	 */
	public ZRect2D boundsGameToScreen(ZRect2D r){
		return this.boundsGameToScreen(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Convert the bounds of a rectangle in game coordinates to the bounds in screen coordinates
	 *
	 * @param x The x coordinate of the upper right hand corner of the rectangle
	 * @param y The y coordinate of the upper right hand corner of the rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return The converted bounds
	 */
	public ZRect2D boundsGameToScreen(double x, double y, double w, double h){
		return new ZRect2D(this.gameToScreenX(x), this.gameToScreenY(y), this.sizeGameToScreenX(w), this.sizeGameToScreenY(h));
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in game space coordinates
	 *
	 * @param x The value to convert
	 * @return The value in game coordinates
	 */
	public double screenToGameX(double x){
		return this.getX().screenToGame(x);
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in game space coordinates
	 *
	 * @param y The value to convert
	 * @return The value in game coordinates
	 */
	public double screenToGameY(double y){
		return this.getY().screenToGame(y);
	}
	
	/**
	 * Convert an x axis size in game space, to a size in screen space
	 *
	 * @param x The value to convert
	 * @return The converted value
	 */
	public double sizeGameToScreenX(double x){
		return this.getX().sizeGameToScreen(x);
	}
	
	/**
	 * Convert a y axis size in game space, to a size in screen space
	 *
	 * @param y The value to convert
	 * @return The converted value
	 */
	public double sizeGameToScreenY(double y){
		return this.getY().sizeGameToScreen(y);
	}
	
	/**
	 * Convert an x axis size in screen space, to a size in game space
	 *
	 * @param x The value to convert
	 * @return The converted value
	 */
	public double sizeScreenToGameX(double x){
		return this.getX().sizeScreenToGame(x);
	}
	
	/**
	 * Convert a y axis size in screen space, to a size in game space
	 *
	 * @param y The value to convert
	 * @return The converted value
	 */
	public double sizeScreenToGameY(double y){
		return this.getY().sizeScreenToGame(y);
	}
	
	/**
	 * Convert the bounds of a rectangle in screen coordinates to the bounds in game coordinates
	 *
	 * @param r The bounds
	 * @return The converted bounds
	 */
	public ZRect2D boundsScreenToGame(ZRect2D r){
		return this.boundsScreenToGame(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Convert the bounds of a rectangle in screen coordinates to the bounds in game coordinates
	 *
	 * @param x The x coordinate of the upper right hand corner of the rectangle
	 * @param y The y coordinate of the upper right hand corner of the rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return The converted bounds
	 */
	public ZRect2D boundsScreenToGame(double x, double y, double w, double h){
		return new ZRect2D(this.screenToGameX(x), this.screenToGameY(y), this.sizeScreenToGameX(w), this.sizeScreenToGameY(h));
	}
	
	/** @return See {@link #x} */
	public CameraAxis getX(){
		return this.x;
	}
	
	/** @return See {@link #y} */
	public CameraAxis getY(){
		return this.y;
	}
	
}
