package zgame.things.still.tiles.twoDee;

import zgame.physics.V2D;
import zgame.physics.collision.Collision2D;
import zgame.things.still.tiles.Tile;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox2D;

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
			return new Collision2D(t.getPosition());
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class Full implements TileHitbox2D{
		@Override
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			var pos = t.getPosition();
			var dims = t.getDimensions();
			return obj.asHitbox(HitBox2D.class).calculateRectCollision(pos.getX(), pos.getY(), dims.getWidth(), dims.getHeight());
		}
	}
	
	/** For tiles whose hitbox is a circle inscribed by the tile */
	class Circle implements TileHitbox2D{
		@Override
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			var center = t.getCenterPosition();
			return obj.asHitbox(HitBox2D.class).calculateCircleCollision(center.getX(), center.getY(), t.getDimensions().getWidth() * 0.5);
		}
	}
	
	/** For tiles whose hitbox takes up the entire tile */
	class BottomSlab implements TileHitbox2D{
		@Override
		public Collision2D collide(Tile<V2D> t, HitBox<V2D> obj){
			var dims = t.getDimensions();
			var h = dims.getHeight() * 0.5;
			var pos = t.getPosition();
			return obj.asHitbox(HitBox2D.class).calculateRectCollision(pos.getX(), pos.getY() + h, dims.getWidth(), h);
		}
	}
	
}
