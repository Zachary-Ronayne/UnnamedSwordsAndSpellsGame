package zgame.things.still.tiles.twoDee;

import zgame.physics.V2D;
import zgame.physics.collision.Collision2D;
import zgame.things.still.tiles.Tile;
import zgame.things.still.tiles.TileHitbox;
import zgame.things.type.bounds.HitBox;

/** An object that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox2D extends TileHitbox<V2D>{
	
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
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			return new Collision2D();
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox2D{
		@Override
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			return obj.calculateRectCollision(t.getX(), t.getY(), t.getWidth(), t.getHeight(), t.getMaterial());
		}
	}
	
	/** For tiles whose hitbox is a circle inscribed by the tile */
	class Circle implements TileHitbox2D{
		@Override
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			return obj.calculateCircleCollision(t.centerX(), t.centerY(), t.getWidth() * 0.5, t.getMaterial());
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class BottomSlab implements TileHitbox2D{
		@Override
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			var h = t.getHeight() * 0.5;
			return obj.calculateRectCollision(t.getX(), t.getY() + h, t.getWidth(), h, t.getMaterial());
		}
	}
	
}
