package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.tiles.Tile;
import zgame.things.tiles.TileHitbox;
import zgame.things.tiles.TileType;

/** A class for rendering basic filled in colored tiles for temporary tile rendering */
public class ZUSASSColorTiles extends TileType{
	
	/** A tile with no hitbox that displays 1 solid color */
	public static final ZUSASSColorTiles BACK_COLOR = new ZUSASSColorTiles("backColor", TileHitbox.NONE);
	/** A tile with no hitbox that displays a darker color than {@link #BACK_COLOR} */
	public static final ZUSASSColorTiles BACK_COLOR_DARK = new ZUSASSColorTiles("backColorDark", TileHitbox.NONE);
	
	/** The color to draw {@link #BACK_COLOR} */
	public static ZColor color = new ZColor(.6);
	/** The color to draw {@link #BACK_COLOR_DARK} */
	public static ZColor colorDark = new ZColor(.3);
	
	/**
	 * Set the colors used by {@link #color} and {@link #colorDark}
	 * 
	 * @param c1 The value for {@link #color}
	 * @param c2 The value for {@link #colorDark}
	 */
	public static void setColors(ZColor c1, ZColor c2){
		color = c1;
		colorDark = c2;
	}

	/**
	 * Create a new ZUSASSColorTiles
	 * 
	 * @param id See {@link #getId()}
	 * @param hitbox See {@link #getHitbox()}
	 */
	protected ZUSASSColorTiles(String id, TileHitbox hitbox){
		super(id, hitbox);
	}
	
	@Override
	public String getOrigin(){
		return "zusassColor";
	}
	
	@Override
	public void render(Tile t, Game g, Renderer r){
		// TODO make proper color rendering
		if(this.getId().equals(BACK_COLOR.getId())) r.setColor(color);
		if(this.getId().equals(BACK_COLOR_DARK.getId())) r.setColor(colorDark);
		r.drawRectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
	}
}
