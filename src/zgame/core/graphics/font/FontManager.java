package zgame.core.graphics.font;

import zgame.core.Game;
import zgame.core.asset.AssetManager;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;

/** A class that keeps track of individual fonts to be used */
public class FontManager extends AssetManager<FontAsset>{
	
	/** The name for the default font of the engine */
	public static final String DEFAULT_FONT_NAME = "zfont";
	
	/** The singleton instance which manages font loading */
	private static FontManager instance;
	
	/** Create a new empty {@link FontManager} */
	private FontManager(){
		super(ZFilePaths.FONTS, "ttf");
	}
	
	@Override
	public FontAsset create(String path){
		return FontAsset.create(path);
	}
	
	/** @return See {@link FontManager#get(String)} */
	public static FontAsset getFontAsset(String font){
		return instance().get(font);
	}
	
	/** @return A {@link GameFont} with the given font name */
	public static GameFont getFont(String font){
		return new GameFont(instance().get(font));
	}
	
	/** @return The asset for tge default font */
	public static FontAsset getDefaultFontAsset(){
		return getFontAsset(DEFAULT_FONT_NAME);
	}
	
	/** @return The default font */
	public static GameFont getDefaultFont(){
		return getFont(DEFAULT_FONT_NAME);
	}
	
	/** Add the default font to the manager */
	public static void addDefaultFont(){
		instance.add(DEFAULT_FONT_NAME);
	}
	
	/** @return The singleton instance for font management */
	public static FontManager instance(){
		if(instance == null){
			ZConfig.error("Failed to get FontManager instance, call FontManager.init() or Game.initAssetManagers() before using fonts");
		}
		
		return instance;
	}
	
	/** Initialize the singleton for {@link FontManager}, must be called before any fonts are used */
	public static void init(){
		if(instance != null) return;
		instance = new FontManager();
	}
	
	/** Destroy all resources used by the manager, generally should only be called by {@link Game} on shut down */
	public static void destroyFonts(){
		if(instance != null) instance.destroy();
		instance = null;
	}
}
