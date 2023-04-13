package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

import static org.lwjgl.glfw.GLFW.*;

/** The menu which displays on top of the game */
public class InventoryMenu extends ZusassMenu{
	
	/** The formatter used to position this menu */
	private final MenuFormatter defaultFormatter;
	
	/**
	 * Create a new {@link InventoryMenu} for displaying the inventory of something
	 *
	 * @param zgame The game which will use this menu
	 */
	public InventoryMenu(ZusassGame zgame){
		super(zgame, "");
		this.setBorder(new ZColor(.1, 0, 0, 0.8));
		this.setBorderWidth(10);
		this.setFill(new ZColor(.3, 0, 0, .8));
		this.setDraggableArea(new MenuThing(0, 10, 0, 30));
		this.setDraggableFormatter(new PixelFormatter(10.0, 10.0, null, null));
		this.setMinDragWidth(60);
		this.setMinDragHeight(40);
		this.setDraggableButton(GLFW_MOUSE_BUTTON_LEFT);
		this.setDraggableSides(true);
		this.setDraggableSideRange(10);
		this.setMinDragWidth(120);
		this.setMinDragHeight(75);
		
		this.defaultFormatter = new MultiFormatter(new PixelFormatter(null, 10.0, null, null), new PercentFormatter(null, 0.8, null, 0.5));
		this.defaultPosition(zgame);
	}
	
	// TODO make an abstract expandable menu component that has all the dragging and min size and min and max positions all accounted for
	
	/**
	 * Bring this menu to its default position on the right side
	 *
	 * @param zgame The game used to position the menu
	 */
	public void defaultPosition(ZusassGame zgame){
		var w = zgame.getWindow();
		this.setWidth(200);
		this.format(w, this.defaultFormatter);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		
		// If shift is pressed, reposition the menu to the default state
		if(shift) this.defaultPosition((ZusassGame)game);
	}
	
}
