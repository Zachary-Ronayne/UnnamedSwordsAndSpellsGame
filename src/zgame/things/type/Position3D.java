package zgame.things.type;

import zgame.physics.V3D;

/** An object that tracks an x, y, and z position */
public interface Position3D extends Position<V3D>{
	
	/** @return The x coordinate of this object */
	double getX();
	
	/** @return The y coordinate of this object */
	double getY();
	
	/** @return The z coordinate of this object */
	double getZ();
	
	@Override
	default V3D getPosition(){
		return new V3D(this.getX(), this.getY(), this.getZ());
	}
}
