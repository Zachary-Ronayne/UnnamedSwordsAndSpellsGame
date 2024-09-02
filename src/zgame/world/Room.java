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
 * @param <H> The implementation of hitboxes in this room
 * @param <E> The type of entities in this room
 * @param <V> The type of vector used by this room
 * @param <R> The room implementation
 */
public abstract class Room<H extends HitBox<H>, E extends EntityThing<H, E, V, R>, V extends ZVector<V>, R extends Room<H, E, V, R>> extends GameThing{
	
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
		this.thingsMap.addClass(HitBox.class);
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
	public abstract CollisionResult collide(H obj);
	
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
		for(int i = 0; i < entities.size(); i++) {
			var e = entities.get(i);
			if(e.isNoClip()) continue;
			this.collide(e.get());
		}
		
		// Remove all things that need to be removed
		for(GameThing thing : this.thingsToRemove) this.tickRemoveThing(thing);
		this.thingsToRemove.clear();
		
		// Run any functions which need to happen
		for(int i = 0; i < this.nextTickFuncs.size(); i++) this.nextTickFuncs.get(i).run();
		this.nextTickFuncs.clear();
	}
	
	/**
	 * Called each time a thing is removed via {@link #tick(Game, double)}, i.e. the thing was added to {@link #thingsToRemove}, and now it's being removed
	 *
	 * @param thing The thing to remove
	 */
	public void tickRemoveThing(GameThing thing){
		this.thingsMap.remove(thing);
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
