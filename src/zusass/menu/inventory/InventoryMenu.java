package zusass.menu.inventory;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zgame.menu.MenuThing;
import zgame.menu.format.MenuFormatter;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

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
		this.makeDraggable(10, 30);
		this.setBorder(new ZColor(.1, 0, 0, 0.8));
		this.setFill(new ZColor(.3, 0, 0, .8));
		this.setMinWidth(60.0);
		this.setMinHeight(40.0);
		this.setMinWidth(120.0);
		this.setMinHeight(75.0);
		
		this.defaultFormatter = new MultiFormatter(new PixelFormatter(null, 10.0, null, null), new PercentFormatter(null, 0.8, null, 0.5));
		this.defaultPosition(zgame);
		
		var m = new MenuThing();
		m.setFormatter(new PixelFormatter(40));
		m.setFullColor(new ZColor(.5));
		this.addThing(m);
	}
	
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
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		
		r.setColor(new ZColor(.8, .3));
		var d = this.getDraggableArea().getRelBounds();
		r.drawRectangle(d.x(bounds.getX() + d.getX()).y(bounds.getY() + d.getY()));
	}
}
