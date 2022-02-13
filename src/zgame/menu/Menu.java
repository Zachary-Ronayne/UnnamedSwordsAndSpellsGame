package zgame.menu;

import java.util.ArrayList;
import java.util.Collection;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/**
 * An object which represents a single {@link Menu}. A menu is a simple object which holds other {@link MenuThing} objects, which can include other Menus
 */
public class Menu extends MenuThing{
	
	/** A collection of every {@link MenuThing} in this {@link Menu} */
	private Collection<MenuThing> things;
	
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
		super(x, y);
		this.things = new ArrayList<MenuThing>();
	}
	
	/**
	 * Add a {@link MenuThing} to this {@link Menu}
	 * If thing is the same as this object, the thing is not added.
	 * Should avoid adding things in a circular manor, i.e. if thing1 contains thing2 and thing2 contains thing3, the thing3 should not contain thing1.
	 * If things are added in a circular manor, infinite recursion will occur
	 * 
	 * @param thing The thing to add
	 * @return true if the thing was added, false otherwise
	 */
	public boolean addThing(MenuThing thing){
		if(this == thing) return false;
		this.things.add(thing);
		return true;
	}
	
	/**
	 * Remove the given {@link MenuThing} from this {@link Menu}
	 * 
	 * @param thing The thing to remove
	 * @return true if the thing was removed, false otherwise
	 */
	public boolean removeThing(MenuThing thing){
		return this.things.remove(thing);
	}
	
	@Override
	public void tick(Game game, double dt){
		for(MenuThing t : this.things) t.tick(game, dt);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(MenuThing t : this.things) t.keyAction(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		for(MenuThing t : this.things) t.mouseAction(game, button, press, shift, alt, ctrl);
		
	}
	
	@Override
	public void mouseMove(Game game, double x, double y){
		for(MenuThing t : this.things) t.mouseMove(game, x, y);
	}
	
	@Override
	public void mouseWheelMove(Game game, double amount){
		for(MenuThing t : this.things) t.mouseWheelMove(game, amount);
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
		for(MenuThing t : this.things) t.renderBackground(game, r);
	}
	
	@Override
	public void render(Game game, Renderer r){
		for(MenuThing t : this.things) t.render(game, r);
	}
	
	@Override
	public void renderHud(Game game, Renderer r){
		for(MenuThing t : this.things) t.renderHud(game, r);
	}
	
}
