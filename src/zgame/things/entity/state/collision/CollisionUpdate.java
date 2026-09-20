package zgame.things.entity.state.collision;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.HitBox;

/** An update for an individual collision occurring */
public class CollisionUpdate<V extends ZVector<V>>{
	
	/** The collision which occurred */
	private final Collision<V> collision;
	
	/** The bounds that was collided into */
	private final Bounds<V> collidedWith;
	
	/** The material which was collided into */
	private final Material material;
	
	/** The object which collided with the other bounds */
	private final HitBox<V> collider;
	
	// TODO update docs
	/**
	 * @param collision See {@link #collision}
	 * @param material See {@link #material}
	 */
	// TODO probably avoid making this such a mess of so many objects
	public CollisionUpdate(Collision<V> collision, HitBox<V> collider, Bounds<V> collidedWith, Material material){
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
	
	public V computeNewPos(V updatedPosition){
		// TODO does this make sense?
		var c = collider.collideAt(updatedPosition, this.collidedWith);
		return c.originalPos().add(c.changePos());
	}
	
}
