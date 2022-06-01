package zgame.things.tiles;

import zgame.core.graphics.ZColor;

/** A class containing base generic tiles for the game */
public final class BaseTiles{
	
	/** The string used as the base origin name for the base tiles of the engine */
	public static final String BASE_ORIGIN = "base";
	
	/** An empty tile which has no hitbox or display */
	public static final ColorTile AIR = new ColorTile("air", BASE_ORIGIN, TileHitbox.NONE, new ZColor(0, 0, 0, 0));
	/** A tile with no hitbox which displays as a dark color */
	public static final ColorTile BACK_DARK = new ColorTile("backDark", BASE_ORIGIN, TileHitbox.NONE, new ZColor(.2));
	/** A tile with no hitbox which displays as a light color */
	public static final ColorTile BACK_LIGHT = new ColorTile("backLight", BASE_ORIGIN, TileHitbox.NONE, new ZColor(.35));
	/** A tile with a solid hitbox that displays as a dark solid color */
	public static final ColorTile WALL_DARK = new ColorTile("wallDark", BASE_ORIGIN, TileHitbox.FULL, new ZColor(.55));
	/** A tile with a solid hitbox that displays as a light solid color */
	public static final ColorTile WALL_LIGHT = new ColorTile("wallLight", BASE_ORIGIN, TileHitbox.FULL, new ZColor(.65));
	
	/** Cannot instantiate {@link BaseTiles} */
	private BaseTiles(){
	}
	
}
