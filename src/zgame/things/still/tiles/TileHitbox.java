package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResponse;
import zgame.things.type.HitBox;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox {
	
	/** See {@link None} */
	None NONE = new None();
	/** See {@link Full} */
	Full FULL = new Full();
	/** See {@link Circle} */
	Circle CIRCLE = new Circle();
	/** See {@link Full} */
	BottomSlab BOTTOM_SLAB = new BottomSlab();
	
	/**
	 * Based on the given rectangular bounds, determine the new position of the rectangle when it collides with the given tile
	 * The coordinates in this method are treated as the upper left hand corner of the rectangle
	 *
	 * @param t The {@link Tile} to collide
	 * @param obj The object with a hitbox which collides with the given {@link Tile}
	 * @return A point to reposition the rectangle to
	 */
	CollisionResponse collide(Tile t, HitBox obj);
	
	/**
	 * Determine if a hitbox hits this {@link TileHitbox}
	 *
	 * @param obj The hitbox to check
	 * @return true if they intersect, false otherwise
	 */
	boolean intersectsTile(Tile t, HitBox obj);
	
	/** For tiles with no collision */
	class None implements TileHitbox{
		@Override
		public CollisionResponse collide(Tile t, HitBox obj){
			return new CollisionResponse();
		}
		
		@Override
		public boolean intersectsTile(Tile t, HitBox obj){
			return false;
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox{
		@Override
		public CollisionResponse collide(Tile t, HitBox obj){
			return obj.calculateRectCollision(t.getX(), t.getY(), t.getWidth(), t.getHeight(), t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile t, HitBox obj){
			return obj.intersectsRect(t.getX(), t.getY(), t.getWidth(), t.getHeight());
		}
	}
	
	/** For tiles whose hitbox is a circle inscribed by the tile */
	class Circle implements TileHitbox{
		@Override
		public CollisionResponse collide(Tile t, HitBox obj){
			return obj.calculateCircleCollision(t.centerX(), t.centerY(), t.getWidth() * 0.5, t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile t, HitBox obj){
			return obj.intersectsCircle(t.centerX(), t.centerY(), t.getWidth() * 0.5);
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class BottomSlab implements TileHitbox{
		@Override
		public CollisionResponse collide(Tile t, HitBox obj){
			var h = t.getHeight() * 0.5;
			return obj.calculateRectCollision(t.getX(), t.getY() + h, t.getWidth(), h, t.getMaterial());
		}
		
		@Override
		public boolean intersectsTile(Tile t, HitBox obj){
			var h = t.getHeight() * 0.5;
			return obj.intersectsRect(t.getX(), t.getY() + h, t.getWidth(), h);
		}
	}
	
}
