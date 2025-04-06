package zgame.world;

import zgame.core.utils.ZMath;

/**
 * Am enum containing constants to use for indexing arrays with the 6 directions in 3D space, i.e. east, west, north, south, up, down.
 * These should be used any time all 6 3D directions must be stored
 */
public enum Direction3D{
	
	/** The index representing the east, i.e. positive x axis direction */
	EAST(0, true, false, 0, 0),
	/** The index representing the west, i.e. negative x axis direction */
	WEST(1, true, true, Math.PI, 0),
	/** The index representing the north, i.e. positive z axis direction */
	NORTH(2, true, false, ZMath.PI_BY_2, 0),
	/** The index representing the south, i.e. negative z axis direction */
	SOUTH(3, true, true, ZMath.PI_BY_2 + Math.PI, 0),
	/** The index representing the upwards, i.e. positive y axis direction */
	UP(4, false, false, 0, ZMath.PI_BY_2),
	/** The index representing the downwards, i.e. negative y axis direction */
	DOWN(5, false, true, 0, ZMath.PI_BY_2 + Math.PI);
	
	/** The expected index in an array for this direction */
	final int index;
	
	/** true if this is a cardinal direction, north, south, east, west, false otherwise */
	final boolean cardinal;
	
	/** true if this points in the negative direction, false otherwise */
	final boolean negative;
	
	/** The yaw, in radians, that this direction points in, always 0 for non cardinal directions */
	final double yaw;
	
	/** The yaw, in radians, that this direction points in, always 0 for cardinal directions */
	final double pitch;
	
	/** @param index See {@link #index} */
	Direction3D(int index, boolean cardinal, boolean negative, double yaw, double pitch){
		this.index = index;
		this.cardinal = cardinal;
		this.negative = negative;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	/** @return See {@link #index} */
	public int i(){
		return this.index;
	}
	
	/** @return See {@link #cardinal} */
	public boolean isCardinal(){
		return this.cardinal;
	}
	
	/** @return See {@link #negative} */
	public boolean isNegative(){
		return this.negative;
	}
	
	/** @return See {@link #yaw} */
	public double getYaw(){
		return this.yaw;
	}
	
	/** @return See {@link #pitch} */
	public double getPitch(){
		return this.pitch;
	}
	
	/**
	 * Determine the cardinal direction that the given angle most closely points to
	 * @param yaw The angle, in radians
	 * @return The direction
	 */
	public static Direction3D findCardinal(double yaw){
		yaw = ZMath.angleNormalized(yaw);
		double upRight = ZMath.PI_BY_4;
		double upLeft = ZMath.PI_BY_4 + ZMath.PI_BY_2;
		double downLeft = ZMath.PI_BY_4 + Math.PI;
		double downRight = ZMath.PI_BY_4 + ZMath.PI_BY_2 + Math.PI;
		
		if(yaw >= upRight && yaw < upLeft) return NORTH;
		else if(yaw >= upLeft && yaw < downLeft) return WEST;
		else if(yaw >= downLeft && yaw < downRight) return SOUTH;
		else return EAST;
	}
	
}
