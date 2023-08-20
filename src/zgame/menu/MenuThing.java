package zgame.menu;

import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.graphics.Destroyable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableGameBuffer;
import zgame.core.utils.ClassMappedList;
import zgame.core.utils.ZPoint;
import zgame.core.utils.ZRect;
import zgame.core.window.GameWindow;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.PixelFormatter;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * An object which can be contained by a Menu
 * <p>
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
	 * true to draw the contents of {@link #things} to {@link #buffer}, if it is being used, false to draw them directly. true by default. This value is ignored when
	 * {@link #usesBuffer()} returns false.
	 */
	private boolean drawThingsToBuffer;
	
	/** true if, when mouse input is given to this {@link MenuThing}'s bounds, it will not be given to any parent elements, false otherwise, true by default */
	private boolean stopParentInput;
	
	/** The {@link MenuThing} which holds this {@link MenuThing}. Can be null if this {@link MenuThing} has no parent */
	private MenuThing parent;
	
	/** Every {@link MenuThing} in this {@link Menu} */
	private final ClassMappedList things;
	
	/**
	 * The buffer used by this {@link MenuThing} to keep track of what's drawn for {@link #renderHud(Game, Renderer)}, or null if using a buffer is not enabled. If null, the
	 * contents of this {@link MenuThing} will be redrawn every frame
	 */
	private DrawableGameBuffer buffer;
	
	/** true if this thing should use a buffer when it is added to a thing, false to not use a buffer */
	private boolean defaultUseBuffer;
	
	/**
	 * If the mouse is clicked and dragged while within the area of the given thing relative to this {@link MenuThing}, it will be dragged around.
	 * Null to disable dragging. Null by default
	 */
	private MenuThing draggableArea;
	
	/**
	 * The point, relative to this {@link MenuThing} where the mouse was last clicked for dragging, or null if dragging is disabled, or null if
	 * an anchor point is not set
	 */
	private ZPoint anchorPoint;
	
	/** The mouse button for actions using {@link #draggableArea} and {@link #draggableSides} */
	private int draggableButton;
	
	/** true if the sides of this thing can be clicked and dragged to change the size, false otherwise */
	private boolean draggableSides;
	
	/** the amount of distance inside this thing from the edges where {@link #draggableSides} will activate */
	private double draggableSideRange;
	
	/** The color of the draggable area */
	private ZColor draggableColor;
	
	/** true to display {@link #draggableColor} in the area when dragging is enabled can be dragged */
	private boolean displayDraggableColor;
	
	/**
	 * Dragging state of the x axis {@link #draggableSides}, negative for left, 0 for none, 1 for right.
	 * If this value and {@link #draggingY} are 0, dragging is for {@link #draggableArea}
	 */
	private int draggingX;
	/**
	 * Dragging state of the y axis {@link #draggableSides}, negative for up, 0 for none, 1 for down
	 * If this value and {@link #draggingX} are 0, dragging is for {@link #draggableArea}
	 */
	private int draggingY;
	
	/** If true, disable all key and mouse input for parents when this thing is being dragged, does nothing if false */
	private boolean disableChildrenWhenDragging;
	
	/** true if the mouse is currently on this thing, and nothing else is on top of this thing and blocking it */
	private boolean mouseOn;
	
	/** The minimum width which this thing can be, null for no min */
	private Double minWidth;
	/** The maximum width which this thing can be, null for no max */
	private Double maxWidth;
	
	/** The minimum height which this thing can be, null for no min */
	private Double minHeight;
	/** The maximum height which this thing can be, null for no max */
	private Double maxHeight;
	
	/**
	 * true if this menuThing should not be allowed to leave the bounds of its parent, false to ignore.
	 * The width and height of this thing will also not exceed that of its parent
	 * If this has no parent, the bounds will update to the game window during {@link #tick(Game, double)}
	 */
	private boolean keepInParent;
	
	/** The object used to dictate how this thing will be formatted when its parent changes, or null to apply no formatting */
	private MenuFormatter formatter;
	
	/** The object used to dictate how {@link #draggableArea} will be formatted when its parent changes, or null to apply no formatting */
	private MenuFormatter draggableFormatter;
	
	/**
	 * The bounds, relative to this thing, which child elements of this thing can be rendered.
	 * Children outside of this bounds will be cut off. Or null to not limit the bounds
	 */
	private MenuThing childBounds;
	
	/** The formatter to use for {@link #childBounds} */
	private MenuFormatter childBoundsFormatter;
	
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
		this.things = new ClassMappedList();
		this.things.addClass(MenuThing.class);
		
		this.relX = x;
		this.relY = y;
		this.setWidth(width);
		this.setHeight(height);
		
		this.fill = new ZColor(1, 1);
		this.border = new ZColor(0, 1);
		this.borderWidth = 0;
		
		this.parent = null;
		
		this.defaultUseBuffer = useBuffer;
		this.drawThingsToBuffer = true;
		
		this.stopParentInput = false;
		
		this.draggableArea = null;
		this.anchorPoint = null;
		this.draggableButton = 0;
		
		this.disableChildrenWhenDragging = true;
		this.draggableSides = false;
		this.draggableSideRange = 15;
		this.draggingX = 0;
		this.draggingY = 0;
		this.setDraggableColor(new ZColor(.5, .5));
		this.setDisplayDraggableColor(false);
		
		this.minWidth = null;
		this.maxWidth = null;
		this.minHeight = null;
		this.maxHeight = null;
		this.keepInParent = false;
		
		this.mouseOn = false;
		
		this.formatter = null;
		this.draggableFormatter = null;
		
		this.childBounds = null;
		this.childBoundsFormatter = null;
	}
	
	/**
	 * Enable {@link #draggableSides} {@link #draggableArea}, and position the draggable area at the top of the menu
	 *
	 * @param borderSize The size of the border and the draggable sides size
	 * @param draggableHeight The distance from the top draggable side, down to the bottom of the draggable area
	 */
	public void makeDraggable(double borderSize, double draggableHeight){
		this.setBorderWidth(borderSize);
		this.setDraggableArea(new MenuThing(0, borderSize, 0, draggableHeight));
		this.setDraggableFormatter(new PixelFormatter(borderSize, borderSize, null, null));
		this.setDraggableButton(GLFW_MOUSE_BUTTON_LEFT);
		this.setDraggableSides(true);
		this.setDraggableSideRange(borderSize);
		this.setDisplayDraggableColor(true);
		this.setKeepInParent(true);
		this.setStopParentInput(true);
		this.setChildBoundsBorder();
		this.setMinWidth(borderSize * 2);
		this.setMinHeight(borderSize * 2);
	}
	
	/**
	 * Format this {@link MenuThing} so that it aligns to its parent based on {@link #formatter}. Does nothing if the parent is null
	 */
	public void format(){
		this.format(this.getFormatter());
	}
	
	/**
	 * Format this {@link MenuThing} so that it aligns to its parent. Does nothing if the parent is null
	 *
	 * @param formatter A formatter to use to set the bounds based on the parent
	 */
	public void format(MenuFormatter formatter){
		var p = this.getParent();
		if(p == null) return;
		this.format(formatter, p.getWidth(), p.getHeight());
	}
	
	/**
	 * Format this {@link MenuThing} so that it aligns with the given window
	 *
	 * @param window The window to format to
	 * @param formatter A formatter to use to set the bounds based on the given game window
	 */
	public void format(GameWindow window, MenuFormatter formatter){
		this.format(formatter, window.getScreenWidth(), window.getScreenHeight());
	}
	
	/**
	 * Format this {@link MenuThing} so that it aligns with the given dimensions
	 *
	 * @param formatter A formatter to use to set the bounds based on the given dimensions
	 * @param width The width to format to
	 * @param height The height to format to
	 */
	public void format(MenuFormatter formatter, double width, double height){
		if(formatter == null) return;
		formatter.onWidthChange(this, width);
		formatter.onHeightChange(this, height);
	}
	
	// issue#11 add option to make things only render in the bounds regardless of a buffer, fix render checking first
	
	/** @param use true to enable using the buffer, false otherwise. If setting to true, probably should follow this up with {@link #regenerateBuffer()} */
	private void setBuffer(boolean use){
		if(use && this.buffer == null) this.initBuffer();
		else if(!use){
			this.destroyBuffer();
			this.buffer = null;
		}
	}
	
	/**
	 * Does nothing if {@link #buffer} is null. Otherwise, forces the contents of the buffer to be redrawn the next time the buffer is requested to be drawn to something.
	 * Essentially, call this method if the state of this thing changed, and should be drawn again
	 */
	public void forceRedraw(){
		if(this.buffer == null) return;
		this.buffer.updateRedraw(true);
	}
	
	/** Initialize the state of {@link #buffer} based on the state of this {@link MenuThing} */
	private void initBuffer(){
		this.destroyBuffer();
		this.buffer = new MenuBuffer(this);
	}
	
	/** Regenerate {@link #buffer} to the current size of this menu thing, does nothing if {@link #buffer} is already null */
	public void regenerateBuffer(){
		if(this.buffer == null) return;
		this.buffer.regenerateBuffer((int)this.getWidth(), (int)this.getHeight());
	}
	
	/**
	 * Force this {@link MenuThing} to stop using a buffer, also destroy the buffer if it was using one. Essentially, call this method to stop using a buffer
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
	
	/** @return See {@link #buffer} */
	public DrawableGameBuffer getBuffer(){
		return this.buffer;
	}
	
	/** @return See {@link #drawThingsToBuffer} */
	public boolean isDrawThingsToBuffer(){
		return this.drawThingsToBuffer;
	}
	
	/** @param drawThingsToBuffer See {@link #drawThingsToBuffer} */
	public void setDrawThingsToBuffer(boolean drawThingsToBuffer){
		this.drawThingsToBuffer = drawThingsToBuffer;
	}
	
	/** @return See {@link #defaultUseBuffer} */
	public boolean getDefaultUseBuffer(){
		return this.defaultUseBuffer;
	}
	
	/** @param defaultUseBuffer See {@link #defaultUseBuffer} */
	public void setDefaultUseBuffer(boolean defaultUseBuffer){
		if(this.defaultUseBuffer == defaultUseBuffer) return;
		this.defaultUseBuffer = defaultUseBuffer;
		this.setBuffer(this.defaultUseBuffer);
	}
	
	@Override
	public void destroy(){
		var things = this.getThings();
		this.destroyBuffer();
		for(int i = 0; i < things.size(); i++) things.get(i).destroy();
	}
	
	/** @return See {@link #stopParentInput} */
	public boolean isStopParentInput(){
		return this.stopParentInput;
	}
	
	/** @param stopParentInput See {@link #stopParentInput} */
	public void setStopParentInput(boolean stopParentInput){
		this.stopParentInput = stopParentInput;
	}
	
	/**
	 * @return The actual x coordinate of this {@link MenuThing}, based on the position of its parent This should only be used for internal game logic, never for rendering
	 * 		operations
	 */
	public double getX(){
		return this.getParentX() + this.getRelX();
	}
	
	/**
	 * @return The actual y coordinate of this {@link MenuThing}, based on the position of its parent This should only be used for internal game logic, never for rendering
	 * 		operations
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
	 * @return See {@link #relX} This should be used for rendering operations, all rendering should take place based on these coordinates, never {@link #getX()}
	 */
	public double getRelX(){
		return this.relX;
	}
	
	/**
	 * @return See {@link #relY} This should be used for rendering operations, all rendering should take place based on these coordinates, never {@link #getY()}
	 */
	public double getRelY(){
		return this.relY;
	}
	
	/** @return The x coordinate of the center of this {@link MenuThing}, relative to the position of its parent */
	public double centerRelX(){
		return this.getRelX() + this.getWidth() * 0.5;
	}
	
	/** @param x The x value that should be the center of this {@link MenuThing} */
	public void setCenterRelX(double x){
		this.setRelX(x - this.getWidth() * 0.5);
	}
	
	/** @return The y coordinate of the center of this {@link MenuThing}, relative to the position of its parent */
	public double centerRelY(){
		return this.getRelY() + this.getHeight() * 0.5;
	}
	
	/** @param y The y value that should be the center of this {@link MenuThing} */
	public void setCenterRelY(double y){
		this.setRelY(y - this.getHeight() * 0.5);
	}
	
	/** @param x See {@link #relX} */
	public void setRelX(double x){
		this.relX = x;
		if(this.isKeepInParent()) this.keepInParentBounds();
	}
	
	/** @param x The amount to move this thing on the x axis */
	public void moveX(double x){
		this.setRelX(this.getRelX() + x);
	}
	
	/** @param y See {@link #relY} */
	public void setRelY(double y){
		this.relY = y;
		if(this.isKeepInParent()) this.keepInParentBounds();
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
		this.setWidth(width, true);
	}
	
	/**
	 * @param width See {@link #width}
	 * @param keepLeft true if the left of this thing should remain in the same position when the height changes, false to keep the right in the same position
	 */
	public void setWidth(double width, boolean keepLeft){
		var oldX = this.getRelX() + this.getWidth();
		
		var m = this.getMaxWidth();
		if(m != null && width > m) width = m;
		m = this.getMinWidth();
		if(m != null && width < m) width = m;
		this.width = width;
		
		if(!keepLeft) this.setRelX(oldX - this.width);
		
		this.onWidthChange();
	}
	
	/** Called when the width changes */
	public void onWidthChange(){
		var f = this.getDraggableFormatter();
		var d = this.getDraggableArea();
		if(f != null && d != null) f.onWidthChange(d, this.width);
		
		f = this.getChildBoundsFormatter();
		d = this.getChildBounds();
		if(f != null && d != null) f.onWidthChange(d, this.width);
		
		for(var t : this.things.get(MenuThing.class)){
			f = t.getFormatter();
			if(f != null) f.onWidthChange(t, this.width);
		}
	}
	
	/** @return See {@link #height} */
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.setHeight(height, true);
	}
	
	/**
	 * @param height See {@link #height}
	 * @param keepTop true if the top of this thing should remain in the same position when the height changes, false to keep the bottom in the same position
	 */
	public void setHeight(double height, boolean keepTop){
		var oldY = this.getRelY() + this.getHeight();
		
		var m = this.getMaxHeight();
		if(m != null && height > m) height = m;
		m = this.getMinHeight();
		if(m != null && height < m) height = m;
		this.height = height;
		
		if(!keepTop) this.setRelY(oldY - this.height);
		
		onHeightChange();
	}
	
	/** Called when the height changes */
	public void onHeightChange(){
		var f = this.getDraggableFormatter();
		var d = this.getDraggableArea();
		if(f != null && d != null) f.onHeightChange(d, this.height);
		
		f = this.getChildBoundsFormatter();
		d = this.getChildBounds();
		if(f != null && d != null) f.onHeightChange(d, this.height);
		
		for(var t : this.things.get(MenuThing.class)){
			f = t.getFormatter();
			if(f != null) f.onHeightChange(t, this.height);
		}
	}
	
	/**
	 * Keep this {@link MenuThing} in the given bounds
	 *
	 * @param x The upper left hand x coordinate of the relative bounds
	 * @param y The upper left hand y coordinate of the relative bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 */
	public void keepInBounds(double x, double y, double w, double h){
		// Not using setters to avoid infinite recursion
		if(this.getWidth() > w){
			this.width = w;
			this.onWidthChange();
		}
		if(this.getHeight() > h){
			this.height = h;
			this.onHeightChange();
		}
		
		if(this.getRelX() < x) this.setRelX(x);
		if(this.getRelX() + this.getWidth() > x + w) this.setRelX(x + w - this.getWidth());
		if(this.getRelY() < y) this.setRelY(y);
		if(this.getRelY() + this.getHeight() > y + h) this.setRelY(y + h - this.getHeight());
	}
	
	/** Ensure this thing stays within the bounds of its parent. Does nothing if this thing has no parent */
	public void keepInParentBounds(){
		var p = this.getParent();
		if(p != null) this.keepInBounds(0, 0, p.getWidth(), p.getHeight());
	}
	
	/**
	 * @return A {@link ZRect} containing the position and size of this {@link MenuThing}, using its absolute coordinates
	 * 		Modifications to the returned rectangle will not modify this object
	 */
	public ZRect getBounds(){
		return new ZRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	/**
	 * @return A {@link ZRect} containing the position and size of this {@link MenuThing}, using its relative coordinates
	 * 		Modifications to the returned rectangle will not modify this object
	 */
	public ZRect getRelBounds(){
		return new ZRect(this.getRelX(), this.getRelY(), this.getWidth(), this.getHeight());
	}
	
	/** @return A {@link ZRect} of the bounds of this thing, where the position is relative to first thing in the tree of menu things that uses a buffer */
	public ZRect getBoundsToBuffer(){
		MenuThing p = this;
		double x = 0;
		double y = 0;
		while(p != null && !p.usesBuffer()){
			x += p.getRelX();
			y += p.getRelY();
			p = p.getParent();
		}
		return new ZRect(x, y, this.getWidth(), this.getHeight());
	}
	
	/** @return See {@link #fill} */
	public ZColor getFill(){
		return this.fill;
	}
	
	/** @param fill See {@link #fill} */
	public void setFill(ZColor fill){
		this.fill = fill;
	}
	
	/**
	 * Set {@link #fill} and also remove the border
	 *
	 * @param c The color for the fill
	 */
	public void setFullColor(ZColor c){
		this.fill = c;
		this.border = new ZColor(0, 0);
	}
	
	/** Set {@link #fill} and {@link #border} to be fully transparent */
	public void invisible(){
		this.setFullColor(new ZColor(0, 0));
	}
	
	/** @return See {@link #border} */
	public ZColor getBorder(){
		return this.border;
	}
	
	/** @param border See {@link #border} */
	public void setBorder(ZColor border){
		this.border = border;
	}
	
	/** Set the border to be completely invisible */
	public void removeBorder(){
		this.setBorder(new ZColor(0, 0));
	}
	
	/** @return See {@link #borderWidth} */
	public double getBorderWidth(){
		return this.borderWidth;
	}
	
	/** @param borderWidth See {@link #borderWidth} */
	public void setBorderWidth(double borderWidth){
		this.borderWidth = borderWidth;
	}
	
	/** @return See {@link #draggableArea} */
	public MenuThing getDraggableArea(){
		return this.draggableArea;
	}
	
	/** @return true if this thing can be dragged around, false otherwise */
	public boolean isDraggable(){
		return this.draggableArea != null;
	}
	
	/** @param draggableArea See {@link #draggableArea} */
	public void setDraggableArea(MenuThing draggableArea){
		this.draggableArea = draggableArea;
		if(this.draggableArea == null){
			this.anchorPoint = null;
		}
	}
	
	/** @param draggable true if the entire bounds of this {@link MenuThing} should be draggable by the mouse, false if dragging should be disabled */
	public void setDraggable(boolean draggable){
		if(draggable) this.setDraggableArea(new MenuThing(0, 0, this.getWidth(), this.getHeight()));
		else this.setDraggableArea(null);
	}
	
	/** @return See {@link #draggableButton} */
	public int getDraggableButton(){
		return this.draggableButton;
	}
	
	/** @param draggableButton See {@link #draggableButton} */
	public void setDraggableButton(int draggableButton){
		this.draggableButton = draggableButton;
	}
	
	/** @return See {@link #draggableSides} */
	public boolean isDraggableSides(){
		return this.draggableSides;
	}
	
	/** @param draggableSides See {@link #draggableSides} */
	public void setDraggableSides(boolean draggableSides){
		this.draggableSides = draggableSides;
	}
	
	/** @return See {@link #disableChildrenWhenDragging} */
	public boolean isDisableChildrenWhenDragging(){
		return this.disableChildrenWhenDragging;
	}
	
	/** @param disableChildrenWhenDragging See {@link #disableChildrenWhenDragging} */
	public void setDisableChildrenWhenDragging(boolean disableChildrenWhenDragging){
		this.disableChildrenWhenDragging = disableChildrenWhenDragging;
	}
	
	/** @return true if this thing is being dragged, false otherwise */
	public boolean currentlyDragging(){
		return this.anchorPoint != null;
	}
	
	/** @return See {@link #draggableSideRange} */
	public double getDraggableSideRange(){
		return this.draggableSideRange;
	}
	
	/** @param draggableSideRange See {@link #draggableSideRange} */
	public void setDraggableSideRange(double draggableSideRange){
		this.draggableSideRange = draggableSideRange;
	}
	
	/** @return See {@link #draggableColor} */
	public ZColor getDraggableColor(){
		return this.draggableColor;
	}
	
	/** @param draggableColor See {@link #draggableColor} */
	public void setDraggableColor(ZColor draggableColor){
		this.draggableColor = draggableColor;
	}
	
	/** @return See {@link #displayDraggableColor} */
	public boolean isDisplayDraggableColor(){
		return this.displayDraggableColor;
	}
	
	/** @param displayDraggableColor See {@link #displayDraggableColor} */
	public void setDisplayDraggableColor(boolean displayDraggableColor){
		this.displayDraggableColor = displayDraggableColor;
	}
	
	/** @return See {@link #minWidth} */
	public Double getMinWidth(){
		return this.minWidth;
	}
	
	/** @param minWidth See {@link #minWidth} */
	public void setMinWidth(Double minWidth){
		this.minWidth = minWidth;
	}
	
	/** @return See {@link #maxWidth} */
	public Double getMaxWidth(){
		return this.maxWidth;
	}
	
	/** @param maxWidth See {@link #maxWidth} */
	public void setMaxWidth(Double maxWidth){
		this.maxWidth = maxWidth;
	}
	
	/** @return See {@link #minHeight} */
	public Double getMinHeight(){
		return this.minHeight;
	}
	
	/** @param minHeight See {@link #minHeight} */
	public void setMinHeight(Double minHeight){
		this.minHeight = minHeight;
	}
	
	/** @return See {@link #maxHeight} */
	public Double getMaxHeight(){
		return this.maxHeight;
	}
	
	/** @param maxHeight See {@link #maxHeight} */
	public void setMaxHeight(Double maxHeight){
		this.maxHeight = maxHeight;
	}
	
	/** @return See {@link #keepInParent} */
	public boolean isKeepInParent(){
		return this.keepInParent;
	}
	
	/** @param keepInParent See {@link #keepInParent} */
	public void setKeepInParent(boolean keepInParent){
		this.keepInParent = keepInParent;
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
		this.format();
	}
	
	/** @return See {@link #formatter} */
	public MenuFormatter getFormatter(){
		return this.formatter;
	}
	
	/** @param formatter See #formatter */
	public void setFormatter(MenuFormatter formatter){
		this.formatter = formatter;
		this.onWidthChange();
		this.onHeightChange();
	}
	
	/** @return See {@link #draggableFormatter} */
	public MenuFormatter getDraggableFormatter(){
		return this.draggableFormatter;
	}
	
	/** @param draggableFormatter See {@link #draggableFormatter} */
	public void setDraggableFormatter(MenuFormatter draggableFormatter){
		this.draggableFormatter = draggableFormatter;
		this.updateFormat(this.draggableFormatter, this.getDraggableArea());
	}
	
	/** @return See {@link #childBounds} */
	public MenuThing getChildBounds(){
		return this.childBounds;
	}
	
	/** @param childBounds See {@link #childBounds} */
	public void setChildBounds(MenuThing childBounds){
		this.childBounds = childBounds;
	}
	
	/** @param size The distance from the edges of the bounds of this thing to use for {@link #childBounds}. Also creates a {@link PixelFormatter} to maintain the size */
	public void setChildBounds(double size){
		if(this.childBounds == null) this.setChildBounds(new MenuThing());
		this.childBoundsFormatter = new PixelFormatter(size);
		this.updateFormat(this.childBoundsFormatter, this);
	}
	
	/** Set {@link #childBounds} to be aligned to the width of the border */
	public void setChildBoundsBorder(){
		this.setChildBounds(this.getBorderWidth());
	}
	
	/** Turn off {@link #childBounds} */
	public void removeChildBounds(){
		this.setChildBounds(null);
	}
	
	/** @return See {@link #childBoundsFormatter} */
	public MenuFormatter getChildBoundsFormatter(){
		return this.childBoundsFormatter;
	}
	
	/** @param childBoundsFormatter See {@link #childBoundsFormatter} */
	public void setChildBoundsFormatter(MenuFormatter childBoundsFormatter){
		this.childBoundsFormatter = childBoundsFormatter;
	}
	
	/** Tell this {@link MenuThing} to not change itself when its parent's width or height changes */
	public void removeFormatting(){
		this.setFormatter(null);
	}
	
	/** @return See {@link #mouseOn} */
	public boolean isMouseOn(){
		return this.mouseOn;
	}
	
	/**
	 * Update the state of if the mouse is on this thing or not
	 *
	 * @param on true if the mouse is on this thing, false otherwise
	 * @param game The game where the update happened
	 */
	private void setMouseOn(Game game, boolean on){
		if(on == this.mouseOn) return;
		this.mouseOn = on;
		if(on) this.mouseEnter(game);
		else this.mouseExit(game);
	}
	
	/**
	 * @param thing Check if the given thing is in this object
	 * @return true if thing is contained by this thing
	 */
	public boolean hasThing(MenuThing thing){
		return this.getThings().contains(thing);
	}
	
	/**
	 * Add a {@link MenuThing} to this {@link Menu} If thing is the same as this object, the thing is not added. Should avoid adding things in a circular manner, i.e. if
	 * thing1 contains thing2 and thing2 contains thing3, then thing3 should not contain thing1. If things are added in a circular manner, infinite recursion will occur. Once
	 * added, any actions which apply to this {@link MenuThing} will also apply to the given thing. This means input, rendering, and game ticks If thing already is in
	 * something else, i.e. it already has a parent, thing will not be added. First remove thing from its current parent using {@link #removeThing(MenuThing)}, then call this
	 * method
	 *
	 * @param thing The thing to add
	 * @return true if the thing was added, false otherwise
	 */
	public boolean addThing(MenuThing thing){
		if(this == thing || this.hasThing(thing) || thing.getParent() != null) return false;
		if((this.getBuffer() == null) != this.defaultUseBuffer) thing.setBuffer(this.defaultUseBuffer);
		thing.setParent(this);
		thing.format();
		return this.things.add(thing);
	}
	
	/**
	 * Helper for updating {@link MenuFormatter} objects
	 *
	 * @param f The formatter to use
	 * @param thing The thing to update
	 */
	private void updateFormat(MenuFormatter f, MenuThing thing){
		if(f == null || thing == null) return;
		f.onWidthChange(thing, this.getWidth());
		f.onHeightChange(thing, this.getHeight());
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
		boolean success = things.remove(thing);
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
		var things = this.getThings();
		if(destroy) for(MenuThing thing : things) thing.destroy();
		things.clear();
	}
	
	/** @return See {@link #things} */
	public List<MenuThing> getThings(){
		return this.things.get(MenuThing.class);
	}
	
	/** @return See {@link #things} */
	public ClassMappedList getAllThings(){
		return this.things;
	}
	
	/**
	 * Move this {@link MenuThing} to the center of the given window
	 *
	 * @param window The window to center by
	 */
	public void center(GameWindow window){
		centerHorizontal(window.getScreenWidth());
		centerVertical(window.getScreenHeight());
	}
	
	/** Move this {@link MenuThing} to the center bounds of its parent. Does nothing if this thing has no parent */
	public void center(){
		centerHorizontal();
		centerVertical();
	}
	
	/** Move this {@link MenuThing} to the center horizontal bounds of its parent. Does nothing if this thing has no parent */
	public void centerHorizontal(){
		if(this.parent == null) return;
		centerHorizontal(this.parent.getWidth());
	}
	
	/** Move this {@link MenuThing} to the center vertical bounds of its parent. Does nothing if this thing has no parent */
	public void centerVertical(){
		if(this.parent == null) return;
		centerVertical(this.parent.getHeight());
	}
	
	/**
	 * Move this {@link MenuThing} to the center it relative to a thing of the given width, on the x axis
	 *
	 * @param width The width to center by
	 */
	public void centerHorizontal(double width){
		this.setRelX((width - this.getWidth()) * 0.5);
	}
	
	/**
	 * Move this {@link MenuThing} to the center it relative to a thing of the given height, on the y axis
	 *
	 * @param height The height to center by
	 */
	public void centerVertical(double height){
		this.setRelY((height - this.getHeight()) * 0.5);
	}
	
	/** Do not call directly */
	@Override
	public void tick(Game game, double dt){
		var things = this.getThings();
		if(this.anchorPoint == null && this.isKeepInParent() && this.parent == null){
			var w = game.getWindow();
			this.keepInBounds(0, 0, w.getScreenWidth(), w.getScreenHeight());
		}
		
		for(int i = 0; i < things.size(); i++){
			MenuThing t = things.get(i);
			t.tick(game, dt);
		}
	}
	
	/** Do not call directly */
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isDisableChildrenWhenDragging() && this.currentlyDragging()) return;
		var things = this.getThings();
		for(int i = 0; i < things.size(); i++){
			MenuThing t = things.get(i);
			t.keyAction(game, button, press, shift, alt, ctrl);
		}
	}
	
	/**
	 * @param x The current x coordinate of the mouse
	 * @param y The current y coordinate of the mouse
	 * @return true if the mouse is intersecting this thing and parent input can be stopped, false otherwise
	 */
	public boolean shouldDisableMouseInput(double x, double y){
		return this.isStopParentInput() && this.getBounds().contains(x, y);
	}
	
	/**
	 * Called when dragging starts. Does nothing by default, override to provide custom behavior
	 *
	 * @param game The game where the drag began in
	 * @param x See {@link #draggingX}
	 * @param y See {@link #draggingY}
	 * @param sideDrag true if the sides of the thing were dragged, false otherwise
	 */
	public void onDragStart(Game game, double x, double y, boolean sideDrag){
		var ts = this.getThings();
		for(var t : ts) t.onDragStart(game, x, y, sideDrag);
	}
	
	/**
	 * Called when dragging stops. Calls this method for all child components by default. Override to provide custom behavior
	 *
	 * @param game The game where the drag ended
	 * @param sideDrag true if the sides of the thing were dragged, false otherwise
	 */
	public void onDragEnd(Game game, boolean sideDrag){
		var ts = this.getThings();
		for(var t : ts) t.onDragEnd(game, sideDrag);
	}
	
	/** Do not call directly */
	@Override
	public boolean mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(!(this.isDisableChildrenWhenDragging() && this.currentlyDragging())){
			var things = this.getThings();
			for(int i = 0; i < things.size(); i++){
				MenuThing t = things.get(i);
				if(t.mouseAction(game, button, press, shift, alt, ctrl)) return true;
			}
		}
		
		this.checkForDraggingStart(game, button, press);
		return shouldDisableMouseInput(game.mouseSX(), game.mouseSY());
	}
	
	/** @return true if the sides of this menu thing are currently being dragged, false otherwise. Does not account for if the anchor point is set */
	public boolean isSideDragging(){
		return this.draggingX != 0 || this.draggingY != 0;
	}
	
	/**
	 * Helper for {@link #mouseAction(Game, int, boolean, boolean, boolean, boolean)}, checking if this element should begin dragging from the mouse
	 *
	 * @param game The game where the button was pressed
	 * @param button The pressed button
	 * @param press true if the button was pressed down, false for released
	 */
	private void checkForDraggingStart(Game game, int button, boolean press){
		if(!press){
			if(this.anchorPoint != null){
				this.anchorPoint = null;
				this.onDragEnd(game, this.isSideDragging());
			}
			this.draggingX = 0;
			this.draggingY = 0;
			return;
		}
		if(button != this.getDraggableButton()) return;
		var d = this.getDraggableArea();
		var mx = game.mouseSX() - this.getRelX() - this.getParentX();
		var my = game.mouseSY() - this.getRelY() - this.getParentY();
		var dragging = false;
		double ax = mx;
		double ay = my;
		// Checking for dragging the entire thing
		if(d != null){
			if(d.getBounds().contains(mx, my)){
				dragging = true;
				this.draggingX = 0;
				this.draggingY = 0;
			}
		}
		// If the entire thing isn't being dragged, and the sides are draggable, check for dragging edges
		if(!dragging && this.isDraggableSides()){
			// Check for the edges being dragged
			// Left edge
			var s = this.getDraggableSideRange();
			var b = new ZRect(0, 0, this.getWidth(), this.getHeight());
			if(b.width(s).contains(mx, my)){
				this.draggingX = -1;
				dragging = true;
			}
			// Right edge
			else if(b.x(b.getX() + b.getWidth() - s).width(s).contains(mx, my)){
				this.draggingX = 1;
				// We need to take into account the width at the point of clicking, so that we are anchored relative to the right side of the menu thing
				ax = mx - this.getWidth();
				dragging = true;
			}
			// Top
			if(b.height(s).contains(mx, my)){
				this.draggingY = -1;
				dragging = true;
			}
			// Bottom
			else if(b.y(b.getY() + b.getHeight() - s).height(s).contains(mx, my)){
				this.draggingY = 1;
				// See comments for the right edge
				ay = my - this.getHeight();
				dragging = true;
			}
		}
		// If any dragging occurred, set the anchor
		if(dragging){
			this.onDragStart(game, this.draggingX, this.draggingY, this.isSideDragging());
			this.anchorPoint = new ZPoint(ax, ay);
		}
	}
	
	/** Do not call directly */
	@Override
	public boolean mouseMove(Game game, double x, double y){
		if(!(this.isDisableChildrenWhenDragging() && this.currentlyDragging())){
			var things = this.getThings();
			for(int i = 0; i < things.size(); i++){
				MenuThing t = things.get(i);
				if(t.mouseMove(game, x, y)) return true;
			}
		}
		var a = this.anchorPoint;
		if(a == null) return shouldDisableMouseInput(x, y);
		boolean fullDrag = this.draggingX == 0 && this.draggingY == 0 && this.isDraggable();
		if(fullDrag){
			// To get the new relative coordinates, take the mouse position, subtract the anchor offset, and subtract the parent offset
			this.setRelX(x - a.getX() - this.getParentX());
			this.setRelY(y - a.getY() - this.getParentY());
		}
		else{
			// Drag the left side
			if(this.draggingX < 0){
				/*
				 Set the width, starting from the current right side of the bounds,
				 going left of that bounds by the difference of the mouse x and parent x,
				 and subtracting out the anchor position to account for the offset
				 */
				this.setWidth(this.getRelX() + this.getWidth() - (x - this.getParentX() - a.getX()), false);
			}
			// Drag the right side
			else if(this.draggingX > 0){
				// Start with the mouse position, subtract out the anchor and parent positions to get the relative coordinates, subtract the relative x to find the new width
				this.setWidth(x - a.getX() - this.getParentX() - this.getRelX(), true);
			}
			// Drag the top
			if(this.draggingY < 0){
				// See comments for the x axis
				this.setHeight(this.getRelY() + this.getHeight() - (y - a.getY() - this.getParentY()), false);
			}
			// Drag the bottom
			else if(this.draggingY > 0){
				// See comments for the x axis
				this.setHeight(y - a.getY() - this.getParentY() - this.getRelY());
			}
		}
		return shouldDisableMouseInput(x, y);
	}
	
	/**
	 * Based on the position of the mouse, this thing, and any of its children, update the state of if the mouse is on this thing.
	 * This updates both this thing, and all of its children
	 *
	 * @param game The game where this update happens
	 * @param x The current x coordinate of the mouse in screen coordinates
	 * @param y The current y coordinate of the mouse in screen coordinates
	 * @param onChild true if the mouse is on a child of the thing, false otherwise
	 * @return true if the mouse is on a child, false otherwise
	 */
	public boolean updateMouseOn(Game game, double x, double y, boolean onChild){
		if(!(this.isDisableChildrenWhenDragging() && this.currentlyDragging())){
			var things = this.getThings();
			for(int i = 0; i < things.size(); i++){
				var t = things.get(i);
				if(t.updateMouseOn(game, x, y, onChild)){
					onChild = true;
				}
			}
		}
		var in = this.getBounds().contains(x, y);
		this.setMouseOn(game, !onChild && in);
		if(!this.useMouseInput(game)) return onChild;
		if(in && !onChild) return true;
		return onChild;
	}
	
	/**
	 * Determine if this thing should accept mouse input and stop mouse input from propagating to further things, false otherwise
	 * @param game The game where the mouse input happened
	 * @return true if it should use mouse input, false otherwise
	 */
	public boolean useMouseInput(Game game){
		return false;
	}
	
	/** Do not call directly */
	@Override
	public boolean mouseWheelMove(Game game, double amount){
		if(!(this.isDisableChildrenWhenDragging() && this.currentlyDragging())){
			var things = this.getThings();
			for(int i = 0; i < things.size(); i++){
				MenuThing t = things.get(i);
				if(t.mouseWheelMove(game, amount)) return true;
			}
		}
		return shouldDisableMouseInput(game.mouseSX(), game.mouseSY());
	}
	
	/**
	 * Called when the mouse enters the active bounds of this thing. Does nothing by default, override to provide custom behavior
	 *
	 * @param game The game when the mouse entered
	 */
	public void mouseEnter(Game game){}
	
	/**
	 * Called when the mouse exits the active bounds of this thing. Does nothing by default, override to provide custom behavior
	 *
	 * @param game The game when the mouse entered
	 */
	public void mouseExit(Game game){}
	
	/** Do not call directly, use {@link #render(Game, Renderer, ZRect)} to draw menu things and override their rendering behavior */
	@Override
	public final void renderBackground(Game game, Renderer r){
	}
	
	/** Do not call directly, use {@link #render(Game, Renderer, ZRect)} to draw menu things and override their rendering behavior */
	@Override
	public final void render(Game game, Renderer r){
	}
	
	/** Do not call directly, use {@link #render(Game, Renderer, ZRect)} to draw menu things and override their rendering behavior */
	@Override
	public final void renderHud(Game game, Renderer r){
		// If using a buffer, draw the contents of the buffer to the relative position
		if(this.usesBuffer()){
			this.buffer.drawToRenderer(this.getRelX(), this.getRelY(), r, game);
			// If not drawing the things to the buffer, draw them directly
			if(!this.isDrawThingsToBuffer()) this.drawThings(game, r, true);
		}
		// Otherwise, draw the object directly with the renderer
		else{
			// Draw relative to the parent
			this.render(game, r, this.getRelBounds());
			this.drawThings(game, r, true);
		}
	}
	
	/**
	 * Draw the contents of just this menu thing, not anything in {@link #things} Anything drawn using this method should be drawn relative to the given bounds, not based on
	 * this thing's position or relative position
	 *
	 * @param game The game associated with this thing
	 * @param r The renderer to use
	 * @param bounds The bounds which this thing will be rendered relative to
	 */
	public void render(Game game, Renderer r, ZRect bounds){
		double b = this.getBorderWidth();
		r.setColor(this.getBorder());
		var x = bounds.getX();
		var y = bounds.getY();
		var w = bounds.getWidth();
		var h = bounds.getHeight();
		r.drawRectangle(x, y, w - b, b);
		r.drawRectangle(x + w - b, y, b, h - b);
		r.drawRectangle(x + b, y + h - b, w - b, b);
		r.drawRectangle(x, y + b, b, h - b);
		
		r.setColor(this.getFill());
		r.drawRectangle(new ZRect(bounds, -b));
		
		if(this.isDisplayDraggableColor() && this.isDraggable()){
			// #issue28 If this uses a buffer, the fill is solid, but this value is transparent and should be on top of the solid color, then this part is still transparent. Why?
			r.setColor(this.getDraggableColor());
			var d = this.getDraggableArea().getRelBounds();
			d = d.x(bounds.getX() + d.getX()).y(bounds.getY() + d.getY());
			r.drawRectangle(d);
			
			r.setColor(this.getBorder());
			d.y += d.getHeight();
			r.drawRectangle(d.height(this.getBorderWidth()));
		}
	}
	
	/**
	 * Render this {@link MenuThing} to the given renderer using the given game, relative to the internal buffer
	 *
	 * @param game The game
	 * @param r The renderer
	 */
	private void renderToBuffer(Game game, Renderer r){
		// Draw relative to the origin
		this.render(game, r, new ZRect(0, 0, this.getWidth(), this.getHeight()));
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
	public void drawThings(Game game, Renderer r, boolean reposition){
		// Position the renderer to draw this thing's things relative to this thing
		if(reposition){
			r.pushMatrix();
			GameWindow w = game.getWindow();
			r.translate(w.sizeScreenToGlX(this.getRelX()), w.sizeScreenToGlY(-this.getRelY()));
		}
		
		// Limit the bounds for drawing children if applicable
		var cb = this.getChildBounds();
		if(cb != null){
			var bufferedBounds = this.getBoundsToBuffer();
			var b = new ZRect(cb.getRelBounds(), bufferedBounds.getX(), bufferedBounds.getY());
			r.pushLimitedBounds(b);
		}
		
		// Draw this thing's things
		// Did I use "thing" enough times?
		var things = this.getThings();
		for(int i = 0; i < things.size(); i++){
			MenuThing t = things.get(i);
			t.renderHud(game, r);
		}
		
		if(cb != null) r.popLimitedBounds();
		
		// Put the matrix back to what it was
		if(reposition) r.popMatrix();
	}
	
	/** A helper class for drawing {@link MenuThing}s */
	public static class MenuBuffer extends DrawableGameBuffer{
		
		/** The thing drawn by this buffer */
		private final MenuThing thing;
		
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
