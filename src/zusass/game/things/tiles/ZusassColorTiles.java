package zusass.game.things.tiles;

import zgame.core.graphics.ZColor;
import zgame.things.still.tiles.ColorTile;
import zgame.things.still.tiles.TileHitbox;

/** A class for rendering basic filled in colored tiles for temporary tile rendering */
public final class ZusassColorTiles{
	
	/** A tile with no hitbox that displays 1 solid color */
	public static final ColorTile BACK_COLOR = new ColorTile("backColor", "zusassColor", TileHitbox.NONE, new ZColor(0));
	/** A tile with no hitbox that displays a darker color than {@link #BACK_COLOR} */
	public static final ColorTile BACK_COLOR_DARK = new ColorTile("backColorDark", "zusassColor", TileHitbox.NONE, new ZColor(0));
	
	/**
	 * Set the colors used by {@link #BACK_COLOR} and {@link #BACK_COLOR_DARK}
	 * 
	 * @param c1 The value for {@link #BACK_COLOR}
	 * @param c2 The value for {@link #BACK_COLOR_DARK}
	 */
	public static void setColors(ZColor c1, ZColor c2){
		BACK_COLOR.setColor(c1);
		BACK_COLOR_DARK.setColor(c2);
	}
	
	/** Cannot instantiate {@link ZusassColorTiles} */
	private ZusassColorTiles(){
	}
}
