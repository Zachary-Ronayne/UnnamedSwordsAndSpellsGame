package zgame.graphics.camera;

import zgame.GameWindow;
import zgame.graphics.Renderer;

import static org.lwjgl.opengl.GL30.*;

/**
 * A class used by {@link Renderer} to track the location of where objects should be draw, based on their position in the game.
 * All positions are in game coordinates
 */
public class GameCamera{
	
	/** The x axis of the camera, where its position represents the upper lefthand corner of the screen */
	private CameraAxis x;
	
	/** The y axis of the camera, where its position represents the upper lefthand corner of the screen */
	private CameraAxis y;

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
	 * Apply OpenGL transformations to position objects to where the camera should be, based on the given window
	 * 
	 * @param window The window
	 */
	public void transform(GameWindow window){
		// Find the distance the camera must travel, in OpenGL coordinates
		double x = window.sizeScreenToGlX(this.getX().getPos());
		double y = -window.sizeScreenToGlY(this.getY().getPos());
		// OpenGL transformations occur in reverse order
		
		// Lastly, translate the camera to its actual position
		glTranslated(x, y, 0);
		// Third, adjust the camera back so that the upper left hand corner is in the correct position
		glTranslated(-1, 1, 0);
		// Second, scale to the appropriate size, based on the axis zoom levels
		glScaled(this.getX().getZoomLevel(), this.getY().getZoomLevel(), 1);
		// First, adjust the camera so that the upper left hand corner, i.e. game coordinates (0, 0) is the center to use for scaling
		glTranslated(1, -1, 0);
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
	 * @param y The position to move on the x axis, relative to the anchor
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
	 * Zoom in on both axis
	 * 
	 * @param zoom The factor to zoom in by, which will be added to {@link #zoomFactor}, positive to zoom in, negative to zoom out, zero for no change
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
		return this.getX().gameToScreen(y);
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
	 * Convert an x axis size in game space, to a size in screen space
	 * 
	 * @param x The value to convert
	 * @return The converted value
	 */
	public double sizeGameToScreenY(double y){
		return this.getX().sizeGameToScreen(y);
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
	 * Convert an y axis size in screen space, to a size in game space
	 * 
	 * @param y The value to convert
	 * @return The converted value
	 */
	public double sizeScreenToGameY(double y){
		return this.getY().sizeScreenToGame(y);
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
