package zgame.things.still.tiles;

import zgame.core.graphics.ZColor;
import zgame.physics.material.Materials;

/** A class containing base generic tiles for a game in 3D */
public final class BaseTiles3D{
	
	/** The string used as the base origin name for the base tiles of the engine */
	public static final String BASE_ORIGIN = "base_3D";
	
	// TODO tune these basic tiles to make sense for 3D
	/** An empty tile which has no hitbox or display */
	public static final CubeTile AIR = new CubeTile("air", BASE_ORIGIN, TileHitbox3D.NONE, new ZColor(0, 0, 0, 0), Materials.NONE);
	/** A tile with a solid hitbox that displays as a dark solid color */
	public static final CubeTile SOLID_DARK = new CubeTile("wallDark", BASE_ORIGIN, TileHitbox3D.NONE, new ZColor(.55), Materials.DEFAULT);
	/** A tile with a solid hitbox that displays as a light solid color */
	public static final CubeTile SOLID_LIGHT = new CubeTile("wallLight", BASE_ORIGIN, TileHitbox3D.NONE, new ZColor(.65), Materials.DEFAULT);
	/** A pink tile with a lot of bounciness */
	public static final CubeTile BOUNCY = new CubeTile("bouncy", BASE_ORIGIN, TileHitbox3D.FULL, new ZColor(1, .5, .5), Materials.BOUNCE);
	/** A brown tile with a huge friction value */
	public static final CubeTile HIGH_FRICTION = new CubeTile("highFriction", BASE_ORIGIN, TileHitbox3D.FULL, new ZColor(.25, .125, .0625), Materials.HIGH_FRICTION);
	
	/** Cannot instantiate {@link BaseTiles3D} */
	private BaseTiles3D(){
	}
	
}
