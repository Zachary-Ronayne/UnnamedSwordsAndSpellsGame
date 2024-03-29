package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.core.utils.FunctionMap;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.BaseTags;
import zgame.things.entity.EntityThing;
import zgame.things.type.HitBox;

import java.util.function.Consumer;

/** An {@link EntityThing} which represents a thing flying through the air */
public abstract class Projectile extends EntityThing{
	
	/**
	 * A mapping of the functions to call when certain object types are hit. This essentially replaces manually defining abstract functions in this class, allowing a specific
	 * function to be called for a specific type
	 */
	private final FunctionMap mappedFuncs;
	
	/** The amount of distance this projectile can travel before being deleted, negative value to never be deleted */
	private double range;
	
	/** The total distance this projectile has traveled since creation */
	private double totalDistance;
	
	/** true if the projectile should be removed in the next tick, false otherwise */
	private boolean willRemove;
	
	/** true if this is a {@link Projectile} which destroys itself when it hits anything, false otherwise */
	private boolean onHit;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public Projectile(double x, double y, ZVector launchVelocity){
		super(x, y);
		this.addVelocity(launchVelocity);
		this.mappedFuncs = new FunctionMap();
		this.range = -1;
		this.totalDistance = 0;
		this.willRemove = false;
		this.onHit = false;
	}
	
	/**
	 * Add a new function for {@link #mappedFuncs}
	 * @param clazz The class of the type of the object accepted by the function
	 * @param func The function
	 * @param <T> The type of clazz
	 */
	public <T> void addHitFunc(Class<T> clazz, Consumer<T> func){
		this.mappedFuncs.addFunc(clazz, func);
	}
	
	/**
	 * Call a function from {@link #mappedFuncs}.
	 * Does nothing if no function exists
	 *
	 * @param clazz The class of the type of the object accepted by the function
	 * @param thing The object to pass to the function
	 * @param <T> The type of clazz
	 */
	public <T> void hit(Class<T> clazz, T thing){
		this.mappedFuncs.func(clazz, thing);
	}
	
	@Override
	public void touchFloor(Material touched){
		if(this.isOnHit()) this.removeNext();
	}
	
	@Override
	public void touchCeiling(Material touched){
		if(this.isOnHit()) this.removeNext();
	}
	
	@Override
	public void touchWall(Material touched){
		if(this.isOnHit()) this.removeNext();
	}
	
	@Override
	public void collide(CollisionResponse r){
		// OnHit projectiles are removed on collision
		if(this.isOnHit() && r.isCollided()) this.removeNext();
	}
	
	@Override
	public void checkEntityCollision(Game game, EntityThing entity, double dt){
		super.checkEntityCollision(game, entity, dt);
		// Ignore the current thing if the projectile will not hit it, or if the entity should not collide with projectiles
		if(!this.willHit(entity) || entity.hasTag(BaseTags.PROJECTILE_NOT_COLLIDE)) return;
		this.hit(game, entity);
		if(this.isOnHit()) this.removeNext();
	}
	
	/**
	 * Called when this {@link Projectile} hits the given {@link HitBox}
	 *
	 * @param game The game where thing was hit
	 * @param thing The {@link HitBox} which was hit
	 */
	public abstract void hit(Game game, HitBox thing);
	
	/**
	 * Determine if this {@link Projectile} will hit the given {@link HitBox} thing when their hitboxes intersect
	 *
	 * @param thing The hitbox to check
	 * @return true thing will hit this, false otherwise
	 */
	public boolean willHit(HitBox thing){
		return this != thing && this.intersects(thing);
	}
	
	/** @return See {@link #onHit} */
	public boolean isOnHit(){
		return this.onHit;
	}
	
	/** @param onHit See {@link #onHit} */
	public void setOnHit(boolean onHit){
		this.onHit = onHit;
	}
	
	/** @return See {@link #totalDistance} */
	public double getTotalDistance(){
		return this.totalDistance;
	}
	
	/** @return See {@link #range} */
	public double getRange(){
		return this.range;
	}
	
	/** @param range See {@link #range} */
	public void setRange(double range){
		this.range = range;
	}
	
	/** Tell this projectile to be removed on the next tick */
	public void removeNext(){
		this.willRemove = true;
	}
	
	@Override
	public void tick(Game game, double dt){
		if(this.willRemove) this.removeFrom(game);
		
		var r = this.getRange();
		if(r >= 0 && this.totalDistance >= r) this.removeNext();
		
		super.tick(game, dt);
		this.totalDistance += this.getVelocity().getMagnitude() * dt;
	}
}
