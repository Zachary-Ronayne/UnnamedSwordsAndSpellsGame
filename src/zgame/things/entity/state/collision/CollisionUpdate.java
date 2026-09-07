package zgame.things.entity.state.collision;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.bounds.HitBox;

import java.util.function.Function;

/** An update for an individual collision occurring */
public class CollisionUpdate<V extends ZVector<V>>{
	
	/** The collision which occurred */
	private final Collision<V> collision;
	
	/** The material which was collided into */
	private final Material material;
	
	/** A function that, given a hitbox to collide with, computes the new position it should move to */
	private final Function<HitBox<V>, V> computeNewPosition;
	
	// TODO update docs
	/**
	 * @param position The existing position of the object that collided
	 * @param collision See {@link #collision}
	 * @param material See {@link #material}
	 */
	public CollisionUpdate(V position, Collision<V> collision, Material material, Function<HitBox<V>, V> computeNewPosition){
		this.collision = collision;
		// Set the material to no material if none is given
		this.material = material == null ? Materials.NONE : material;
		this.computeNewPosition = computeNewPosition;
	}
	
	public Collision<V> getCollision(){
		return collision;
	}
	
	// TODO make this hold the lambda function and compute the initial displacement
	
}
