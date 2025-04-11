package zgame.things.still.tiles;

import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.things.type.bounds.ClickerBounds;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Direction3D;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox3D extends TileHitbox<HitBox3D, Tile3D, CollisionResult3D>{
	
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
		public CollisionResult3D collide(Tile3D t, HitBox3D obj){
			return new CollisionResult3D();
		}
		
		@Override
		public double clickDistance(Tile3D t, ClickerBounds clicker){
			// With no hitbox, there will never be a distance
			return -1;
		}
		
		@Override
		public boolean canCollide(Direction3D face){
			return false;
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox3D{
		@Override
		public CollisionResult3D collide(Tile3D t, HitBox3D obj){
			return obj.calculateRectCollision(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength(), t.getMaterial(), t.getCollisionFaces());
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
			
			var clickDirection = new ZVector3D(clicker.getClickYaw(), clicker.getClickPitch(), 1, false);
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
