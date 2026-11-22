package zgame.things.core.state;

import zgame.core.GameTickable;
import zgame.physics.ZVector;
import zgame.things.core.GameThing;

import java.util.UUID;

/**
 * A positioned {@link GameThing} that does not move
 *
 * @param <V> The type of vector this thing uses
 */
// TODO consider doing something meaningful with the vector here, maybe static things have a bounds
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
	
}
