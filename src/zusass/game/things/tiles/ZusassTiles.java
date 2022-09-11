package zusass.game.things.tiles;

import zgame.core.graphics.ZColor;
import zgame.things.still.tiles.ColorTile;
import zgame.things.still.tiles.TileHitbox;

/** The class containing the tiles for the ZUSASS game */
public final class ZusassTiles{
	
	/** A tile with no hitbox or display */
	public static final ColorTile AIR = new ColorTile("air", "zusass", TileHitbox.NONE, new ZColor(0, 0, 0, 0));
	
	/** Cannot instantiate {@link #ZUSASSTiles} */
	private ZusassTiles(){
	}
	
}
