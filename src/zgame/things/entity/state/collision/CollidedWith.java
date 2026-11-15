package zgame.things.entity.state.collision;

import zgame.core.utils.Uuidable;
import zgame.physics.ZVector;
import zgame.things.type.bounds.Bounds;

/**
 * An object representing a thing that was collided with
 *
 * @param collided The object that was collided with
 * @param bounds The bounds of the thing collided with
 * @param id The object representing the id of what was collided with
 *
 * @param <T> The type of thing collided with
 * @param <V> The type of dimensions this thing collided with
 */
public record CollidedWith<T, V extends ZVector<V>>(T collided, Bounds<V> bounds, Uuidable id){
	
	/**
	 * An object where only a generic bounds was interacted with
	 * @param bounds The bounds collided
	 */
	public CollidedWith(Bounds<V> bounds){
		this(null, bounds, null);
	}
	
}
