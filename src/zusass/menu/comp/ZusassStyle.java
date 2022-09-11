package zusass.menu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;
import zusass.ZusassData;

/** A utility class containing methods for styling Zusass components */
public final class ZusassStyle{
	
	public static void applyStyle(Game<ZusassData> game, MenuThing<ZusassData> thing){
		if(thing instanceof MenuText){
			MenuText<ZusassData> text = (MenuText<ZusassData>)thing;
			text.setFontColor(new ZColor(.2));
			text.setFontSize(50);
			text.setTextX(10);
			text.setTextY(thing.getHeight() - 10);
			text.setFont(game.getFont("zfont"));
		}
		thing.setBorderWidth(2);
		thing.setBorder(new ZColor(.6));
	}
	
	/** Cannot instantiate {@link #ZusassStyle()} */
	private ZusassStyle(){
	}
	
}
