package zgame.things.still;

import zgame.core.GameTickable;
import zgame.physics.ZVector;
import zgame.things.type.GameThing;

// TODO add docs
public abstract class StaticThing<V extends ZVector<V>, State extends StaticThingState<V>> extends GameThing<State> implements GameTickable{
	
	@Override
	public String getUuid(){
		return this.getCurrent().getUuid();
	}
}
