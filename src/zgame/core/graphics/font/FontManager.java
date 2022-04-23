package zgame.core.graphics.font;

import zgame.core.asset.AssetManager;
import zgame.core.utils.ZFilePaths;

/** A class that keeps track of individual fonts to be used */
public class FontManager extends AssetManager<FontAsset>{
	
	/** Create a new empty {@link FontManager} */
	public FontManager(){
		super(ZFilePaths.FONTS, "ttf");
	}

	@Override
	public FontAsset create(String path){
		return FontAsset.create(path);
	}
}
