package zgame.things.still;

import zgame.core.GameTickable;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResult;
import zgame.things.entity.EntityThing;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.HitBox;
import zgame.world.Room;

/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 *
 * @param <H> The hitbox implementation used by entities in the room
 * @param <E> The type of entities in the room
 * @param <V> The vectors used by entities in the room
 * @param <R> The room implementation which this door can be in
 * @param <C> The type of collisions which occur in the room
 */
// issue#50 find a way to avoid having to do this comical amount of type parameters without having to resort to weird type casting or instanceof checks
public interface Door<
		R extends Room<H, E, V, R, C>,
		H extends HitBox<H, C>,
		E extends EntityThing<H, E, V, R, C>,
		V extends ZVector<V>,
		C extends CollisionResult<C>
		> extends GameTickable{
	
	/** @return The {@link Room} which this door leads to. Can be null to make this a real fake door */
	R getLeadRoom();
	
	/**
	 * Move the given {@link EntityThing} from the given room to {@link #getLeadRoom()}, only if it's able to enter this door
	 *
	 * @param r The room which thing is coming from, can be null if there is no room the thing is coming from
	 * @param thing The thing to move
	 * @return true if thing entered this room, false otherwise
	 */
	default boolean enterRoom(R r, E thing){
		var leadRoom = this.getLeadRoom();
		
		if(leadRoom != null && !leadRoom.canEnter(thing)) return false;
		
		if(!this.canEnter(thing)) return false;
		// If the thing can leave the room, remove it
		if(r != null && r.canLeave(thing)) r.removeThing(thing);
		// Otherwise, do not allow the thing to enter the room
		else {
			return false;
		}
		if(leadRoom != null){
			this.onEntityEnter(thing);
			thing.enterRoom(r, leadRoom);
		}
		else return false;
		return true;
	}
	
	/**
	 * Run when an entity enters {@link #getLeadRoom()} of this door
	 * @param thing The entity moved
	 */
	void onEntityEnter(E thing);
	
	/**
	 * Determine if thing is able to enter this door.
	 * Calls {@link EntityThing#canEnterRooms()} by default, can override to implement custom behavior
	 *
	 * @param thing The thing
	 * @return true if thing can enter the door, false otherwise
	 */
	default boolean canEnter(E thing){
		return thing.canEnterRooms();
	}
	
}
