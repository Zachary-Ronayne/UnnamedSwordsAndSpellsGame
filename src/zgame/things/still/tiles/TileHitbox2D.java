package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResult2D;
import zgame.things.type.bounds.HitBox2D;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox2D extends TileHitbox<HitBox2D, Tile2D, CollisionResult2D>{
	
	/** See {@link None} */
	None NONE = new None();
	/** See {@link Full} */
	Full FULL = new Full();
	/** See {@link Circle} */
	Circle CIRCLE = new Circle();
	/** See {@link Full} */
	BottomSlab BOTTOM_SLAB = new BottomSlab();
	
	/** For tiles with no collision */
	class None implements TileHitbox2D{
		@Override
		public CollisionResult2D collide(Tile2D t, HitBox2D obj){
			return new CollisionResult2D();
		}
		
		@Override
		public boolean intersectsTile(Tile2D t, HitBox2D obj){
			return false;
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox2D{
		@Override
		public CollisionResult2D collide(Tile2D t, HitBox2D obj){
			return obj.calculateRectCollision(t.getX(), t.getY(), t.getWidth(), t.getHeight(), t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile2D t, HitBox2D obj){
			return obj.intersectsRect(t.getX(), t.getY(), t.getWidth(), t.getHeight());
		}
	}
	
	/** For tiles whose hitbox is a circle inscribed by the tile */
	class Circle implements TileHitbox2D{
		@Override
		public CollisionResult2D collide(Tile2D t, HitBox2D obj){
			return obj.calculateCircleCollision(t.centerX(), t.centerY(), t.getWidth() * 0.5, t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile2D t, HitBox2D obj){
			return obj.intersectsCircle(t.centerX(), t.centerY(), t.getWidth() * 0.5);
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class BottomSlab implements TileHitbox2D{
		@Override
		public CollisionResult2D collide(Tile2D t, HitBox2D obj){
			var h = t.getHeight() * 0.5;
			return obj.calculateRectCollision(t.getX(), t.getY() + h, t.getWidth(), h, t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile2D t, HitBox2D obj){
			var h = t.getHeight() * 0.5;
			return obj.intersectsRect(t.getX(), t.getY() + h, t.getWidth(), h);
		}
	}
	
}
