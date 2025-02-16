package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.utils.FunctionMap;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResult;
import zgame.things.BaseTags;
import zgame.things.entity.EntityThing;
import zgame.things.type.bounds.HitBox;
import zgame.world.Room;

import java.util.function.Consumer;

/**
 * An interface for abstracting common functionality for projectile entities
 *
 * @param <H> The type of hitbox of the projectile
 * @param <E> The type of entity of the projectile
 * @param <V> The type of vector used by the projectile
 * @param <R> The type of room which the projectile can exist in
 * @param <C> The type of collision results used by the projectile
 */
public interface Projectile<H extends HitBox<H, C>,
		E extends EntityThing<H, E, V, R, C>,
		V extends ZVector<V>,
		R extends Room<H, E, V, R, C>,
		C extends CollisionResult<C>
		> extends GameTickable, HitBox<H, C>{
	
	/**
	 * Called to check this projectile's collision with the given entity
	 * @param game The game where the collision took place
	 * @param entity The entity being potentially collided with
	 * @param dt The amount of time passed in a tick
	 */
	default void checkEntityCollision(Game game, E entity, double dt){
		// Ignore the current thing if the projectile will not hit it, or if the entity should not collide with projectiles
		if(!this.willHit(entity.get()) || entity.hasTag(BaseTags.PROJECTILE_NOT_COLLIDE)) return;
		this.hit(game, entity.get());
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
	default void touchFloor(C collision){
		if(this.isOnHit()) this.removeNext();
	}
	
	// Projectiles do nothing on leaving a floor by default
	@Override
	default void leaveFloor(){}
	
	@Override
	default void touchCeiling(C collision){
		if(this.isOnHit()) this.removeNext();
	}
	
	@Override
	default void touchWall(C result){
		if(this.isOnHit()) this.removeNext();
	}
	
	// Projectiles do nothing on leaving a wall by default
	@Override
	default void leaveWall(){}
	
	@Override
	default void collide(C r){
		// OnHit projectiles are removed on collision
		if(this.isOnHit() && r.isCollided()) this.removeNext();
	}
	
	/**
	 * Called when this {@link Projectile2D} hits the given {@link HitBox}
	 *
	 * @param game The game where thing was hit
	 * @param thing The {@link HitBox} which was hit
	 */
	void hit(Game game, H thing);
	
	/**
	 * Determine if this {@link Projectile2D} will hit the given {@link HitBox} thing when their hitboxes intersect
	 *
	 * @param thing The hitbox to check
	 * @return true thing will hit this, false otherwise
	 */
	default boolean willHit(H thing){
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
	default void tick(Game game, double dt){
		if(this.willRemove()) this.getEntity().removeFrom(game);
		
		var r = this.getRange();
		if(r >= 0 && this.getTotalDistance() >= r) this.removeNext();
		
		this.setTotalDistance(this.getTotalDistance() + this.getEntity().getVelocity().getMagnitude() * dt);
	}
	
	/** @return The entity thing which is this projectile, all projectiles should be an entity. Must never return null, should always return this */
	E getEntity();
	
}
