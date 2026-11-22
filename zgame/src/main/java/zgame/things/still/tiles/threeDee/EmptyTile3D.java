package zgame.things.still.tiles.threeDee;

import zgame.physics.V3D;
import zgame.physics.material.Materials;
import zgame.things.still.tiles.TileType3D;
import zgame.things.type.bounds.threeDee.clickable.ClickerBounds;
import zgame.things.type.bounds.threeDee.shape.EmptyShape3D;
import zgame.world.Direction3D;

/** A tile type that has no bounds */
public class EmptyTile3D extends TileType3D implements EmptyShape3D{
	
	/**
	 * Create a new {@link TileType3D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 */
	public EmptyTile3D(String id, String origin){
		super(id, origin, V3D.EMPTY, Materials.NONE);
	}
	
	@Override
	public boolean canCollide(Tile3D t, Direction3D face){
		return false;
	}
	
	// Empty tiles will never have click distances
	@Override
	public double clickDistance(Tile3D t, ClickerBounds clicker){
		return -1;
	}
}
