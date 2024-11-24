package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult3D;
import zgame.things.type.bounds.HitBox3D;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox3D extends TileHitbox<HitBox3D, Tile3D, CollisionResult3D>{
	
	/** See {@link None} */
	None NONE = new None();
	/** See {@link Full} */
	Full FULL = new Full();
	
	/** For tiles with no collision */
	class None implements TileHitbox3D{
		@Override
		public CollisionResult3D collide(Tile3D t, HitBox3D obj){
			return new CollisionResult3D();
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox3D{
		@Override
		public CollisionResult3D collide(Tile3D t, HitBox3D obj){
			return obj.calculateRectCollision(t.getX(), t.getY(), t.getZ(), t.getWidth(), t.getHeight(), t.getLength(), t.getMaterial());
		}
	}
	
}
