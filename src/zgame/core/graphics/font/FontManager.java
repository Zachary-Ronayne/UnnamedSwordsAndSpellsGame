package zgame.core.graphics.font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

// TODO abstract out much of this into a ResourceManager

/** A class that keeps track of individual fonts to be used */
public class FontManager{
	
	/** A map containing all fonts handled by this {@link FontManager} */
	private Map<String, GameFont> fonts;
	
	/** Create a new empty {@link FontManager} */
	public FontManager(){
		this.fonts = new HashMap<String, GameFont>();
	}
	
	/**
	 * Add the given font to the manager
	 * 
	 * @param font The font to add
	 * @param name The name of the font, also use this name to get the font with {@link #getFont(String)}
	 */
	public void addFont(GameFont font, String name){
		this.fonts.put(name, font);
	}
	
	/**
	 * Add the font of the given name to the manager. This method assumes the name corresponds to a .ttf font in {@link ZFilePaths#FONTS}
	 * 
	 * @param name The name of the font, also use this name to get the font
	 */
	public void addFont(String name){
		this.addFont(GameFont.create(ZStringUtils.concat(name, ".ttf")), name);
	}
	
	/**
	 * @param name The name of the font to get
	 * @return The font, or null if the font does not exist
	 */
	public GameFont getFont(String name){
		return this.fonts.get(name);
	}
	
	/**
	 * Load all the fonts in {@link ZFilePaths#FONTS}, where the name of the file without a file extension is how they will be referred to using {@link #getFont(String)}
	 * This will only load files with a .ttf extension
	 */
	public void addAllFonts(){
		List<String> names = ZAssetUtils.getNames(ZFilePaths.FONTS, true);
		for(String s : names) if(s.endsWith(".ttf")) this.addFont(s.substring(0, s.length() - 4));
	}
}
