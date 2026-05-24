package zgame.things.entity.projectile;

import zgame.core.GameTickable;
import zgame.core.utils.FunctionMap;
import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.BaseTags;
import zgame.things.entity.EntityThing;
import zgame.things.type.bounds.HitBox;

import java.util.function.Consumer;

/**
 * An interface for abstracting common functionality for projectile entities
 *
 * @param <V> The type of vector used by the projectile
 */
public interface Projectile<V extends ZVector<V>> extends GameTickable, HitBox<V>{
	
	/**
	 * Called to check this projectile's collision with the given entity
	 *
	 * @param entity The entity being potentially collided with
	 * @param dt The amount of time passed in a tick
	 */
	default void checkEntityCollision(EntityThing<V> entity, double dt){
		// Ignore the current thing if the projectile will not hit it, or if the entity should not collide with projectiles
		if(!this.willHit(entity) || entity.hasTag(BaseTags.PROJECTILE_NOT_COLLIDE)) return;
		this.hit(entity);
		if(this.isOnHit()) this.removeNext();
	}
	
	/**
	 * @return A non-null mapping of the functions to call when certain object types are hit. This essentially replaces manually defining abstract functions in this class,
	 * 		allowing a specific function to be called for a specific type
	 */
	FunctionMap getMappedFuncs();
	
	/**
	 * Add a new function for {@link #getMappedFuncs()}
	 *
	 * @param clazz The class of the type of the object accepted by the function
	 * @param func The function
	 * @param <T> The type of clazz
	 */
	default <T> void addHitFunc(Class<T> clazz, Consumer<T> func){
		this.getMappedFuncs().addFunc(clazz, func);
	}
	
	/**
	 * Call a function from {@link #getMappedFuncs()}
	 * Does nothing if no function exists
	 *
	 * @param clazz The class of the type of the object accepted by the function
	 * @param thing The object to pass to the function
	 * @param <T> The type of clazz
	 */
	default <T> void hit(Class<T> clazz, T thing){
		this.getMappedFuncs().func(clazz, thing);
	}
	
	@Override
	default void touchFloor(Collision<V> result){
		if(this.isOnHit()) this.removeNext();
	}
	
	// Projectiles do nothing on leaving a floor by default
	@Override
	default void leaveFloor(){}
	
	@Override
	default void touchCeiling(Collision<V> result){
		if(this.isOnHit()) this.removeNext();
	}
	
	@Override
	default void touchWall(Collision<V> result){
		if(this.isOnHit()) this.removeNext();
	}
	
	// Projectiles do nothing on leaving a wall by default
	@Override
	default void leaveWall(){}
	
	@Override
	default void collide(Collision<V> result){
		// OnHit projectiles are removed on collision
		if(this.isOnHit() && result.isCollided()) this.removeNext();
	}
	
	/**
	 * Called when this {@link Projectile} hits the given {@link HitBox}
	 *
	 * @param thing The {@link HitBox} which was hit
	 */
	void hit(HitBox<V> thing);
	
	/**
	 * Determine if this {@link Projectile} will hit the given {@link HitBox} thing when their hitboxes intersect
	 *
	 * @param thing The hitbox to check
	 * @return true thing will hit this, false otherwise
	 */
	default boolean willHit(HitBox<V> thing){
		// A projectile should not hit itself
		if(this == thing) return false;
		
		// If thing is the source of this projectile, then it will not hit
		var sourceId = this.getSourceId();
		if(sourceId != null && sourceId.equals(thing.getUuid())) return false;
		
		// Otherwise it will hit as long as they intersect
		return this.intersects(thing);
	}
	
	/** @return true if this projectile should be deleted when it hits something, false otherwise */
	boolean isOnHit();
	
	/** @return The total distance that this projectile has travelled */
	double getTotalDistance();
	
	/** @param distance Set the value returned by {@link #getTotalDistance()} */
	void setTotalDistance(double distance);
	
	/** @return The maximum range this projectile can travel before being deleted, or negative to never be deleted by range */
	double getRange();
	
	/** @return The uuid of a thing that created this projectile, or null if nothing created it. If the creator of this thing touches this, then this will not hit its creator */
	String getSourceId();
	
	/** Tell this projectile to be removed on the next tick */
	void removeNext();
	
	/** @return true if this thing should be removed on the next tick, false otherwise. Should return true any time {@link #removeNext()} has been called */
	boolean willRemove();
	
	@Override
	default void tick(double dt){
		if(this.willRemove()) this.getEntity().removeFrom();
		
		var r = this.getRange();
		if(r >= 0 && this.getTotalDistance() >= r) this.removeNext();
		
		this.setTotalDistance(this.getTotalDistance() + this.getEntity().getVelocity().getMagnitude() * dt);
	}
	
	/** @return The entity thing which is this projectile, all projectiles should be an entity. Must never return null, should always return this */
	EntityThing<V> getEntity();
	
}
