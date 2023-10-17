package zusass.menu.comp;

import zgame.core.graphics.ZColor;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;
import zusass.ZusassGame;

/** A utility class containing methods for styling Zusass components */
public final class ZusassStyle{
	
	public static void applyStyleThing(ZusassGame zgame, MenuThing thing){
		thing.setBorderWidth(4);
		thing.setBorder(new ZColor(.6));
	}
	
	public static void applyStyleText(ZusassGame zgame, MenuText text){
		applyStyleThing(zgame, text);
		text.setFontColor(new ZColor(.2));
		text.setFontSize(50);
		text.setTextX(10);
		text.setTextY(text.getHeight() - 10);
		text.setFont(zgame.getFont("zfont"));
	}
	
	/** Cannot instantiate {@link ZusassStyle} */
	private ZusassStyle(){
	}
	
}
