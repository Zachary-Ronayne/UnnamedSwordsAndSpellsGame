package zgame.things.still;

import zgame.core.utils.Uuidable;
import zgame.physics.ZVector;
import zgame.things.core.GameThingState;
import zgame.things.type.bounds.Bounds;

import java.util.UUID;

/** The state of a thing which does not move as an entity would, and generally doesn't move, but can be at an arbitrary position */
public abstract class StaticThingState<V extends ZVector<V>> extends GameThingState implements Uuidable, Bounds<V>{
	
	/** The uuid representing this thing */
	private final String uuid;
	
	/**
	 * Create a new empty static thing
	 */
	public StaticThingState(){
		super();
		
		this.uuid = UUID.randomUUID().toString();
	}
	
	/** @return See {@link #uuid} */
	@Override
	public String getUuid(){
		return this.uuid;
	}
	
}