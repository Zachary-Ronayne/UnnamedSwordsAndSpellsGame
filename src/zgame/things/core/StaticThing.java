package zgame.things.core;

import zgame.core.GameTickable;
import zgame.core.annotations.PackagePrivate;
import zgame.physics.ZVector;
import zgame.things.still.StaticThingState;

// TODO add docs
public abstract class StaticThing<V extends ZVector<V>, State extends StaticThingState<V>> extends GameThing<State> implements GameTickable{
	
	@Override
	public String getUuid(){
		return this.getCurrent().getUuid();
	}
	
	@Override
	@PackagePrivate
	@SuppressWarnings("unchecked")
	final <AddV extends ZVector<AddV>> void onThingRoomAdd(Room<AddV> to){
		this.onRoomAdd((Room<V>)to);
	}
	
	/**
	 * Run when this thing enters a room, does nothing by default, provide custom implementation for behavior
	 * @param to The room this was added to
	 */
	@PackagePrivate
	abstract void onRoomAdd(Room<V> to);
}
