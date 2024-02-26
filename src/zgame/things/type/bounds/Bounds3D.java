package zgame.things.type.bounds;

import zgame.core.utils.ZPoint3D;
import zgame.core.utils.ZRect3D;
import zgame.things.type.Position3D;

/** An object which has a bounds that can be defined in a 3D space */
public interface Bounds3D extends Position3D{
	
	/** @return The width this bounds takes up */
	double getWidth();
	
	/** @return The height this bounds takes up */
	double getHeight();
	
	/** @return The length this bounds takes up */
	double getLength();
	
	/** @return The center x coordinate of this bounds */
	default double centerX(){
		return this.getX();
	}
	
	/** @return The center y coordinate of this bounds */
	default double centerY(){
		return this.getY() + getHeight() * 0.5;
	}
	
	/** @return The center z coordinate of this bounds */
	default double centerZ(){
		return this.getZ();
	}
	
	/** @return The center point of this {@link Bounds3D} */
	default ZPoint3D center(){
		return new ZPoint3D(this.centerX(), this.centerY(), this.centerZ());
	}
	
	/** @return A rectangle representing the full bounds which this {@link Bounds3D} takes up */
	default ZRect3D getBounds(){
		return new ZRect3D(this.getX(), this.getY(), this.getZ(), this.getWidth(), this.getHeight(), this.getLength());
	}
	
}