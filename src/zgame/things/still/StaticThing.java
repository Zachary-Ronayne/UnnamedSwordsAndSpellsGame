package zgame.things.still;

import zgame.core.utils.Uuidable;
import zgame.things.type.GameThing;

import java.util.UUID;

/** A thing which does not move as an entity would, and generally doesn't move, but can be at an arbitrary position */
public abstract class StaticThing extends GameThing implements Uuidable{
	
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