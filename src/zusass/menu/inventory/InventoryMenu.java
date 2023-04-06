package zusass.menu.inventory;

import zgame.core.graphics.ZColor;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

/** The menu which displays on top of the game */
public class InventoryMenu extends ZusassMenu{
	
	/**
	 * Create a new {@link InventoryMenu} for displaying the inventory of something
	 * @param zgame The game which will use this menu
	 */
	public InventoryMenu(ZusassGame zgame){
		super(zgame, "");
		this.setFill(new ZColor(.5,.5));
		this.setWidth(200);
		this.setHeight(400);
		this.setRelX(100);
		this.setRelY(10);
	}
	
}
