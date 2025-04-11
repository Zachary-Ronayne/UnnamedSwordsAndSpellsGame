package zusass.game.things.tiles;

import zgame.core.graphics.ZColor;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.CubeTile;
import zgame.things.still.tiles.TileHitbox3D;

/** A class for rendering basic filled in colored tiles for temporary tile rendering */
public final class ZusassColorTiles{
	
	public static final String ORIGIN = "zusass";
	
	/** A tile with no hitbox that displays 1 solid color */
	public static final CubeTile BACK_COLOR = new CubeTile("backColor", ORIGIN, TileHitbox3D.FULL, new ZColor(0), Materials.DEFAULT);
	/** A tile with no hitbox that displays a darker color than {@link #BACK_COLOR} */
	public static final CubeTile BACK_COLOR_DARK = new CubeTile("backColorDark", ORIGIN, TileHitbox3D.FULL, new ZColor(0), Materials.DEFAULT);
	
	/**
	 * Set the colors used by {@link #BACK_COLOR} and {@link #BACK_COLOR_DARK}
	 *
	 * @param c1 The value for {@link #BACK_COLOR}
	 * @param c2 The value for {@link #BACK_COLOR_DARK}
	 */
	public static void setColors(ZColor c1, ZColor c2){
		BACK_COLOR.setBaseColor(c1);
		BACK_COLOR_DARK.setBaseColor(c2);
	}
	
	/** Cannot instantiate {@link ZusassColorTiles} */
	private ZusassColorTiles(){
	}
}
