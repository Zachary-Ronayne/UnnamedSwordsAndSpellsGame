package zgame.things.type.bounds.threeDee.bounds.shape;

import zgame.physics.V3D;
import zgame.physics.collision.Collision3D;
import zgame.things.type.bounds.ShapedBounds;
import zgame.things.type.bounds.threeDee.bounds.Bounds3D;

/** A 3D bounds represented by a concrete shape */
public sealed interface ShapedBounds3D extends Bounds3D, ShapedBounds<V3D> permits RectPrismBounds, CylinderBounds, SphereBounds, EmptyBounds3D{
	
	@Override
	default boolean intersects(Bounds3D bounds){
		return switch(bounds.asShapedBounds()){
			// No bounds, no intersection
			case EmptyBounds3D ignore -> false;
			// Otherwise, use the shape
			case CylinderBounds c -> this.intersectsCylinder(c);
			case RectPrismBounds r -> this.intersectsRect(r);
			case SphereBounds s -> this.intersectsSphere(s);
		};
	}
	
	/** @return true if this bounds intersects the given rectangular prism bounds */
	boolean intersectsRect(RectPrismBounds bounds);
	
	/** @return true if this bounds intersects the given cylinder bounds */
	boolean intersectsCylinder(CylinderBounds bounds);
	
	/** @return true if this bounds intersects the given spherical bounds */
	boolean intersectsSphere(SphereBounds bounds);
	
	@Override
	default ShapedBounds3D asShapedBounds(){
		return this;
	}
	
	@Override
	default Collision3D collideAt(V3D currentMinPos, Bounds3D bounds){
		return switch(bounds.asShapedBounds()){
			// No bounds, no intersection
			case EmptyBounds3D ignore -> new Collision3D(this.getMinPosition());
			// Otherwise, use the shape
			case CylinderBounds c -> this.collideCylinder(c);
			case RectPrismBounds r -> this.collideRect(r);
			case SphereBounds s -> this.collideSphere(s);
		};
	}
	
	/**
	 * Determine a {@link Collision3D} from colliding this object with the given rectangular prism bounds. Essentially, move this object so that it no longer intersecting wit the given bounds.
	 *
	 * @param bounds The bounds of the prism collided with
	 * @return The information about the collision
	 */
	Collision3D collideRect(RectPrismBounds bounds);
	
	/**
	 * Determine a {@link Collision3D} from colliding this object with the given cylinder bounds. Essentially, move this object so that it no longer intersecting with the given bounds
	 *
	 * @param bounds The bounds of the cylinder collided with
	 * @return The information about the collision
	 */
	Collision3D collideCylinder(CylinderBounds bounds);
	
	/**
	 * Determine a {@link Collision3D} from colliding this object with the given spherical bounds. Essentially, move this object so that it no longer intersecting with the given bounds
	 *
	 * @param bounds The bounds of the cylinder collided with
	 * @return The information about the collision
	 */
	Collision3D collideSphere(SphereBounds bounds);
	
}
