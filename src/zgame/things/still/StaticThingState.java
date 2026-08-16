package zgame.things.still;

import zgame.core.utils.Uuidable;
import zgame.physics.ZVector;
import zgame.things.core.GameThingState;
import zgame.things.type.bounds.Bounds;

import java.util.UUID;

// TODO is this object needed? Probably can delete this

/** The state of a thing which does not move as an entity would, and generally doesn't move, but can be at an arbitrary position */
public abstract class StaticThingState<V extends ZVector<V>> extends GameThingState implements Uuidable, Bounds<V>{
	
	
	/**
	 * Create a new empty static thing
	 */
	public StaticThingState(){
		super();
		
		this.uuid = UUID.randomUUID().toString();
	}
	
	
}