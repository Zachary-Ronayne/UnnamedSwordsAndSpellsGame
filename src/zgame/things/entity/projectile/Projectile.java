package zgame.things.entity.projectile;

import zgame.core.utils.FunctionMap;
import zgame.core.utils.Uuidable;
import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.BaseTags;
import zgame.things.core.EntityThing;
import zgame.things.entity.state.collision.CollidedWith;
import zgame.things.entity.state.collision.CollisionUpdate;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.ShapedBounds;

import java.util.List;
import java.util.function.Consumer;

/**
 * A class for holding common functionality for projectile entities
 *
 * @param <V> The type of vector used by the projectile
 */
public abstract class Projectile<V extends ZVector<V>> implements Bounds<V>{
	
	/** The entity representing this projectile */
	private final EntityThing<V> entity;
	/** The object with an id that produced this projectile */
	private final Uuidable source;
	
	/**
	 * A mapping of the functions to call when certain object types are hit. This essentially replaces manually defining abstract functions in this class, allowing a specific
	 * function to be called for a specific type
	 */
	private final FunctionMap mappedFuncs;
	
	// TODO move these be moved to a state object
	/** The amount of distance this projectile can travel before being deleted, negative value to never be deleted */
	private double range;
	/** true if this is a {@link Projectile2D} which destroys itself when it hits anything, false otherwise */
	private boolean onHit;
	
	/** The total distance this projectile has traveled since creation */
	private double totalDistance;
	/** true if the projectile should be removed in the next tick, false otherwise */
	private boolean willRemove;
	
	/**
	 * @param entity See {@link #entity}
	 * @param source See {@link #source}
	 */
	public Projectile(EntityThing<V> entity, Uuidable source){
		this.entity = entity;
		this.source = source;
		
		this.mappedFuncs = new FunctionMap();
		
		this.range = -1;
		this.onHit = false;
		
		this.totalDistance = 0;
		this.willRemove = false;
	}
	
	// Methods to be called as needed by the entity
	
	/**
	 * Called when a projectile collides
	 * @param c A list of collisions that occurred
	 */
	public void collided(List<CollisionUpdate<V>> c){
		for(var update : c){
			// Skip if there was no collision
			if(!update.collision().isCollided()) return;
			
			// TODO does this need to interact with the state system
			this.hit(update.getCollidedWith());
		}
	}
	
	/**
	 * Check if the given entity collides with this projectile. Calls {@link #hit(CollidedWith)} if the entity was hit
	 * @param entity The entity to potentially hit
	 */
	public <T extends EntityThing<V>> void checkHitEntity(T entity){
		if(entity.intersectsDimensions(this.entity)){
			// TODO test that collisions actually occur
			var collidedWith = new CollidedWith<>(entity, entity, entity);
			this.hit(collidedWith);
		}
	}
	
	// TODO could entity things have a list of GameTickable objects that are automatically run instead of having projectiles have to manually call these?
	public void tick(double dt){
		// TODO probably move this to the state update system
		// If needing to be removed, remove it
		if(this.willRemove()){
			this.entity.removeFrom();
			return;
		}
		
		// If moving past the maximum range, it needs to be removed
		var r = this.getRange();
		if(r >= 0 && this.getTotalDistance() >= r) this.removeNext();
		
		// Update the distance this projectile has moved
		this.setTotalDistance(this.getTotalDistance() + this.entity.getVelocity().getMagnitude() * dt);
	}
	
	// Misc projectile logic
	
	/**
	 * Call an object is hit
	 * Does nothing if no function exists for that object's type
	 *
	 * @param collidedWith The thing that got hit
	 * @param <T> The type of clazz
	 */
	// TODO can clazz be removed as a parameter? Or maybe don't use classes as a map key
	@SuppressWarnings("unchecked")
	public <T> void hit(CollidedWith<T, V> collidedWith){
		// A hit occurred
		if(this.isOnHit()) this.removeNext();
		
		// Ignore the current thing if the projectile will not hit it, or if the thing should not collide with projectiles
		if(!this.willHit(collidedWith.bounds(), collidedWith.id()) || collidedWith.id().hasTag(BaseTags.PROJECTILE_NOT_COLLIDE)) return;
		
		// Call the function
		// TODO test if this is a valid cast, add assumptions made here to the docs
		// TODO test if collided can just be the bounds object and avoid the extra variable
		this.getMappedFuncs().func((Class<T>)(collidedWith.collided().getClass()), collidedWith.collided());
		
	}
	
	/**
	 * Determine if this {@link Projectile} will hit the given {@link Bounds} when they intersect
	 *
	 * @param bounds The bounds to check
	 * @param source The object identifying the bounds that this projectile is about to hit
	 * @return true thing will hit this, false otherwise
	 */
	public boolean willHit(Bounds<V> bounds, Uuidable source){
		// A projectile should not hit itself
		if(this.entity.matches(source)) return false;
		
		// If thing is the source of this projectile, then it will not hit
		if(this.source.matches(source)) return false;
		
		// Otherwise it will hit as long as they intersect
		return this.intersectsDimensions(bounds);
	}
	
	
	// Getters and setters
	
	/** @return See {@link #mappedFuncs} */
	private FunctionMap getMappedFuncs(){
		return this.mappedFuncs;
	}
	
	/**
	 * Add a new function for {@link #getMappedFuncs()}
	 *
	 * @param clazz The class of the type of the object accepted by the function
	 * @param func The function
	 * @param <T> The type of clazz
	 */
	public <T> void addHitFunc(Class<T> clazz, Consumer<T> func){
		this.getMappedFuncs().addFunc(clazz, func);
	}
	
	/** @return See {@link #onHit} */
	public boolean isOnHit(){
		return this.onHit;
	}
	
	/** @param onHit See {@link #onHit} */
	public void setOnHit(boolean onHit){
		this.onHit = onHit;
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
	
	/** @return See {@link #willRemove} */
	public boolean willRemove(){
		return this.willRemove;
	}
	
	/** @return See {@link #totalDistance} */
	public double getTotalDistance(){
		return this.totalDistance;
	}
	
	/** @param totalDistance See {@link #totalDistance} */
	private void setTotalDistance(double totalDistance){
		this.totalDistance = totalDistance;
	}
	
	
	// Bounds implementations to pull from the entity's bounds
	
	// TODO does it make sense to have projectile have to implement all of these just from the entity?
	@Override
	public V getMinPosition(){
		return this.entity.getMinPosition();
	}
	
	@Override
	public V getMaxPosition(){
		return this.entity.getMaxPosition();
	}
	
	@Override
	public V getCenterPosition(){
		return this.entity.getCenterPosition();
	}
	
	@Override
	public boolean intersectsDimensions(Bounds<V> bounds){
		return this.entity.intersectsDimensions(bounds);
	}
	
	@Override
	public Collision<V> collideAtDimensions(V currentMinPos, Bounds<V> bounds){
		return this.entity.collideAtDimensions(currentMinPos, bounds);
	}
	
	@Override
	public ShapedBounds<V> asShapedBoundsDimension(){
		return this.entity.asShapedBoundsDimension();
	}
	
	@Override
	public double getGravityDragReferenceArea(){
		return this.entity.getGravityDragReferenceArea();
	}
	
	@Override
	public V getDimensions(){
		return this.entity.getDimensions();
	}
	
	@Override
	public ShapedBounds<V> atMinPositionDimension(V minPos){
		return this.entity.atMinPositionDimension(minPos);
	}
}
