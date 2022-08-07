package zusass.menu.savesmenu.comp;

import zgame.core.graphics.ZColor;
import zgame.menu.scroller.MenuScrollerButton;
import zgame.menu.scroller.VerticalScroller;
import zusass.ZUSASSData;

/** A {@link VerticalScroller} made for the saves menu */
public class SavesMenuScroller extends VerticalScroller<ZUSASSData>{

	/** Create the {@link SavesMenuScroller} */
	public SavesMenuScroller(){
		super(1200, 20, 25, 680, 0);
		this.setFill(new ZColor(.8));
		this.setBorder(new ZColor(0.8));
		this.setBorderWidth(1);

		MenuScrollerButton<ZUSASSData> button = this.getButton();
		button.setFill(new ZColor(.3));
		button.setBorder(new ZColor(0.8));
		button.setBorderWidth(2);
	}

}
