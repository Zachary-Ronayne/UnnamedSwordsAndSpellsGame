package zgame.things.tiles;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;

/** An enum that represents the hitbox of a tile, i.e., what parts of the tile have collision */
public interface TileHitbox{
	
	public static final None NONE = new None();
	public static final Full FULL = new Full();

	/**
	 * Based on the given rectangular bounds, determine the new position of the rectangle when it collides with the given tile
	 * The coordinates in this method are treated as the upper left hand corner of the rectangle
	 * 
	 * @param t The tile to collide
	 * @param x The x coordinate of the bounds
	 * @param y The y coordinate of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param px The x coordinate of the location of the bounds in the previous instance of time
	 * @param py The y coordinate of the location of the bounds in the previous instance of time
	 * @return A point to reposition the rectangle to
	 */
	public CollisionResponse collideRect(Tile t, double x, double y, double w, double h, double px, double py);

	/** For tiles with no collision */
	public static class None implements TileHitbox{
		@Override
		public CollisionResponse collideRect(Tile t, double x, double y, double w, double h, double px, double py){
			return new CollisionResponse();
		}
	}

	/** For tiles whose hitbox takes up the entire tile */
	public static class Full implements TileHitbox{
		@Override
		public CollisionResponse collideRect(Tile t, double x, double y, double w, double h, double px, double py){
			// CollisionResponse r = ZCollision.rectToRectBasic(t.getX(), t.getY(), t.getWidth(), t.getHeight(), x, y, w, h);
			CollisionResponse r = ZCollision.rectToRect(t.getX(), t.getY(), t.getWidth(), t.getHeight(), x, y, w, h, px, py);
			return r;
		}
	}

}
