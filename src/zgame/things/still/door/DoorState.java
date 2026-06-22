package zgame.things.still.door;

import zgame.physics.ZVector;
import zgame.things.still.StaticThingState;
import zgame.things.core.GameThing;
import zgame.things.core.Room;

/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 *
 * @param <V> The vectors used by this door
 */
public abstract class DoorState<V extends ZVector<V>> extends StaticThingState<V>{
	// TODO consider which fields from Door should be moved to DoorState
}
