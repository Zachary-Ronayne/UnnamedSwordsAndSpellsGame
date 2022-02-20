package zgame.menu;

import zgame.core.Game;
import zgame.core.GameIntractable;
import zgame.core.graphics.Renderer;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

/** An object which can be contained by a Menu */
public abstract class MenuThing implements GameIntractable{
	
	/** The x coordinate of the {@link MenuThing}, in screen coordinates, relative to {@link #parent}, or relative to (0, 0) if {@link #parent} is null */
	private double relX;
	/** The y coordinate of the {@link MenuThing}, in screen coordinates */
	private double relY;
	/** The width which this {@link MenuThing} should take up, in screen coordinates */
	private double width;
	/** The height which this {@link MenuThing} should take up, in screen coordinates */
	private double height;
	
	/** The {@link MenuThing} which holds this {@link MenuThing}. Can be null if this {@link MenuThing} has no parent */
	private MenuThing parent;
	
	/** A collection of every {@link MenuThing} in this {@link Menu} */
	private Collection<MenuThing> things;
	
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
		this.parent = null;
		this.things = new ArrayList<MenuThing>();
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
	
	/** @return A {@link Rectangle2D} containing the position and size of this {@link MenuThing} */
	public Rectangle2D.Double getBounds(){
		return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	/** @return See {@link #parent} */
	public MenuThing getParent(){
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
	public void setParent(MenuThing parent){
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
	public boolean addThing(MenuThing thing){
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
	public boolean removeThing(MenuThing thing){
		if(thing.getParent() == this) thing.setParent(null);
		return this.things.remove(thing);
	}
	
	/** Do not call directly */
	@Override
	public final void tick(Game game, double dt){
		this.tickO(game, dt);
		for(MenuThing t : this.things) t.tick(game, dt);
	}
	
	/** Do not call directly */
	@Override
	public final void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.keyActionO(game, button, press, shift, alt, ctrl);
		for(MenuThing t : this.things) t.keyAction(game, button, press, shift, alt, ctrl);
	}
	
	/** Do not call directly */
	@Override
	public final void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.mouseActionO(game, button, press, shift, alt, ctrl);
		for(MenuThing t : this.things) t.mouseAction(game, button, press, shift, alt, ctrl);
	}
	
	/** Do not call directly */
	@Override
	public final void mouseMove(Game game, double x, double y){
		this.mouseMoveO(game, x, y);
		for(MenuThing t : this.things) t.mouseMove(game, x, y);
	}
	
	/** Do not call directly */
	@Override
	public final void mouseWheelMove(Game game, double amount){
		this.mouseWheelMoveO(game, amount);
		for(MenuThing t : this.things) t.mouseWheelMove(game, amount);
	}
	
	/** Do not call directly */
	@Override
	public final void renderBackground(Game game, Renderer r){
		this.renderBackgroundO(game, r);
		for(MenuThing t : this.things) t.renderBackground(game, r);
	}
	
	/** Do not call directly */
	@Override
	public final void render(Game game, Renderer r){
		this.renderO(game, r);
		for(MenuThing t : this.things) t.render(game, r);
	}
	
	/** Do not call directly */
	@Override
	public final void renderHud(Game game, Renderer r){
		this.renderHudO(game, r);
		for(MenuThing t : this.things) t.renderHud(game, r);
	}
	
	/** A version of {@link #tick(Game, double)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly */
	public void tickO(Game game, double dt){
	}
	
	/**
	 * A version of {@link #keyAction(Game, int, boolean, boolean, boolean, boolean)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly
	 */
	public void keyActionO(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	/**
	 * A version of {@link #mouseAction(Game, int, boolean, boolean, boolean, boolean)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call
	 * directly
	 */
	public void mouseActionO(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	/** A version of {@link #mouseMove(Game, double, double)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly */
	public void mouseMoveO(Game game, double x, double y){
	}
	
	/** A version of {@link #mouseWheelMove(Game, double)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly */
	public void mouseWheelMoveO(Game game, double amount){
	}
	
	/** A version of {@link #renderBackground(Game, Renderer)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly */
	public void renderBackgroundO(Game game, Renderer r){
	}
	
	/** A version of {@link #render(Game, Renderer)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly */
	public void renderO(Game game, Renderer r){
	}
	
	/** A version of {@link #renderHud(Game, Renderer)} which can be overwritten to make this {@link MenuThing} perform actions. Do not call directly */
	public void renderHudO(Game game, Renderer r){
	}
	
}
