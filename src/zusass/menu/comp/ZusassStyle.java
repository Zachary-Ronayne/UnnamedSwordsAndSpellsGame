package zusass.menu.comp;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.FontManager;
import zgame.core.graphics.image.ImageManager;
import zgame.core.graphics.texture.RepeatingTexture;
import zgame.core.sound.SoundSource;
import zgame.core.utils.ZRect2D;
import zgame.menu.MenuText;
import zgame.menu.MenuThing;
import zusass.utils.ZusassImages;
import zusass.utils.ZusassSounds;

/** A utility class containing methods for styling Zusass components */
public final class ZusassStyle{
	
	/** The sound source for playing a click button sound */
	private static final SoundSource clickSound = new SoundSource();
	
	/** A generic object holding how menu things should generally draw their textures */
	private final static RepeatingTexture MENU_THING_TEXTURE = new RepeatingTexture(128);
	
	/** Generically apply styling for the given thing to be for Zusass */
	public static void applyStyleThing(MenuThing thing){
		thing.setBorderWidth(4);
		thing.setBorder(new ZColor(.6));
		thing.setFill(new ZColor(1.2));
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
	
	/**
	 * Render the generic fill for Zusass menu things using a textured fill
	 * @param thing The thing which is being rendered
	 * @param r The renderer to use
	 * @param bounds The bounds which should be filled in
	 */
	public static void renderGenericFill(MenuThing thing, Renderer r, ZRect2D bounds){
		r.pushColor(thing.getFill());
		r.pushTextureTintShader();
		r.drawRepeatingTexture(bounds, MENU_THING_TEXTURE, ImageManager.image(ZusassImages.SMOOTH_STONE_GRAYSCALE));
		r.popShader();
		r.popColor();
	}
	
	/**
	 * Render the generic border for Zusass menu things using a textured border
	 * @param thing The thing which is being rendered
	 * @param r The renderer to use
	 * @param borderBounds The bounds which should be filled in
	 */
	public static void renderGenericBorderBounds(MenuThing thing, Renderer r, ZRect2D borderBounds){
		r.pushColor(thing.getBorder());
		r.pushTextureTintShader();
		r.drawRepeatingTexture(borderBounds, MENU_THING_TEXTURE, ImageManager.image(ZusassImages.SMOOTH_STONE_GRAYSCALE));
		r.popShader();
		r.popColor();
	}
	
	/** Play the sound for clicking something */
	public static void playClickSound(){
		Game.get().playEffect(clickSound, ZusassSounds.CLICK);
	}
	
	/** Cannot instantiate {@link ZusassStyle} */
	private ZusassStyle(){
	}
	
}
