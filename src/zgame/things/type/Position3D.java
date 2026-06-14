package zgame.things.type;

import zgame.physics.V3D;

/** An object that tracks an x, y, and z position */
public interface Position3D extends Position<V3D>{
	
	/** @return The x coordinate of this object */
	default double getX(){
		return this.getPosition().getX();
	}
	
	/** @return The y coordinate of this object */
	default double getY(){
		return this.getPosition().getY();
	}
	
	/** @return The z coordinate of this object */
	default double getZ(){
		return this.getPosition().getZ();
	}
}
