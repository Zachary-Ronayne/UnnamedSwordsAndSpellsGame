package zgame.things.type.bounds;

import zgame.core.utils.Uuidable;
import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.type.Materialable;
import zgame.things.type.Position;

/**
 * An interface which defines an object that has a hit box, meaning something with a position that other objects can collide with
 * @param <V> The type of dimension this hitbox interacts with
 */
// TODO does hitbox still need a material and uuid?
// TODO should hitbox even be considered a bounds?
public interface HitBox<V extends ZVector<V>> extends Materialable, Uuidable, Bounds<V> {
	
	/** @return The type of this hitbox, for determining how it will collide with other hitboxes */
	HitboxType getHitboxType();
	
	/**
	 * @param bounds A bounds to collide this hitbox into
	 * @return The collision representing how this hitbox will need to be moved to no longer be colliding with the given bounds
	 */
	default Collision<V> collideBounds(Bounds<V> bounds){
		return collideAt(this.getPosition(), bounds);
	}
	
	/**
	 * @param currentPos The position that this hitbox should be considered in for this computation
	 * @param bounds The bounds that does not move during this collision
	 * @return The collision representing how the moving bounds will need to be moved to no longer be colliding with the unmoving bounds
	 */
	Collision<V> collideAt(V currentPos, Bounds<V> bounds);
	
	// TODO does this value need to be defined as a part of entity state?
	/** @return The surface area of this hitbox as it moves down */
	double getGravityDragReferenceArea();
	
	// TODO figure out if this is actually necessary
	/**
	 * Helper method for converting this hitbox to the correct type, implement as returning this cast to the correct type
	 * @param clazz The type of hitbox to convert to
	 * @return This, but as the correct type
	 * @param <H> The type to convert to
	 */
	<H extends HitBox<V>> H asHitbox(Class<H> clazz);
	
}
