package zgame.things.still.tiles.twoDee.empty;

import zgame.physics.V2D;
import zgame.things.still.tiles.TileType2D;
import zgame.things.still.tiles.twoDee.Tile2D;
import zgame.things.type.bounds.EmptyHitBox2D;

/** A tile with no hitbox */
public class EmptyTile extends Tile2D implements EmptyHitBox2D{
	// TODO add docs
	
	public EmptyTile(int x, int y, TileType2D type){
		super(x, y, type);
	}
	
	@Override
	public V2D getDimensions(){
		return new V2D(Tile2D.size(), Tile2D.size());
	}
	
}
