package zgame.world;

/**
 * Am enum containing constants to use for indexing arrays with the 4 cardinal directions in 3D space, i.e. east, west, north, south
 * These should be used any time all 6 3D directions must be stored
 */
public enum Cardinal{
	
	/** The index representing the east, i.e. positive x axis direction */
	EAST(0),
	/** The index representing the west, i.e. negative x axis direction */
	WEST(1),
	/** The index representing the north, i.e. positive z axis direction */
	NORTH(2),
	/** The index representing the south, i.e. negative z axis direction */
	SOUTH(3);
	
	/** The expected index in an array for this direction */
	final int index;
	
	/** @param index See {@link #index} */
	Cardinal(int index){
		this.index = index;
	}
	
	/** @return See {@link #index} */
	public int getIndex(){
		return this.index;
	}
	
}
