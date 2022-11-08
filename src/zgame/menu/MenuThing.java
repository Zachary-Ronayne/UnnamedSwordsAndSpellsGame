package zgame.menu;

import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableGameBuffer;
import zgame.core.utils.ZRect;
import zgame.core.window.GameWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * An object which can be contained by a Menu
 * 
 * All {@link MenuThing}s should be rendered via their relative coordinates
 */
public class MenuThing implements GameInteractable, Destroyable{
	
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
	
	/**
	 * true to draw the contents of {@link #things} to {@link #buffer}, if it is being used, false to draw them directly.
	 * true by default.
	 * This value is ignored when {@link #usesBuffer()} returns false.
	 */
	private boolean drawThingsToBuffer;
	
	/** The {@link MenuThing} which holds this {@link MenuThing}. Can be null if this {@link MenuThing} has no parent */
	private MenuThing parent;
	
	/** A collection of every {@link MenuThing} in this {@link Menu} */
	private List<MenuThing> things;
	
	/**
	 * The buffer used by this {@link MenuThing} to keep track of what's drawn for {@link #render(Game, Renderer)}, or null if using a buffer is not enabled.
	 * If null, the contents of this {@link MenuThing} will be redrawn every frame
	 */
	private DrawableGameBuffer buffer;
	
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
		this(x, y, width, height, false);
	}
	
	/**
	 * Create a {@link MenuThing} of the given position and size
	 * 
	 * @param x See {@link #relX}
	 * @param y See {@link #relY}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 * @param useBuffer true to use {@link #buffer}, false otherwise
	 */
	public MenuThing(double x, double y, double width, double height, boolean useBuffer){
		this.relX = x;
		this.relY = y;
		this.width = width;
		this.height = height;
		
		this.fill = new ZColor(1, 1);
		this.border = new ZColor(0, 1);
		this.borderWidth = 0;
		
		this.parent = null;
		this.things = new ArrayList<MenuThing>();
		
		this.setBuffer(useBuffer);
		this.drawThingsToBuffer = true;
	}
	
	// TODO add option to make things only render in the bounds regardless of a buffer, fix render checking first
	
	/** @param true to enable using the buffer, false otherwise */
	public void setBuffer(boolean use){
		if(use && this.buffer == null) this.initBuffer();
		else if(!use){
			this.destroyBuffer();
			this.buffer = null;
		}
	}
	
	/**
	 * Does nothing if {@link #buffer} is null. Otherwise, forces the contents of the buffer to be redrawn the next time the buffer is requested to be drawn to something.
	 * 
	 * Essentially, call this method if the state of this thing changed, and should be drawn again
	 */
	public void forceRedraw(){
		if(this.buffer == null) return;
		this.buffer.updateRedraw(true);
	}
	
	/** Initialize the state of {@link #buffer} based on the state of this {@link MenuThing} */
	private void initBuffer(){
		this.buffer = new MenuBuffer(this);
	}
	
	/**
	 * Force the underlying buffer for this {@link MenuThing} to be recreated. This is an expensive operation, and should not be used frequently.
	 * Also forces this object to use a buffer, even if it was previously not using one
	 * 
	 * Essentially, call this method to make this thing use a buffer
	 */
	public void updateBuffer(){
		this.destroyBuffer();
		this.initBuffer();
	}
	
	/**
	 * Force this {@link MenuThing} to stop using a buffer, also destroy the buffer if was using one
	 * 
	 * Essentially, call this method to stop using a buffer
	 */
	public void deleteBuffer(){
		this.destroyBuffer();
		this.buffer = null;
	}
	
	/** Destroy {@link #buffer} with a null check */
	private void destroyBuffer(){
		if(this.buffer != null) this.buffer.destroy();
	}
	
	/** @return true if this {@link MenuThing} currently uses a buffer to render its contents, false otherwise */
	public boolean usesBuffer(){
		return this.buffer != null;
	}
	
	/** @return See {@link #drawThingsToBuffer} */
	public boolean isDrawThingsToBuffer(){
		return this.drawThingsToBuffer;
	}
	
	/** @param drawThingsToBuffer See {@link #drawThingsToBuffer} */
	public void setDrawThingsToBuffer(boolean drawThingsToBuffer){
		this.drawThingsToBuffer = drawThingsToBuffer;
	}
	
	@Override
	public void destroy(){
		this.destroyBuffer();
		for(int i = 0; i < this.things.size(); i++) this.things.get(i).destroy();
	}
	
	/**
	 * @return The actual x coordinate of this {@link MenuThing}, based on the position of its parent
	 *         This should only be used for internal game logic, never for rendering operations
	 */
	public double getX(){
		return this.getParentX() + this.getRelX();
	}
	
	/**
	 * @return The actual y coordinate of this {@link MenuThing}, based on the position of its parent
	 *         This should only be used for internal game logic, never for rendering operations
	 */
	public double getY(){
		return this.getParentY() + this.getRelY();
	}
	
	/** @return The actual x coordinate of the center of this {@link MenuThing}, based on the position of its parent */
	public double centerX(){
		return this.getX() + this.getWidth() * 0.5;
	}
	
	/** @return The actual y coordinate of the center of this {@link MenuThing}, based on the position of its parent */
	public double centerY(){
		return this.getY() + this.getHeight() * 0.5;
	}
	
	/**
	 * @return See {@link #relX}
	 *         This should be used for rendering operations, all rendering should take place based on these coordinates, never {@link #getX()}
	 */
	public double getRelX(){
		return this.relX;
	}
	
	/**
	 * @return See {@link #relY}
	 *         This should be used for rendering operations, all rendering should take place based on these coordinates, never {@link #getY()}
	 */
	public double getRelY(){
		return this.relY;
	}
	
	/** @return The x coordinate of the center of this {@link MenuThing}, relative to the position of its parent */
	public double centerRelX(){
		return this.getRelX() + this.getWidth() * 0.5;
	}
	
	/** @return The y coordinate of the center of this {@link MenuThing}, relative to the position of its parent */
	public double centerRelY(){
		return this.getRelY() + this.getHeight() * 0.5;
	}
	
	/** @param x See {@link #relX} */
	public void setRelX(double x){
		this.relX = x;
	}
	
	/** @param x The amount to move this thing on the x axis */
	public void moveX(double x){
		this.setRelX(this.getRelX() + x);
	}
	
	/** @param y See {@link #relY} */
	public void setRelY(double y){
		this.relY = y;
	}
	
	/** @param y The amount to move this thing on the x axis */
	public void moveY(double y){
		this.setRelY(this.getRelY() + y);
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
	
	/** @return A {@link ZRect} containing the position and size of this {@link MenuThing}, using its absolute coordinates */
	public ZRect getBounds(){
		return new ZRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	/** @return A {@link ZRect} containing the position and size of this {@link MenuThing}, using its relative coordinates */
	public ZRect getRelBounds(){
		return new ZRect(this.getRelX(), this.getRelY(), this.getWidth(), this.getHeight());
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
	public MenuThing getParent(){
		return this.parent;
	}
	
	/** @return true if this {@link MenuThing} is the root of the menu, i.e. it has no parent thing */
	public boolean isRoot(){
		return this.parent == null;
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
	 * Should avoid adding things in a circular manor, i.e. if thing1 contains thing2 and thing2 contains thing3, then thing3 should not contain thing1.
	 * If things are added in a circular manor, infinite recursion will occur.
	 * Once added, any actions which apply to this {@link MenuThing} will also apply to the given thing. This means input, rendering, and game ticks
	 * If thing already is in something else, i.e. it already has a parent, thing will not be added.
	 * First remove thing from it's current parent using {@link #removeThing(MenuThing)}, then call {@link #addThing(MenuThing)}
	 * 
	 * @param thing The thing to add
	 * @return true if the thing was added, false otherwise
	 */
	public boolean addThing(MenuThing thing){
		if(this == thing || this.things.contains(thing) || thing.getParent() != null) return false;
		thing.setParent(this);
		return this.things.add(thing);
	}
	
	/**
	 * Remove the given {@link MenuThing} from this {@link Menu}, then destroy it
	 * 
	 * @param thing The thing to remove
	 * @return true if the thing was removed, false otherwise
	 */
	public boolean removeThing(MenuThing thing){
		return this.removeThing(thing, true);
	}
	
	/**
	 * Remove the given {@link MenuThing} from this {@link Menu}
	 * 
	 * @param thing The thing to remove
	 * @param destroy true to destroy this menu thing as it is removed, false otherwise
	 * @return true if the thing was removed, false otherwise
	 */
	public boolean removeThing(MenuThing thing, boolean destroy){
		if(thing == null) return false;
		if(thing.getParent() == this) thing.setParent(null);
		boolean success = this.things.remove(thing);
		if(destroy) thing.destroy();
		return success;
	}
	
	/** Removes and destroys everything currently in this {@link MenuThing} */
	public void removeAll(){
		this.removeAll(true);
	}
	
	/**
	 * Removes everything currently in this {@link MenuThing}
	 * 
	 * @param destroy true to also destroy every removed thing, false otherwise
	 */
	public void removeAll(boolean destroy){
		if(destroy) for(MenuThing thing : this.things) thing.destroy();
		this.things.clear();
	}
	
	/** @return See {@link #things} */
	public List<MenuThing> getThings(){
		return this.things;
	}
	
	/** Move this {@link MenuThing} to the center bounds of it's parent. Does nothing if this thing has no parent */
	public void center(){
		centerHorizontal();
		centerVertical();
	}
	
	/** Move this {@link MenuThing} to the center horizontal bounds of it's parent. Does nothing if this thing has no parent */
	public void centerHorizontal(){
		if(this.parent == null) return;
		this.setRelX((this.parent.getWidth() - this.getWidth()) * 0.5);
	}
	
	/** Move this {@link MenuThing} to the center vertical bounds of it's parent. Does nothing if this thing has no parent */
	public void centerVertical(){
		if(this.parent == null) return;
		this.setRelY((this.parent.getHeight() - this.getHeight()) * 0.5);
	}
	
	/** Do not call directly */
	@Override
	public void tick(Game game, double dt){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.tick(game, dt);
		}
	}
	
	/** Do not call directly */
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.keyAction(game, button, press, shift, alt, ctrl);
		}
	}
	
	/** Do not call directly */
	@Override
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.mouseAction(game, button, press, shift, alt, ctrl);
		}
	}
	
	/** Do not call directly */
	@Override
	public void mouseMove(Game game, double x, double y){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.mouseMove(game, x, y);
		}
	}
	
	/** Do not call directly */
	@Override
	public void mouseWheelMove(Game game, double amount){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.mouseWheelMove(game, amount);
		}
	}
	
	/** Do not call directly */
	@Override
	public void renderBackground(Game game, Renderer r){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.renderBackground(game, r);
		}
	}
	
	/** Do not call directly. Do not override, instead override {@link #renderSelf(Game, Renderer, ZRect)} */
	@Override
	public final void render(Game game, Renderer r){
		// If using a buffer, draw the contents of the buffer to the relative position
		if(this.usesBuffer()){
			this.buffer.drawToRenderer(this.getRelX(), this.getRelY(), r, game);
			// If not drawing the things to the buffer, draw them directly
			if(!this.isDrawThingsToBuffer()) this.drawThings(game, r, true);
		}
		// Otherwise, draw the object directly with the renderer
		else{
			// Draw relative to the parent
			this.renderSelf(game, r, this.getRelBounds());
			this.drawThings(game, r, true);
		}
	}

	/**
	 * Draw the contents of just this menu thing, not anything in {@link #things}
	 * Anything drawn using this method should be drawn relative to the given bounds, not based on this thing's position or relative position
	 * 
	 * @param game The game associated with this thing
	 * @param r The renderer to use
	 * @param bounds The bounds which this thing will be rendered relative to
	 */
	public void renderSelf(Game game, Renderer r, ZRect bounds){
		r.setColor(this.getBorder());
		r.drawRectangle(bounds);
		r.setColor(this.getFill());
		double b = this.getBorderWidth();
		r.drawRectangle(new ZRect(bounds, -b));
	}
	
	/**
	 * Render this {@link MenuThing} to the given renderer using the given game, relative to the the internal buffer
	 * 
	 * @param game The game
	 * @param r The renderer
	 */
	private void renderToBuffer(Game game, Renderer r){
		// Draw relative to the origin
		this.renderSelf(game, r, new ZRect(0, 0, this.getWidth(), this.getHeight()));
		// If drawing things directly to the buffer, draw them
		if(this.isDrawThingsToBuffer()) this.drawThings(game, r, false);
	}
	
	/**
	 * Render this the contents of {@link #things} using the associated game and renderer
	 * 
	 * @param game The game
	 * @param r The renderer
	 * @param reposition true to reposition the coordinates based on {@link #relX} and {@link #relY}, false otherwise
	 */
	private void drawThings(Game game, Renderer r, boolean reposition){
		// Position the renderer to draw this thing's things relative to this thing
		if(reposition){
			r.pushMatrix();
			GameWindow w = game.getWindow();
			r.translate(w.sizeScreenToGlX(this.getRelX()), w.sizeScreenToGlY(-this.getRelY()));
		}
		// Draw this thing's things
		// Did I use "thing" enough times?
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.render(game, r);
		}
		// Put the matrix back to what it was
		if(reposition) r.popMatrix();
	}
	
	/** Do not call directly */
	@Override
	public void renderHud(Game game, Renderer r){
		for(int i = 0; i < this.things.size(); i++){
			MenuThing t = this.things.get(i);
			t.renderHud(game, r);
		}
	}
	
	/** A helper class for drawing {@link MenuThing}s */
	public class MenuBuffer extends DrawableGameBuffer{
		
		/** The thing drawn by this buffer */
		private MenuThing thing;
		
		/**
		 * Create the new buffer
		 * 
		 * @param thing The thing to use for the buffer
		 */
		public MenuBuffer(MenuThing thing){
			super(thing.getWidth(), thing.getHeight());
			this.thing = thing;
		}
		
		@Override
		public void draw(Game game, Renderer r){
			this.thing.renderToBuffer(game, r);
		}
	}
	
}
