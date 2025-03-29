package zgame.world;

import java.util.ArrayList;
import java.util.List;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ClassMappedList;
import zgame.core.utils.NotNullList;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResult;
import zgame.things.entity.EntityThing;
import zgame.things.still.Door;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.HitBox;

/**
 * An object which represents a location in a game, i.e. something that holds the player, NPCs, the tiles, etc.
 *
 * @param <H> The hitbox implementation used by entities in the room
 * @param <E> The type of entities in this room
 * @param <V> The vectors used by entities in this room
 * @param <R> The room implementation
 * @param <C> The type of collisions which occur in this room
 */
// issue#50 find a way to avoid having to do this comical amount of type parameters without having to resort to weird type casting or instanceof checks
public abstract class Room<
		H extends HitBox<H, C>,
		E extends EntityThing<H, E, V, R, C>,
		V extends ZVector<V>,
		R extends Room<H, E, V, R, C>,
		C extends CollisionResult<C>
		> extends GameThing{
	
	/** All of the things in this room */
	private final ClassMappedList thingsMap;
	
	/** All of the {@link GameThing} objects which will be removed on the next game tick */
	private final List<GameThing> thingsToRemove;
	
	/** A list of things to do the next time this room is ticked. Once the tick happens, this list will be emptied */
	private final List<Runnable> nextTickFuncs;
	
	/**
	 * Create a new empty {@link Room}
	 */
	public Room(){
		this.thingsMap = new ClassMappedList();
		this.thingsMap.addClass(GameThing.class);
		this.thingsMap.addClass(this.getHitBoxType());
		this.thingsMap.addClass(GameTickable.class);
		this.thingsMap.addClass(this.getEntityClass());
		
		this.thingsToRemove = new ArrayList<>();
		this.nextTickFuncs = new ArrayList<>();
	}
	
	@Override
	public void destroy(){
		var things = this.getThings();
		for(int i = 0; i < things.size(); i++) things.get(i).destroy();
	}
	
	/** @return See {@link #thingsMap} */
	public ClassMappedList getAllThings(){
		return this.thingsMap;
	}
	
	/** @return A list of all the things in this room. This is the actual collection holding the things, not a copy. Do not directly update the state of this collection */
	public NotNullList<GameThing> getThings(){
		return this.thingsMap.get(GameThing.class);
	}
	
	/** @return A list of all the entities in this room. This is the actual collection holding the things, not a copy. Do not directly update the state of this collection */
	public NotNullList<E> getEntities(){
		return this.thingsMap.get(this.getEntityClass());
	}
	
	/**
	 * @param uuid The uuid of the entity to get
	 * @return The entity, or null if no entity with that uuid exists in this room
	 */
	public E getEntity(String uuid){
		return this.thingsMap.getMap(this.getEntityClass()).get(uuid);
	}
	
	/** @return All the tickable things in this room. This is the actual collection holding the things, not a copy. Do not directly update the state of this collection */
	public NotNullList<GameTickable> getTickableThings(){
		return this.thingsMap.get(GameTickable.class);
	}
	
	/** @return All the hitbox things in this room. This is the actual collection holding the things, not a copy. Do not directly update the state of this collection */
	public NotNullList<H> getHitBoxThings(){
		return this.thingsMap.get(this.getHitBoxType());
	}
	
	/** @return The type of hitboxes used in this room should just return the class of H */
	public abstract Class<H> getHitBoxType();
	
	/** @return The type of entities used by this class */
	public abstract Class<E> getEntityClass();
	
	/**
	 * Add a {@link GameThing} to this {@link Room}
	 *
	 * @param thing The {@link GameThing} to add
	 */
	public void addThing(GameThing thing){
		this.thingsMap.add(thing);
	}
	
	/**
	 * Remove a {@link GameTickable} from this {@link Room} on the next tick
	 *
	 * @param thing The {@link GameTickable} to remove
	 */
	public final void removeThing(GameThing thing){
		this.thingsToRemove.add(thing);
	}
	
	/**
	 * Collide the given {@link EntityThing} with this room. Essentially, attempt to move the given object so that it no longer intersects with anything in this room.
	 *
	 * @param obj The object to collide
	 * @return The CollisionResponse representing the final collision that took place, where the collision material is the floor collision, if one took place
	 */
	public abstract C collide(H obj);
	
	/**
	 * Collide the given {@link EntityThing} with the entities in the given room
	 *
	 * @param game The game with the current room to collide with
	 * @param checkEntity The entity to check collision for
	 * @param dt The amount of time, in seconds, which passed in the tick where this collision took place
	 */
	public void checkEntityCollisions(Game game, E checkEntity, double dt){
		// issue#21 make this more efficient by reducing redundant checks, and not doing the same collision calculation for each pair of entities
		// Probably just rewrite the actual entity collision outside of on intersection, the commented out code is copied and modified from the EntityThing class, which was originally just for 2D
		
		// Check any stored entities, and remove them if they are not intersecting or are not in the room
//		ArrayList<String> toRemove = new ArrayList<>(checkEntity.collidingUuids.size());
//		for(String eUuid : checkEntity.collidingUuids){
//			var e = room.getEntity(eUuid);
//			if(e == null || !checkEntity,get().intersects(e.get())) toRemove.add(eUuid);
//		}
//		for(String uuid : toRemove){
//			checkEntity.collidingUuids.remove(uuid);
//
//			// Set the velocity so that it will move the entity an amount to cancel out the current force applied on the next tick, then remove the force on the next tick
//			var removed = checkEntity.removeForce(uuid);
//			if(removed != null) checkEntity.addVelocity(removed.scale(-dt / checkEntity.getMass()));
//		}
		// Get all entities
		var entities = this.getEntities();
		
		// Iterate through all entities, ignoring the given entity, and find the ones intersecting the given entity
		for(int i = 0; i < entities.size(); i++){
			var e = entities.get(i);
			if(e == checkEntity || !checkEntity.get().intersects(e.get())) continue;
			checkEntity.checkEntityCollision(game, e, dt);

//			// If they intersect, determine the force they should have against each other, and apply it to both entities
//			String eUuid = e.getUuid();
//			ZPoint checkEntityP = new ZPoint(checkEntity.centerX(), checkEntity.maxY());
//			ZPoint eP = new ZPoint(e.centerX(), e.maxY());
//			// Find the distance between the center bottom of the entities, to determine how much force should be applied
//			double dist = checkEntityP.distance(eP);
//
//			// Find a distance where, if the bottom centers of the entities are further than checkEntity distance, they are definitely not intersecting
//			double maxDist = (checkEntity.getWidth() + checkEntity.getHeight() + e.getWidth() + e.getHeight()) * .5;
//			// The maximum amount of force that can be applied
//			double maxMag = (checkEntity.getForce().getMagnitude() + e.getForce().getMagnitude());
//
//			// In the equation f(x) = mx^2 + b, so that f(x) = 0 is the maximum amount of force, and 0 = mx^2 + b is the maximum distance to use
//			double b = maxMag;
//			double m = b / (maxDist * maxDist);
//
//			// Use that equation to find the force
//			double mag = m * dist * dist + b;
//			double angle = ZMath.lineAngle(eP.getX(), eP.getY(), checkEntityP.getX(), checkEntityP.getY());
//
//			// Find the initial amount of force to set
//			ZVector newForce = new ZVector(angle, mag, false);
//
//			// Apply most of the force as the x component, and less as the y component
//			newForce = new ZVector(newForce.getX(), newForce.getY() * 0.1);
//
//			//issue#21
//
//			// Try keeping track of the total velocity an entity collision has added to another entity, and then remove that much velocity when the entities stop colliding
//
//			// If that amount of force would move the entity too far away, set it so that the entities will only be touching on the next tick
//			// double xForce = newForce.getX();
//			// double xMoved = xForce / checkEntity.getMass() * dt * dt;
//			// double xDiff;
//			// if(checkEntity.getX() < e.getX()) xDiff = Math.abs(checkEntity.getX() + checkEntity.getWidth() - e.getX());
//			// else xDiff = Math.abs(e.getX() + e.getWidth() - checkEntity.getX());
//			// if(ZMath.sameSign(xMoved, xDiff) && Math.abs(xMoved) > xDiff){
//			// 	double newMoved = xMoved < 0 ? -xDiff : xDiff;
//			// 	newForce = new ZVector(newMoved / (dt * dt) * checkEntity.getMass(), newForce.getY());
//			// }
//
//			double limit = 10000;
//			if(newForce.getX() > limit) newForce = new ZVector(limit, newForce.getY());
//			else if(newForce.getX() < -limit) newForce = new ZVector(-limit, newForce.getY());
//
//			// Apply the force to both entities, not just the given entity
//			checkEntity.setForce(eUuid, newForce);
//			checkEntity.collidingUuids.add(eUuid);
//			e.setForce(checkEntity.getUuid(), newForce.scale(-1));
//			e.collidingUuids.add(checkEntity.getUuid());
		}
	}
	
	/**
	 * Make something happen to this {@link GameThing} the next time it is ticked
	 *
	 * @param r The function to run
	 */
	public void onNextTick(Runnable r){
		this.nextTickFuncs.add(r);
	}
	
	/**
	 * Update this {@link Room}
	 *
	 * @param game The {@link Game} which this {@link Room} should update relative to
	 * @param dt The amount of time passed in this update
	 */
	public void tick(Game game, double dt){
		// Update all updatable objects
		var tickable = this.getTickableThings();
		for(int i = 0; i < tickable.size(); i++){
			GameTickable t = tickable.get(i);
			t.tick(game, dt);
		}
		
		// Update the position of all relevant objects
		var entities = this.getEntities();
		for(int i = 0; i < entities.size(); i++) entities.get(i).updatePosition(game, dt);
		
		// Check the collision of this room for entities
		for(int i = 0; i < entities.size(); i++){
			var e = entities.get(i);
			if(e.isNoClip()) continue;
			// Check for tile collisions
			this.collide(e.get());
			
			// Check for entity collision, and apply appropriate forces based on what is currently colliding
			this.checkEntityCollisions(game, e, dt);
		}
		
		// Remove all things that need to be removed
		for(GameThing thing : this.thingsToRemove) this.tickRemoveThing(game, thing);
		this.thingsToRemove.clear();
		
		// Run any functions which need to happen
		for(int i = 0; i < this.nextTickFuncs.size(); i++) this.nextTickFuncs.get(i).run();
		this.nextTickFuncs.clear();
	}
	
	/**
	 * Called each time a thing is removed via {@link #tick(Game, double)}, i.e. the thing was added to {@link #thingsToRemove}, and now it's being removed
	 *
	 * @param game The game where the removal took place
	 * @param thing The thing to remove
	 */
	private void tickRemoveThing(Game game, GameThing thing){
		this.thingsMap.remove(thing);
		thing.onRoomRemove(game);
	}
	
	/**
	 * Draw this {@link Room} to the given {@link Renderer}
	 *
	 * @param game The {@link Game} to draw this {@link Room} relative to
	 * @param r The {@link Renderer} to draw this {@link Room} on
	 */
	public void render(Game game, Renderer r){
		// Draw all the things
		var things = this.getThings();
		for(int i = 0; i < things.size(); i++) things.get(i).renderWithCheck(game, r);
	}
	
	/**
	 * Determine if the given {@link GameThing} is allowed to enter this {@link Room} using a {@link Door}
	 *
	 * @param thing The thing to check for
	 * @return true if it can enter, false otherwise. Always true by default, override to provide custom behavior
	 */
	public boolean canEnter(GameThing thing){
		return true;
	}
	
	/**
	 * Determine if the given {@link GameThing} is allowed to leave this {@link Room} using a {@link Door}
	 *
	 * @param thing The thing to check for
	 * @return true if it can leave, false otherwise. Always true by default, override to provide custom behavior
	 */
	public boolean canLeave(GameThing thing){
		return true;
	}
	
}
