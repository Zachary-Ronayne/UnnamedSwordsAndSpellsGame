package zgame.menu;

import zgame.core.Game;
import zgame.core.GameIntractable;
import zgame.core.graphics.Renderer;
import java.awt.geom.Rectangle2D;

/** An object which can be contained by a Menu */
public abstract class MenuThing implements GameIntractable{
	
	/** The x coordinate of the {@link MenuThing}, in screen coordinates */
	private double x;
	/** The y coordinate of the {@link MenuThing}, in screen coordinates */
	private double y;
	/** The width which this {@link MenuThing} should take up, in screen coordinates */
	private double width;
	/** The height which this {@link MenuThing} should take up, in screen coordinates */
	private double height;
	
	/**
	 * Create a {@link MenuThing} with no size or position
	 */
	public MenuThing(){
		this(0, 0, 0, 0);
	}
	
	/**
	 * Create a {@link MenuThing} of the given position with no size
	 * 
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 */
	public MenuThing(double x, double y){
		this(x, y, 0, 0);
	}
	
	/**
	 * Create a {@link MenuThing} of the given position and size
	 * 
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 */
	public MenuThing(double x, double y, double width, double height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/** @return See {@link #x} */
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	public void setX(double x){
		this.x = x;
	}
	
	/** @return See {@link #y} */
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	public void setY(double y){
		this.y = y;
	}
	
	/** @return See {@link #width} */
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
	
	/** @return A {@link Rectangle2D} containing the position and size of this {@link MenuThing} */
	public Rectangle2D.Double getBounds(){
		return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	@Override public void tick(Game game, double dt){
		
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		
	}
	
	@Override
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		
	}
	
	@Override
	public void mouseMove(Game game, double x, double y){
		
	}
	
	@Override
	public void mouseWheelMove(Game game, double amount){
		
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
		
	}
	
	@Override
	public void render(Game game, Renderer r){
	}
	
	@Override
	public void renderHud(Game game, Renderer r){
	}
	
}
