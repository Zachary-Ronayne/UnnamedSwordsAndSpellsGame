package zgame.things.tiles;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;

/** A class containing base generic tiles for the game */
public class BaseTiles extends TileType{
	
	/** An empty tile which has no hitbox or display */
	public static final BaseTiles AIR = new BaseTiles("air", TileHitbox.NONE);
	/** A tile with no hitbox which displays as a dark color */
	public static final BaseTiles BACK_DARK = new BaseTiles("backDark", TileHitbox.NONE);
	/** A tile with no hitbox which displays as a light color */
	public static final BaseTiles BACK_LIGHT = new BaseTiles("backLight", TileHitbox.NONE);
	/** A tile with a solid hitbox that displays as a dark solid color */
	public static final BaseTiles WALL_DARK = new BaseTiles("wallDark", TileHitbox.FULL);
	/** A tile with a solid hitbox that displays as a light solid color */
	public static final BaseTiles WALL_LIGHT = new BaseTiles("wallLight", TileHitbox.FULL);

	/**
	 * Create a new base tile
	 * 
	 * @param id See {@link #getId()}
	 * @param hitbox See {@link #getHitbox()}
	 */
	protected BaseTiles(String id, TileHitbox hitbox){
		super(id, hitbox);
	}

	@Override
	public String getOrigin(){
		return "base";
	}

	@Override
	public void render(Tile t, Game g, Renderer r){
		// TODO make proper color rendering
		if(this.getId().equals(BACK_LIGHT.getId())) r.setColor(new ZColor(.6));
		if(this.getId().equals(BACK_DARK.getId())) r.setColor(new ZColor(.3));
		r.drawRectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
	}

}
