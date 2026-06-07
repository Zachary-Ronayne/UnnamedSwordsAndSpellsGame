package zgame.things.still.tiles.threeDee;

import zgame.core.utils.ZMath;
import zgame.physics.V3D;
import zgame.physics.collision.Collision3D;
import zgame.things.still.tiles.Tile;
import zgame.things.still.tiles.TileHitbox;
import zgame.things.type.bounds.ClickerBounds;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Direction3D;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox3D extends TileHitbox<V3D>{
	
	/** See {@link None} */
	None NONE = new None();
	/** See {@link Full} */
	Full FULL = new Full();
	
	/**
	 * Find the distance from the given clicker to click on the given tile
 	 * @param t The tile to check
	 * @param clicker The thing doing the clicking
	 * @return The distance, or a negative number if there's no click
	 */
	double clickDistance(Tile3D t, ClickerBounds clicker);
	
	/**
	 * Used to determine which faces of the tile should be checked for collision
	 *
	 * @param face The face which is being checked
	 * @return true if this tile hitbox can collide with things, false otherwise.
	 */
	boolean canCollide(Direction3D face);
	
	/** For tiles with no collision */
	class None implements TileHitbox3D{
		@Override
		public Collision3D collide(Tile<V3D> t, HitBox<V3D> obj){
			return new Collision3D(obj);
		}
		
		@Override
		public double clickDistance(Tile3D t, ClickerBounds clicker){
			// With no hitbox, there will never be a distance
			return -1;
		}
		
		// TODO figure out where canCollide is supposed to be called
		@Override
		public boolean canCollide(Direction3D face){
			return false;
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox3D{
		@Override
		public Collision3D collide(Tile<V3D> t, HitBox<V3D> obj){
			var tPos = t.getPosition();
			var tDims = t.getDimensions();
			
			return obj.asHitbox(HitBox3D.class)
					.calculateRectCollision(
							tPos.getX(), tPos.getY(), tPos.getZ(),
							tDims.getWidth(), tDims.getHeight(), tDims.getLength(),
							t.getMaterial(), t.asTile(Tile3D.class).getCollisionFaces()
					);
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
		
		@Override
		public boolean canCollide(Direction3D face){
			return true;
		}
	}
	
}
