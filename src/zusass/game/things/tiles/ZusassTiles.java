package zusass.game.things.tiles;

import zgame.core.graphics.ZColor;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.*;

/** A class defining tiles used in the Zusass game */
public final class ZusassTiles{
	
	public static final String ORIGIN = "zusass";
	
	/** A solid tile used to vary the color of the walls of level rooms */
	public static final CubeTexTintTile LEVEL_WALL_COLOR = solidTintTile("grayBrick", "brickGrayscale", new ZColor(0));
	/** A solid tile used to vary the color of the ceiling of level rooms */
	public static final CubeTexTintTile LEVEL_CEILING_COLOR = solidTintTile("grayBrick", "brickGrayscale", new ZColor(0.1));
	/** A solid tile used to vary the color of the floor of level rooms */
	public static final CubeTexTintTile LEVEL_FLOOR_COLOR = solidTintTile("grayBrick", "brickGrayscale", new ZColor(0.2));
	/** A generic brick tile */
	public static final CubeTexTintTile GRAY_BRICK = solidTintTile("grayBrick", "brickGrayscale", new ZColor(1));
	
	/**
	 * Set the tint colors used by the levels
	 *
	 * @param c The base color
	 */
	public static void setLevelTint(ZColor c){
		LEVEL_WALL_COLOR.setTint(c);
		LEVEL_CEILING_COLOR.setTint(c.scale(1.1));
		LEVEL_FLOOR_COLOR.setTint(c.scale(0.7));
	}
	
	/**
	 * Make a {@link CubeTexTintTile} with a solid hitbox and a tint
	 * @param id See {@link TileType3D#getId()}
	 * @param fileName See {@link CubeTexTile#getFileName()}
	 * @param tint See {@link CubeTexTintTile#getTint()}
	 * @return The generated tile type
	 */
	public static CubeTexTintTile solidTintTile(String id, String fileName, ZColor tint){
		return new CubeTexTintTile(id, ORIGIN, fileName, TileHitbox3D.FULL, Materials.DEFAULT, tint);
	}
	
	/** Cannot instantiate {@link ZusassTiles} */
	private ZusassTiles(){
	}
}
