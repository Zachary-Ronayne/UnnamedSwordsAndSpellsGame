package zgame.menu;

import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An object which can be contained by a Menu
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public abstract class MenuThing<D> implements GameInteractable<D>{
	
	/** The x coordinate of the {@link MenuThing}, in screen coordinates, relative to {@link #parent}, or relative to (0, 0) if {@link #parent} is null */
	private double relX;
	/** The y coordinate of the {@link MenuThing}, in screen coordinates */
	private double relY;
	/** The width which this {@link MenuThing} should take up, in screen coordinates */
	private double width;
	/** The height which this {@link MenuThing} should take up, in screen coordinates */
	private double height;
	
	/** The color of the inside of this {@link MenuThing} */
	private ZColor fill;
	/** The color of the border of this {@link MenuThing} */
	private ZColor border;
	/** The width, in pixels, of the border of this {@link MenuThing} */
	private double borderWidth;
	
	/** The {@link MenuThing} which holds this {@link MenuThing}. Can be null if this {@link MenuThing} has no parent */
	private MenuThing<D> parent;
	
	/** A collection of every {@link MenuThing} in this {@link Menu} */
	private Collection<MenuThing<D>> things;
	
	/**
	 * Create a {@link MenuThing} with no size or position
	 */
	public MenuThing(){
		this(0, 0, 0, 0);
	}
	
	/**
	 * Create a {@link MenuThing} of the given position with no size
	 * 
	 * @param x See {@link #relX}
	 * @param y See {@link #relY}
	 */
	public MenuThing(double x, double y){
		this(x, y, 0, 0);
	}
	
	/**
	 * Create a {@link MenuThing} of the given position and size
	 * 
	 * @param x See {@link #relX}
	 * @param y See {@link #relY}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 */
	public MenuThing(double x, double y, double width, double height){
		this.relX = x;
		this.relY = y;
		this.width = width;
		this.height = height;
		
		this.fill = new ZColor(1, 1);
		this.border = new ZColor(0, 1);
		this.borderWidth = 0;
		
		this.parent = null;
		this.things = new ArrayList<MenuThing<D>>();
	}
	
	/** @return The actual x coordinate of this {@link MenuThing}, based on the position of its parent */
	public double getX(){
		return this.getParentX() + this.getRelX();
	}
	
	/** @return The actual y coordinate of this {@link MenuThing}, based on the position of its parent */
	public double getY(){
		return this.getParentY() + this.getRelY();
	}
	
	/** @return See {@link #relX} */
	public double getRelX(){
		return this.relX;
	}
	
	/** @return See {@link #relY} */
	public double getRelY(){
		return this.relY;
	}
	
	/** @param x See {@link #relX} */
	public void setRelX(double x){
		this.relX = x;
	}
	
	/** @param y See {@link #relY} */
	public void setRelY(double y){
		this.relY = y;
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
	
	/** @return A {@link ZRect} containing the position and size of this {@link MenuThing} */
	public ZRect getBounds(){
		return new ZRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	/** @return See {@link #fill} */
	public ZColor getFill(){
		return this.fill;
	}
	
	/** @param fill See {@link #fill} */
	public void setFill(ZColor fill){
		this.fill = fill;
	}
	
	/** @return See {@link #border} */
	public ZColor getBorder(){
		return this.border;
	}
	
	/** @param border See {@link #border} */
	public void setBorder(ZColor border){
		this.border = border;
	}
	
	/** @return See {@link #borderWidth} */
	public double getBorderWidth(){
		return this.borderWidth;
	}
	
	/** @param borderWidth See {@link #borderWidth} */
	public void setBorderWidth(double borderWidth){
		this.borderWidth = borderWidth;
	}

	
	/** @return See {@link #parent} */
	public MenuThing<D> getParent(){
		return this.parent;
	}
	
	/** @return The real x coordinate of {@link #parent}. If {@link #parent} is null, this method returns 0 */
	public double getParentX(){
		return (this.getParent() == null) ? 0 : parent.getX();
	}
	
	/** @return The real y coordinate of {@link #parent}. If {@link #parent} is null, this method returns 0 */
	public double getParentY(){
		return (this.getParent() == null) ? 0 : parent.getY();
	}
	
	/** @param parent See {@link #parent} */
	public void setParent(MenuThing<D> parent){
		this.parent = parent;
	}
	
	/**
	 * Add a {@link MenuThing} to this {@link Menu}
	 * If thing is the same as this object, the thing is not added.
	 * Should avoid adding things in a circular manor, i.e. if thing1 contains thing2 and thing2 contains thing3, the thing3 should not contain thing1.
	 * If things are added in a circular manor, infinite recursion will occur.
	 * Once added, any actions which apply to this {@link MenuThing} will also apply to the given thing. This means input, rendering, and game ticks
	 * 
	 * @param thing The thing to add
	 * @return true if the thing was added, false otherwise
	 */
	public boolean addThing(MenuThing<D> thing){
		if(this == thing) return false;
		this.things.add(thing);
		thing.setParent(this);
		return true;
	}
	
	/**
	 * Remove the given {@link MenuThing} from this {@link Menu}
	 * 
	 * @param thing The thing to remove
	 * @return true if the thing was removed, false otherwise
	 */
	public boolean removeThing(MenuThing<D> thing){
		if(thing.getParent() == this) thing.setParent(null);
		return this.things.remove(thing);
	}

	/** @return See {@link #things} */
	public Collection<MenuThing<D>> getThings(){
		return this.things;
	}
	
	/** Do not call directly */
	@Override
	public void tick(Game<D> game, double dt){
		for(MenuThing<D> t : this.things) t.tick(game, dt);
	}
	
	/** Do not call directly */
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(MenuThing<D> t : this.things) t.keyAction(game, button, press, shift, alt, ctrl);
	}
	
	/** Do not call directly */
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(MenuThing<D> t : this.things) t.mouseAction(game, button, press, shift, alt, ctrl);
	}
	
	/** Do not call directly */
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		for(MenuThing<D> t : this.things) t.mouseMove(game, x, y);
	}
	
	/** Do not call directly */
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		for(MenuThing<D> t : this.things) t.mouseWheelMove(game, amount);
	}
	
	/** Do not call directly */
	@Override
	public void renderBackground(Game<D> game, Renderer r){
		for(MenuThing<D> t : this.things) t.renderBackground(game, r);
	}
	
	/** Do not call directly */
	@Override
	public void render(Game<D> game, Renderer r){
		r.setColor(this.border);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		r.setColor(this.fill);
		double b = this.getBorderWidth();
		r.drawRectangle(this.getX() + b, this.getY() + b, this.getWidth() - b * 2, this.getHeight() - b * 2);
		for(MenuThing<D> t : this.things) t.render(game, r);
	}
	
	/** Do not call directly */
	@Override
	public void renderHud(Game<D> game, Renderer r){
		for(MenuThing<D> t : this.things) t.renderHud(game, r);
	}
	
}
