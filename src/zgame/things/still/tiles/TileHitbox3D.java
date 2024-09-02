package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult;
import zgame.things.type.bounds.HitBox2D;
import zgame.things.type.bounds.HitBox3D;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox3D extends TileHitbox<HitBox3D, Tile3D>{
	
	// TODO have a separate hitbox class for 2D and 3D
	
	/** See {@link None} */
	None NONE = new None();
	/** See {@link Full} */
	Full FULL = new Full();
	
	/** For tiles with no collision */
	class None implements TileHitbox3D{
		@Override
		public CollisionResult collide(Tile3D t, HitBox3D obj){
			return new CollisionResult();
		}
		
		@Override
		public boolean intersectsTile(Tile3D t, HitBox3D obj){
			return false;
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox3D{
		@Override
		public CollisionResult collide(Tile3D t, HitBox3D obj){
			// TODO implement
			return new CollisionResult();
		}
		
		@Override
		public boolean intersectsTile(Tile3D t, HitBox3D obj){
			// TODO implement
			return false;
		}
	}
	
}
