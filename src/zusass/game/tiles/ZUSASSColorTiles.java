package zusass.game.tiles;

import zgame.core.graphics.ZColor;
import zgame.things.tiles.ColorTile;
import zgame.things.tiles.TileHitbox;

/** A class for rendering basic filled in colored tiles for temporary tile rendering */
public final class ZUSASSColorTiles {
	
	/** A tile with no hitbox that displays 1 solid color */
	public static final ColorTile BACK_COLOR = new ColorTile("backColor", "zusassColor", TileHitbox.NONE, new ZColor(0));
	/** A tile with no hitbox that displays a darker color than {@link #BACK_COLOR} */
	public static final ColorTile BACK_COLOR_DARK = new ColorTile("backColorDark", "zussassColor", TileHitbox.NONE, new ZColor(0));
	
	/**
	 * Set the colors used by {@link #color} and {@link #colorDark}
	 * 
	 * @param c1 The value for {@link #color}
	 * @param c2 The value for {@link #colorDark}
	 */
	public static void setColors(ZColor c1, ZColor c2){
		BACK_COLOR.setColor(c1);
		BACK_COLOR_DARK.setColor(c2);
	}

	/* Cannot instantiate #ZUSASSColorTiles */
	private ZUSASSColorTiles(){
	}
}
