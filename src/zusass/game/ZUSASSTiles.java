package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.tiles.Tile;
import zgame.things.tiles.TileHitbox;
import zgame.things.tiles.TileType;

/** The class containing the tiles for the ZUSASS game */
public class ZUSASSTiles extends TileType{

	/**
	 * Create a new tile for the ZUSASS game
	 * 
	 * @param id See {@link #getId()}
	 * @param hitbox See {@link #getHitbox()}
	 */
	protected ZUSASSTiles(String id, TileHitbox hitbox){
		super(id, hitbox);
	}
	
	@Override
	public String getOrigin(){
		return "zusass";
	}
	
	@Override
	public void render(Tile t, Game g, Renderer r){
		// TODO make proper rendering
		r.setColor(new ZColor(0, 0, 0, 0));
		r.drawRectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
	}
	
}
