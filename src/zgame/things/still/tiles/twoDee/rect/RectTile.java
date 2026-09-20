package zgame.things.still.tiles.twoDee.rect;

import zgame.physics.V2D;
import zgame.things.still.tiles.TileType2D;
import zgame.things.still.tiles.twoDee.Tile2D;
import zgame.things.type.bounds.RectangleHitBox;

/** A tile with a full rectangular hit box */
public class RectTile extends Tile2D implements RectangleHitBox{
	
	// TODO add docs
	
	public RectTile(int x, int y, TileType2D type){
		super(x, y, type);
	}
	
	@Override
	public V2D getDimensions(){
		return new V2D(Tile2D.size(), Tile2D.size());
	}
	
}
