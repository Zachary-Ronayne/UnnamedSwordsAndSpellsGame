package zgame.things.type.bounds;

import zgame.core.utils.Uuidable;
import zgame.physics.ZVector;
import zgame.things.type.Materialable;

/**
 * An interface which defines an object that has a hit box, meaning something with a position that other objects can collide with
 * @param <V> The type of dimension this hitbox interacts with
 */
// TODO does hitbox still need a material and uuid?
public interface HitBox<V extends ZVector<V>> extends Materialable, Uuidable, Bounds<V> {
	
	/** @return The type of this hitbox, for determining how it will collide with other hitboxes */
	HitboxType getHitboxType();
	
	/**
	 * @param h The hitbox to check
	 * @return true if this hitbox intersects the given hitbox, false otherwise
	 */
	boolean intersects(HitBox<V> h);
	
	// TODO does this value need to be defined as a part of entity state?
	/** @return The surface area of this hitbox as it moves down */
	double getGravityDragReferenceArea();
	
	/**
	 * Helper method for converting this hitbox to the correct type, implement as returning this cast to the correct type
	 * @param clazz The type of hitbox to convert to
	 * @return This, but as the correct type
	 * @param <H> The type to convert to
	 */
	<H extends HitBox<V>> H asHitbox(Class<H> clazz);
	
}
