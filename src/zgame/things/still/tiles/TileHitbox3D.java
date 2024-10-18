package zgame.things.still.tiles;

import zgame.core.utils.ZRect2D;
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
		
		@Override
		public boolean intersectsTile(Tile3D t, HitBox3D obj){
			return false;
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox3D{
		@Override
		public CollisionResult3D collide(Tile3D t, HitBox3D obj){
			// TODO make a real implementation and use a cylinder hitbox for entities
			
			if(!new ZRect2D(t.getX() - t.getWidth() * 0.5, t.getZ() - t.getLength() * 0.5, t.getWidth(), t.getLength())
					.intersects(obj.getX() - obj.getWidth() * 0.5, obj.getZ() - obj.getLength() * 0.5, obj.getWidth(), obj.getLength())) return new CollisionResult3D();
			
			if(obj.getY() >= t.getY() + t.getHeight()) return new CollisionResult3D();
			
			return new CollisionResult3D(0, t.getY() + t.getHeight() - obj.getY(), 0, false, false, true, t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile3D t, HitBox3D obj){
			// TODO implement
			return false;
		}
	}
	
}
