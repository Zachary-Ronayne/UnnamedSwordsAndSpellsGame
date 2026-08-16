package zgame.things.core;

import zgame.core.GameTickable;
import zgame.core.annotations.PackagePrivate;
import zgame.physics.ZVector;

import java.util.UUID;

/**
 * A positioned {@link GameThing} that does not move
 *
 * @param <V> The type of vector this thing uses
 */
public abstract class StaticThing<V extends ZVector<V>> extends GameThing implements GameTickable{
	
	/** The uuid representing this thing */
	private final String uuid;
	
	/**
	 * Create a new empty static thing
	 */
	public StaticThing(){
		super();
		
		this.uuid = UUID.randomUUID().toString();
	}
	
	/** @return See {@link #uuid} */
	@Override
	public String getUuid(){
		return this.uuid;
	}
	
	// TODO are these needed?
	@Override
	@PackagePrivate
	@SuppressWarnings("unchecked")
	final <AddV extends ZVector<AddV>> void onThingRoomAdd(Room<AddV> to){
		this.onRoomAdd((Room<V>)to);
	}
	
	/**
	 * Run when this thing enters a room, does nothing by default, provide custom implementation for behavior
	 *
	 * @param to The room this was added to
	 */
	@PackagePrivate
	abstract void onRoomAdd(Room<V> to);
	
}
