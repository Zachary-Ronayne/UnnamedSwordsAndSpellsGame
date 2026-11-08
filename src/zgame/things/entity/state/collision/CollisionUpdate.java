package zgame.things.entity.state.collision;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.bounds.Bounds;

/** An update for an individual collision occurring */
public class CollisionUpdate<V extends ZVector<V>>{
	
	/** The collision which occurred */
	private final Collision<V> collision;
	
	// TODO maybe consolidate these objects
	/** The bounds that was collided into */
	private final CollidedWith<?, V> collidedWith;
	
	/** The material which was collided into */
	private final Material material;
	
	/** The object which collided with the other bounds */
	private final Bounds<V> collider;
	
	// TODO update docs
	/**
	 * @param collision See {@link #collision}
	 * @param material See {@link #material}
	 */
	public CollisionUpdate(Collision<V> collision, Bounds<V> collider, CollidedWith<?, V> collidedWith, Material material){
		this.collision = collision;
		this.collider = collider;
		this.collidedWith = collidedWith;
		// Set the material to no material if none is given
		this.material = material == null ? Materials.NONE : material;
	}
	
	public Collision<V> collision(){
		return collision;
	}
	
	public Material material(){
		return material;
	}
	
	/**
	 * Compute the new minimum position that {@link #collider} should be at after this collision is applied to the given position
	 * @param updatedMinPosition The position that {@link #collider} should be considered at
	 * @return The new min position for {@link #collider}
	 */
	public V computeNewMinPos(V updatedMinPosition){
		var c = collider.collideAtDimensions(updatedMinPosition, this.collidedWith.bounds());
		return c.originalPos().add(c.changePos());
	}
	
	/** @return See {@link #collidedWith} */
	public CollidedWith<?, V> getCollidedWith(){
		return this.collidedWith;
	}
}
