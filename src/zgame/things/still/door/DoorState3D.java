package zgame.things.still.door;

import zgame.things.still.StaticThingState3D;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.RectPrismBounds;
import zgame.world.Room;

// TODO consider which fields from Door should be moved to DoorState
/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 */
public class DoorState3D extends StaticThingState3D implements RectPrismBounds{
	
	// TODO does this make any sense?
	/**
	 * Create a new entity with the given values
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 */
	public DoorState3D(double x, double y, double z, double w, double h, double l){
		super(x, y, z, w, h, l);
	}
}
