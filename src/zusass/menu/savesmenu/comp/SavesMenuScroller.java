package zusass.menu.savesmenu.comp;

import zgame.core.graphics.ZColor;
import zgame.menu.scroller.MenuScrollerButton;
import zgame.menu.scroller.VerticalScroller;
import zusass.ZusassGame;

/** A {@link VerticalScroller} made for the saves menu */
public class SavesMenuScroller extends VerticalScroller{

	/** The x coordinate of this {@link SavesMenuScroller} */
	public static final double X = 1200;
	/** The y coordinate of this {@link SavesMenuScroller} */
	public static final double Y = 20;

	/**
	 * Create the {@link SavesMenuScroller}
	 * 
	 * @param game The game associated with this thing
	 */
	public SavesMenuScroller(ZusassGame zgame){
		super(X, Y, 25, 680, 0, zgame);
		this.setFill(new ZColor(.8));
		this.setBorder(new ZColor(0.8));
		this.setBorderWidth(1);

		MenuScrollerButton button = this.getButton();
		button.setFill(new ZColor(.3));
		button.setBorder(new ZColor(0.8));
		button.setBorderWidth(2);
	}

}
