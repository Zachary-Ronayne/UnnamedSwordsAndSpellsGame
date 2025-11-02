package zusass.menu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.FontManager;
import zgame.core.sound.SoundSource;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;

/** A utility class containing methods for styling Zusass components */
public final class ZusassStyle{
	
	/** The sound source for playing a click button sound */
	private static final SoundSource clickSound = new SoundSource();
	
	/** Generically apply styling for the given thing to be for Zusass */
	public static void applyStyleThing(MenuThing thing){
		thing.setBorderWidth(4);
		thing.setBorder(new ZColor(.6));
	}
	
	/** Generically apply styling for the given text to be for Zusass */
	public static void applyStyleText(MenuText text){
		applyStyleThing(text);
		text.setFontColor(new ZColor(.2));
		text.setFontSize(50);
		text.setTextX(10);
		text.setTextY(text.getHeight() - 10);
		text.setFont(FontManager.getDefaultFont());
	}
	
	/** Play the sound for clicking something */
	public static void playClickSound(){
		Game.get().playEffect(clickSound, "click");
	}
	
	/** Cannot instantiate {@link ZusassStyle} */
	private ZusassStyle(){
	}
	
}
