package zgame.things.type.bounds;

import zgame.core.utils.ZRect3D;
import zgame.physics.V3D;
import zgame.things.type.Position3D;

/** An object which has a bounds that can be defined in a 3D space */
public interface Bounds3D extends Position3D, Bounds<V3D>{
	
	/** @return The maximum x coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double maxX(){
		return this.getMaxPosition().getX();
	}
	
	/** @return The minimum x coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double minX(){
		return this.getMinPosition().getX();
	}
	
	/** @return The maximum y coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double maxY(){
		return this.getMaxPosition().getY();
	}
	
	/** @return The minimum y coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double minY(){
		return this.getMinPosition().getY();
	}
	
	/** @return The maximum z coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double maxZ(){
		return this.getMaxPosition().getZ();
	}
	
	/** @return The minimum z coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double minZ(){
		return this.getMinPosition().getZ();
	}
	
	/** @return The width this bounds takes up */
	default double getWidth(){
		return this.getDimensions().getWidth();
	}
	
	/** @return The height this bounds takes up */
	default double getHeight(){
		return this.getDimensions().getHeight();
	}
	
	/** @return The length this bounds takes up */
	default double getLength(){
		return this.getDimensions().getLength();
	}
	
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
	
	/** @return A rectangle representing the full bounds which this {@link Bounds3D} takes up */
	default ZRect3D getBounds(){
		return new ZRect3D(this.getX(), this.getY(), this.getZ(), this.getWidth(), this.getHeight(), this.getLength());
	}
	
	/**
	 * Find the distance from this bounds to the given bounds, based on their center bottom coordinates
	 *
	 * @param b The other bounds to compare
	 * @return The distance
	 */
	default double distance(Bounds3D b){
		double x = this.getX() - b.getX();
		double y = this.getY() - b.getY();
		double z = this.getZ() - b.getZ();
		return Math.sqrt(x * x + y + y + z * z);
	}
	
}