package zgame.things.still.tiles;

import zgame.physics.ZVector;
import zgame.things.core.GameThing;
import zgame.things.type.Materialable;
import zgame.things.type.bounds.Bounds;

/** A {@link GameThing} with a bounds based on an index in an array. The indexes of this object should directly correlate to its position */
public abstract class Tile<V extends ZVector<V>> extends GameThing implements Bounds<V>, Materialable{
}
