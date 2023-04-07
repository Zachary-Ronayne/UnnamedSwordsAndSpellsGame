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
		this.setDraggableArea(new ZRect(0, 0, this.getWidth(), 20));
		this.setDraggableButton(GLFW_MOUSE_BUTTON_LEFT);
		this.setDraggableSides(true);
	}
	
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
