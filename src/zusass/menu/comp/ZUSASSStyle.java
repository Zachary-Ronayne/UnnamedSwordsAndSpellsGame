package zusass.menu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;
import zusass.ZUSASSData;

/** A utility class containing methods for styling ZUSASS components */
public final class ZUSASSStyle{

	public static void applyStyle(Game<ZUSASSData> game, MenuThing<ZUSASSData> thing){
		if(thing instanceof MenuText){
			MenuText<ZUSASSData> text = (MenuText<ZUSASSData>)thing;
			text.setFontColor(new ZColor(.2));
			text.setFontSize(50);
			text.setTextX(10);
			text.setTextY(thing.getHeight() - 10);
			text.setFont(game.getFont("zfont"));
		}
		thing.setBorderWidth(2);
		thing.setBorder(new ZColor(.6));
	}
	
	/** Cannot instantiate {@link #ZUSASSStyle}
	 */
	private ZUSASSStyle(){
	}

}
