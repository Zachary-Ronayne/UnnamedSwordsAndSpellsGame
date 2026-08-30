package zgame.things.core;

import zgame.core.annotations.PackagePrivate;
import zgame.physics.ZVector;
import zgame.things.core.state.StaticThing;
import zgame.things.type.bounds.Bounds;

/**
 * A door that can be interacted with to enter another {@link Room}
 * @param <V> The type of vectors this door uses
 */
public abstract class Door<V extends ZVector<V>> extends StaticThing<V> implements Bounds<V>{
	
	/** The {@link Room} which this door leads to. Can be null to make this a real fake door */
	private final Room<V> leadRoom;
	/** The position to place objects which go through this door */
	private final V roomPos;
	
	/**
	 * Initialize this door to lead to the given room
	 * @param leadRoom See {@link #leadRoom}
	 * @param roomPos See {@link #roomPos}
	 */
	public Door(Room<V> leadRoom, V roomPos){
		this.leadRoom = leadRoom;
		this.roomPos = roomPos;
	}
	
	/** @return See {@link #leadRoom} */
	public Room<V> getLeadRoom(){
		return this.leadRoom;
	}
	
	/** @return See {@link #roomPos} */
	public V getRoomPos(){
		return this.roomPos;
	}
	
	/**
	 * Determine if thing is able to enter this door.
	 * Calls {@link EntityThing#canEnterRooms()} by default, can override to implement custom behavior
	 *
	 * @param thing The thing
	 * @return true if thing can enter the door, false otherwise
	 */
	public boolean canEnter(EntityThing<V> thing){
		return thing.canEnterRooms();
	}
	
	/**
	 * Move the given {@link EntityThing} from the given room to {@link #getLeadRoom()}, only if it's able to enter this door
	 *
	 * @param r The room which thing is coming from, can be null if there is no room the thing is coming from
	 * @param thing The thing to move
	 * @return true if thing entered this room, false otherwise
	 */
	@PackagePrivate
	boolean enterRoom(Room<V> r, EntityThing<V> thing){
		// Do nothing if the thing cannot enter the room
		var leadRoom = this.getLeadRoom();
		if(leadRoom != null && !leadRoom.canEnter(thing)) return false;
		
		// Do nothing if this thing cannot enter the door
		if(!this.canEnter(thing)) return false;
		
		// If the thing can leave the room, remove it
		if(r != null && r.canLeave(thing)) r.removeThing(thing);
		// Otherwise, do not allow the thing to enter the room
		else return false;
		
		// After leaving the given room, move it to the lead room
		if(leadRoom != null){
			// TODO schedule position update/teleport, or make updating the position a part of the logic for entering a room
			thing.initPosition(this.getRoomPos());
			
			leadRoom.addThing(thing);
		}
		else return false;
		
		return true;
	}
	
	
}
