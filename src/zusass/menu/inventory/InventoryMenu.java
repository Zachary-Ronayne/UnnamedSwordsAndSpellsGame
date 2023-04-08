package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zgame.menu.MenuThing;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import static org.lwjgl.glfw.GLFW.*;

/** The menu which displays on top of the game */
public class InventoryMenu extends ZusassMenu{
	
	/**
	 * Create a new {@link InventoryMenu} for displaying the inventory of something
	 * @param zgame The game which will use this menu
	 */
	public InventoryMenu(ZusassGame zgame){
		super(zgame, "");
		// TODO make a utility method that sets the solid color, like makes the border invisible and adds the fill
		this.setBorder(new ZColor(0, 0, 0, 0));
		this.setFill(new ZColor(.3, 0, 0, .8));
		this.setWidth(200);
		this.setHeight(600);
		this.defaultPosition(zgame);
		this.setDraggableArea(new ZRect(15, 15, this.getWidth(), 15));
		this.setMinDragWidth(60);
		this.setMinDragHeight(40);
		this.setDraggableButton(GLFW_MOUSE_BUTTON_LEFT);
		this.setDraggableSides(true);
		
		var m = new MenuThing();
		m.setRelX(10);
		m.setRelY(40);
		m.setWidth(60);
		m.setHeight(60);
		m.setDraggableButton(GLFW_MOUSE_BUTTON_RIGHT);
		m.setDraggableSides(true);
		m.setDraggableArea(new ZRect(10, 10, 40, 40));
		m.removeBorder();
		m.setFill(new ZColor(0, 0, 1));
		this.addThing(m);
	}
	
	/**
	 * Bring this menu to its default position on the right side
	 * @param zgame The game used to position the menu
	 */
	public void defaultPosition(ZusassGame zgame){
		var w = zgame.getWindow();
		this.center(w);
		this.setRelX(w.getWidth() - this.getWidth() * 1.1);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		
		// If shift is pressed, reposition the menu to the default state
		if(shift) this.defaultPosition((ZusassGame)game);
	}
	
}
