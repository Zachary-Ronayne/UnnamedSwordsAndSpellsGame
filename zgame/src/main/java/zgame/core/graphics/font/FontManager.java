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
		super(ZFilePaths.fonts(), "ttf");
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
		var asset = instance().get(font);
		if(asset == null) ZConfig.error("Cannot get font: ", font, ", no asset has been loaded. Ensure FontManager is initialized and a font under that name has been loaded");
		return new GameFont(asset);
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
		if(instance.get(DEFAULT_FONT_NAME) == null) instance.add(new FontAsset(ZFilePaths.FONTS_CORE + DEFAULT_FONT_NAME + ".ttf"), DEFAULT_FONT_NAME);
	}
	
	/** @return The singleton instance for font management */
	public static FontManager instance(){
		if(instance == null){
			ZConfig.error("Failed to get FontManager instance, call FontManager.init() or Game.initAssetManagers() before using fonts");
		}
		
		return instance;
	}
	
	/** Initialize all font assets managed by this manager */
	public static void initFontAssets(){
		for(var asset : instance().getAll()){
			asset.init();
		}
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
