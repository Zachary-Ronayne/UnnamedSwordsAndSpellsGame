package zusass.menu.comp;

import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.FontManager;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;

/** A utility class containing methods for styling Zusass components */
public final class ZusassStyle{
	
	public static void applyStyleThing(MenuThing thing){
		thing.setBorderWidth(4);
		thing.setBorder(new ZColor(.6));
	}
	
	public static void applyStyleText(MenuText text){
		applyStyleThing(text);
		text.setFontColor(new ZColor(.2));
		text.setFontSize(50);
		text.setTextX(10);
		text.setTextY(text.getHeight() - 10);
		text.setFont(FontManager.getDefaultFont());
	}
	
	/** Cannot instantiate {@link ZusassStyle} */
	private ZusassStyle(){
	}
	
}
