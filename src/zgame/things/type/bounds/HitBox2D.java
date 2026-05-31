package zgame.things.type.bounds;

import zgame.physics.V2D;
import zgame.physics.collision.Collision2D;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile2D;

/**
 * An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds
 */
public interface HitBox2D extends HitBox<V2D>, Bounds2D{
	
	@Override
	default double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	default double maxY(){
		return this.getY() + this.getHeight();
	}
	
	/**
	 * Determine a {@link Collision2D} from colliding this object with the given rectangular bounds. Essentially, move this object so that it no longer intersecting with
	 * the given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 *
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param m The material which was collided with
	 * @return The information about the collision
	 */
	Collision2D calculateRectCollision(double x, double y, double w, double h, Material m);
	
	/**
	 * Determine a {@link Collision2D} from colliding this object with the given circular bounds. Essentially, move this object so that it no longer intersecting with
	 * the given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 *
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param r The radius of the circle
	 * @param m The material which was collided with
	 * @return The information about the collision
	 */
	Collision2D calculateCircleCollision(double x, double y, double r, Material m);
	
	/**
	 * @param x The upper left hand x rectangle
	 * @param y The upper left hand y rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return true if this object intersects the given rectangular bounds, false otherwise
	 */
	boolean intersectsRect(double x, double y, double w, double h);
	
	/**
	 * @param x The center x coordinate of the circle
	 * @param y The center y coordinate of the circle
	 * @param r The radius of the circle
	 * @return true if this object intersects the given circle bounds, false otherwise
	 */
	boolean intersectsCircle(double x, double y, double r);
	
	/**
	 * @param hitbox The hitbox to check
	 * @return true if this hitbox intersects the given hitbox, false otherwise
	 */
	@Override
	default boolean intersects(HitBox<V2D> hitbox){
		
		var dims = hitbox.getDimensions();
		double w = dims.getWidth();
		double h = dims.getHeight();
		
		return switch(hitbox.getHitboxType()){
			case CIRCLE -> {
				var pos = hitbox.getCenterPosition();
				double x = pos.getX();
				double y = pos.getY();
				yield this.intersectsCircle(x, y, w * 0.5);
			}
			case RECT -> {
				var pos = hitbox.getMinPosition();
				double x = pos.getX();
				double y = pos.getY();
				yield this.intersectsRect(x, y, w, h);
			}
			default -> false;
		};
	}
	
	/**
	 * Called when this {@link HitBox2D} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this {@link HitBox2D}
	 */
	default void hitBy(Projectile2D p){}
	
	/**
	 * Provide a coordinate to reposition this object so that it is to the left of the given x coordinate.
	 * If the object is already to the left of the coordinate, this method returns {@link #getX()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being to the left of the given
	 * coordinate
	 *
	 * @param x The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepLeft(double x){
		if(this.getX() + this.getWidth() <= x) return this.getX();
		return x - this.getWidth();
	}
	
	/**
	 * Provide a coordinate to reposition this object so that it is to the right of the given x coordinate.
	 * If the object is already to the right of the coordinate, this method returns {@link #getX()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being to the right of the given
	 * coordinate
	 *
	 * @param x The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepRight(double x){
		return Math.max(this.getX(), x);
	}
	
	/**
	 * Provide a coordinate to reposition this object so that it is above the given y coordinate.
	 * If the object is already above the coordinate, this method returns {@link #getY()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being above of the given
	 * coordinate
	 *
	 * @param y The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepAbove(double y){
		if(this.getY() + this.getHeight() <= y) return this.getY();
		return y - this.getHeight();
	}
	
	/**
	 * Provide a coordinate to reposition this object so that it is below the given y coordinate.
	 * If the object is already below the coordinate, this method returns {@link #getY()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being below of the given
	 * coordinate
	 *
	 * @param y The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepBelow(double y){
		return Math.max(this.getY(), y);
	}
	
}
