package zgame.things.still.door;

import zgame.core.annotations.PackagePrivate;
import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;
import zgame.things.still.StaticThing;
import zgame.world.Room;

// TODO add docs
public abstract class Door<V extends ZVector<V>, State extends DoorState<V>> extends StaticThing<V, State>{
	
	/** The {@link Room} which this door leads to. Can be null to make this a real fake door */
	private Room<V> leadRoom;
	/** The position to place objects which go through this door */
	private V roomPos;
	
	/** @return See {@link #leadRoom} */
	public Room<V> getLeadRoom(){
		return this.leadRoom;
	}
	
	/** @return See {@link #roomPos} */
	public V getRoomPos(){
		return this.roomPos;
	}
	
	/**
	 * Set the place this {@link Door} leads to
	 *
	 * @param r See {@link #leadRoom}
	 * @param roomPos See roomPos
	 */
	public void setLeadRoom(Room<V> r, V roomPos){
		this.leadRoom = r;
		this.roomPos = roomPos;
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
		// TODO need to figure out how this should interact with state
		var leadRoom = this.getLeadRoom();
		
		if(leadRoom != null && !leadRoom.canEnter(thing)) return false;
		
		if(!this.canEnter(thing)) return false;
		// If the thing can leave the room, remove it
		if(r != null && r.canLeave(thing)) r.removeThing(thing);
			// Otherwise, do not allow the thing to enter the room
		else{
			return false;
		}
		if(leadRoom != null){
			this.onEntityEnter(thing);
			// TODO figure out how this should work with being accessible without exposing it to actual game logic for a specific implementation
			thing.enterRoom(r, leadRoom);
		}
		else return false;
		
		return true;
	}
	
	/**
	 * Run when an entity enters {@link #getLeadRoom()} of this door
	 * @param thing The entity moved
	 */
	public void onEntityEnter(EntityThing<V> thing){
		// TODO schedule position update/teleport, or make updating the position a part of the logic for entering a room
		thing.setPos(this.getRoomPos());
	}
	
}
