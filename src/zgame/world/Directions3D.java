package zgame.world;

/**
 * A class containing constants to use for indexing arrays with the 6 directions in 3D space, i.e. east, west, north, south, up, down.
 * These should be used any time all 6 3D directions must be stored
 */
public final class Directions3D{
	
	/** The index representing the east, i.e. positive x axis direction */
	public static final int EAST = 0;
	/** The index representing the west, i.e. negative x axis direction */
	public static final int WEST = 1;
	/** The index representing the north, i.e. positive z axis direction */
	public static final int NORTH = 2;
	/** The index representing the south, i.e. negative z axis direction */
	public static final int SOUTH = 3;
	/** The index representing the upwards, i.e. positive y axis direction */
	public static final int UP = 4;
	/** The index representing the downwards, i.e. negative y axis direction */
	public static final int DOWN = 5;
	
	/** Cannot instantiate {@link Directions3D} */
	private Directions3D(){}
	
}
