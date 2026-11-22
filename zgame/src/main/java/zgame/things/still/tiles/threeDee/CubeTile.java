package zgame.things.still.tiles.threeDee;

import zgame.core.utils.ZMath;
import zgame.physics.V3D;
import zgame.physics.material.Material;
import zgame.things.still.tiles.TileType3D;
import zgame.things.type.bounds.threeDee.clickable.ClickerBounds;
import zgame.things.type.bounds.threeDee.shape.RectPrismShape;
import zgame.world.Direction3D;

/** A simple cube shaped tile which has a constant material */
public abstract class CubeTile extends TileType3D implements RectPrismShape{
	
	/**
	 * Create a new {@link CubeTile} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param material See {@link #material}
	 */
	public CubeTile(String id, String origin, Material material){
		super(id, origin, V3D.EMPTY, material);
	}
	
	@Override
	public V3D getDimensions(){
		double s = Tile3D.size();
		return new V3D(s, s, s);
	}
	
	@Override
	public boolean[] getCollisionFaces(){
		// TODO what should collision faces be? Should these even remain as a method?
		return new boolean[]{true, true, true, true, true, true};
	}
	
	@Override
	public boolean canCollide(Tile3D t, Direction3D face){
		return this.getCollisionFaces()[face.i()];
	}
	
	@Override
	public double clickDistance(Tile3D t, ClickerBounds clicker){
		// If the clicker is inside the bounds of the tile, then the distance to click will always be 0
		if(
				ZMath.in(t.minX(), clicker.getClickX(), t.maxX()) &&
				ZMath.in(t.minY(), clicker.getClickY(), t.maxY()) &&
				ZMath.in(t.minZ(), clicker.getClickZ(), t.maxZ())
		) {
			return 0;
		}
		
		var clickDirection = new V3D(clicker.getClickYaw(), clicker.getClickPitch(), 1, false);
		return ZMath.rayDistanceToRectPrism(clicker.getClickX(), clicker.getClickY(), clicker.getClickZ(),
				clickDirection.getX(), clickDirection.getY(), clickDirection.getZ(),
				t.minX(), t.minY(), t.minZ(), t.maxX(), t.maxY(), t.maxZ());
	}
}
