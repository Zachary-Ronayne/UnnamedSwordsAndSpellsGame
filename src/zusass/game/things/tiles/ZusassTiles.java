package zusass.game.things.tiles;

import zgame.core.graphics.ZColor;
import zgame.things.still.tiles.ColorTile;
import zgame.things.still.tiles.TileHitbox2D;

/** The class containing the tiles for the Zusass game */
public final class ZusassTiles{
	
	/** A tile with no hitbox or display */
	public static final ColorTile AIR = new ColorTile("air", "zusass", TileHitbox2D.NONE, new ZColor(0, 0, 0, 0));
	
	/** Cannot instantiate {@link ZusassTiles} */
	private ZusassTiles(){
	}
	
}
