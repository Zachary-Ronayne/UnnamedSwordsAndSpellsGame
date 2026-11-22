package zgame.menu;

import zgame.core.state.MenuNode;

/**
 * An object which represents a single {@link Menu}. A menu is a simple object used to group together other {@link MenuThing} objects with a background.
 */
public class Menu extends MenuThing{
	
	/** true if this menu should be destroyed by default when it is removed, false otherwise. This value is true by default */
	private boolean defaultDestroyRemove;
	
	/** The {@link MenuNode} holding this menu, or null if this menu is not in a node */
	private MenuNode node;
	
	/** true if key input from this menu should move down the stack to the rest of the input, false otherwise */
	private boolean propagateKeyAction;
	/** true if mouse input from this menu should move down the stack to the rest of the input, false otherwise */
	private boolean propagateMouseAction;
	/** true if mouse movement from this menu state should move down the stack to the rest of the input, false otherwise */
	private boolean propagateMouseMove;
	/** true if mouse wheel movement from this menu should move down the stack to the rest of the input, false otherwise */
	private boolean propagateMouseWheelMove;
	/** true if when ticking, ticks should move down the menu stack, false otherwise */
	private boolean propagateTick;
	/** true if when rendering, rendering should move down menu the stack, false otherwise */
	private boolean propagateRender;
	
	/** Create an empty menu at (0, 0) */
	public Menu(){
		this(0, 0);
	}
	
	/**
	 * Create an empty menu at the given coordinates
	 *
	 * @param x The x coordinate of the menu
	 * @param y The y coordinate of the menu
	 */
	public Menu(double x, double y){
		this(x, y, 0, 0, false);
	}
	
	/**
	 * Create a {@link Menu} of the given position and size
	 *
	 * @param x See {@link #relX}
	 * @param y See {@link #relY}
	 * @param width See {@link #width}
	 * @param height See {@link #height}
	 * @param useBuffer true to use {@link #buffer}, false otherwise
	 */
	public Menu(double x, double y, double width, double height, boolean useBuffer){
		super(x, y, width, height, useBuffer);
		this.setFill(this.getFill().solid());
		this.setDefaultDestroyRemove(true);
		
		this.setPropagateKeyAction(true);
		this.setPropagateMouseAction(true);
		this.setPropagateMouseMove(true);
		this.setPropagateMouseWheelMove(true);
		this.setPropagateTick(true);
		this.setPropagateRender(true);
	}
	
	/** @return See {@link #defaultDestroyRemove} */
	public boolean isDefaultDestroyRemove(){
		return this.defaultDestroyRemove;
	}
	
	/** @param defaultDestroyRemove See {@link #defaultDestroyRemove} */
	public void setDefaultDestroyRemove(boolean defaultDestroyRemove){
		this.defaultDestroyRemove = defaultDestroyRemove;
	}
	
	@Override
	public boolean useMouseInput(){
		return true;
	}
	
	/** @return See {@link #node} */
	public MenuNode getNode(){
		return this.node;
	}
	
	/** @param node See {@link #node} */
	public void setNode(MenuNode node){
		this.node = node;
	}
	
	/**
	 * Called when this menu is added to the currently displayed menus
	 */
	public void onAdd(){}
	
	/**
	 * Called when this menu is removed from the currently displayed menus
	 */
	public void onRemove(){}
	
	/** @return See {@link #propagateKeyAction} */
	public boolean isPropagateKeyAction(){
		return this.propagateKeyAction;
	}
	
	/** @param propagateKeyAction See {@link #propagateKeyAction} */
	public void setPropagateKeyAction(boolean propagateKeyAction){
		this.propagateKeyAction = propagateKeyAction;
	}
	
	/** @return See {@link #propagateMouseAction} */
	public boolean isPropagateMouseAction(){
		return this.propagateMouseAction;
	}
	
	/** @param propagateMouseAction See {@link #propagateMouseAction} */
	public void setPropagateMouseAction(boolean propagateMouseAction){
		this.propagateMouseAction = propagateMouseAction;
	}
	
	/** @return See {@link #propagateMouseMove} */
	public boolean isPropagateMouseMove(){
		return this.propagateMouseMove;
	}
	
	/** @param propagateMouseMove See {@link #propagateMouseMove} */
	public void setPropagateMouseMove(boolean propagateMouseMove){
		this.propagateMouseMove = propagateMouseMove;
	}
	
	/** @return See {@link #propagateMouseWheelMove} */
	public boolean isPropagateMouseWheelMove(){
		return this.propagateMouseWheelMove;
	}
	
	/** @param propagateMouseWheelMove See {@link #propagateMouseWheelMove} */
	public void setPropagateMouseWheelMove(boolean propagateMouseWheelMove){
		this.propagateMouseWheelMove = propagateMouseWheelMove;
	}
	
	/** @return See {@link #propagateTick} */
	public boolean isPropagateTick(){
		return this.propagateTick;
	}
	
	/** @param propagateTick See {@link #propagateTick} */
	public void setPropagateTick(boolean propagateTick){
		this.propagateTick = propagateTick;
	}
	
	/** @return See {@link #propagateRender} */
	public boolean isPropagateRender(){
		return this.propagateRender;
	}
	
	/** @param propagateRender See {@link #propagateRender} */
	public void setPropagateRender(boolean propagateRender){
		this.propagateRender = propagateRender;
	}
	
	/**
	 * @param propagate The value for {@link #propagateRender}, {@link #propagateTick}, {@link #propagateKeyAction}, {@link #propagateMouseAction},
	 *        {@link #propagateMouseMove}, {@link #propagateMouseWheelMove}
	 */
	public void setPropagate(boolean propagate){
		this.setPropagateKeyAction(propagate);
		this.setPropagateMouseAction(propagate);
		this.setPropagateMouseMove(propagate);
		this.setPropagateMouseWheelMove(propagate);
		this.setPropagateTick(propagate);
		this.setPropagateRender(propagate);
	}
	
	/**
	 * @param propagate The value for {@link #propagateKeyAction}, {@link #propagateMouseAction},
	 *        {@link #propagateMouseMove}, {@link #propagateMouseWheelMove}
	 */
	public void setPropagateInput(boolean propagate){
		this.setPropagateKeyAction(propagate);
		this.setPropagateMouseAction(propagate);
		this.setPropagateMouseMove(propagate);
		this.setPropagateMouseWheelMove(propagate);
	}
	
}
