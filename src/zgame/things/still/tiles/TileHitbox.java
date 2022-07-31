package zgame.things.still.tiles;

import zgame.physics.collision.CollisionResponse;
import zgame.things.type.HitBox;

/** An enum that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox{
	
	public static final None NONE = new None();
	public static final Full FULL = new Full();

	/**
	 * Based on the given rectangular bounds, determine the new position of the rectangle when it collides with the given tile
	 * The coordinates in this method are treated as the upper left hand corner of the rectangle
	 * 
	 * @param t The {@link Tile} to collide
	 * @param obj The object with a hitbox which collides with the given {@link Tile}
	 * @return A point to reposition the rectangle to
	 */
	public CollisionResponse collide(Tile t, HitBox obj);

	/** For tiles with no collision */
	public static class None implements TileHitbox{
		@Override
		public CollisionResponse collide(Tile t, HitBox obj){
			return new CollisionResponse();
		}
	}

	/** For tiles whose hitbox takes up the entire tile */
	public static class Full implements TileHitbox{
		@Override
		public CollisionResponse collide(Tile t, HitBox obj){
			return obj.calculateRectCollision(t.getX(), t.getY(), t.getWidth(), t.getHeight(), t.getType().getMaterial());
		}
	}

}
