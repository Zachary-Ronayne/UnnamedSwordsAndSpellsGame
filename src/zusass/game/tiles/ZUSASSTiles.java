package zusass.game.tiles;

import zgame.core.graphics.ZColor;
import zgame.things.tiles.ColorTile;
import zgame.things.tiles.TileHitbox;

/** The class containing the tiles for the ZUSASS game */
public final class ZUSASSTiles{
	
	/** A tile with no hitbox or display */
	public static final ColorTile AIR = new ColorTile("air", "zusass", TileHitbox.NONE, new ZColor(0, 0, 0, 0));
	
	/** Cannot instantiate {@link #ZUSASSTiles} */
	private ZUSASSTiles(){
	}
	
}
