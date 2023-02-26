package zgame.things.still.tiles;

import zgame.core.graphics.ZColor;
import zgame.physics.material.Materials;

/** A class containing base generic tiles for the game */
public final class BaseTiles{
	
	/** The string used as the base origin name for the base tiles of the engine */
	public static final String BASE_ORIGIN = "base";
	
	/** An empty tile which has no hitbox or display */
	public static final ColorTile AIR = new ColorTile("air", BASE_ORIGIN, TileHitbox.NONE, new ZColor(0, 0, 0, 0), Materials.NONE);
	/** A tile with no hitbox which displays as a dark color */
	public static final ColorTile BACK_DARK = new ColorTile("backDark", BASE_ORIGIN, TileHitbox.NONE, new ZColor(.2), Materials.NONE);
	/** A tile with no hitbox which displays as a light color */
	public static final ColorTile BACK_LIGHT = new ColorTile("backLight", BASE_ORIGIN, TileHitbox.NONE, new ZColor(.35), Materials.NONE);
	/** A tile with a solid hitbox that displays as a dark solid color */
	public static final ColorTile WALL_DARK = new ColorTile("wallDark", BASE_ORIGIN, TileHitbox.FULL, new ZColor(.55));
	/** A tile with a solid hitbox that displays as a light solid color */
	public static final ColorTile WALL_LIGHT = new ColorTile("wallLight", BASE_ORIGIN, TileHitbox.FULL, new ZColor(.65));
	/** A tile with a solid hitbox that represents the bottom half of a tile */
	public static final ColorTile WALL_BOTTOM_SLAB = new BottomSlabColorTile("wallBottomSlab", BASE_ORIGIN, new ZColor(.5), Materials.DEFAULT);
	/** A pink tile with a lot of bounciness */
	public static final ColorTile BOUNCY = new ColorTile("bouncy", BASE_ORIGIN, TileHitbox.FULL, new ZColor(1, .5, .5), Materials.BOUNCE);
	/** A brown tile with a huge friction value */
	public static final ColorTile HIGH_FRICTION = new ColorTile("highFriction", BASE_ORIGIN, TileHitbox.FULL, new ZColor(.25, .125, .0625), Materials.HIGH_FRICTION);
	
	/** Cannot instantiate {@link BaseTiles} */
	private BaseTiles(){
	}
	
}
