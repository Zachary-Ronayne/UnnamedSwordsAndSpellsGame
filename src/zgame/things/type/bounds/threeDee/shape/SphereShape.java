package zgame.things.type.bounds.threeDee.shape;

import zgame.physics.V3D;
import zgame.things.type.bounds.threeDee.bounds.shape.ShapedBounds3D;
import zgame.things.type.bounds.threeDee.bounds.shape.SphereBounds;

/** A sphere */
public interface SphereShape extends Shape3D{
	
	/** @return The radius of the sphere */
	double getRadius();
	
	@Override
	default V3D getDimensions(){
		var d = this.getRadius() * 2;
		return new V3D(d, d, d);
	}
	
	/** @return The area of an axis aligned circle of this sphere */
	default double getFaceArea(){
		var r = this.getRadius();
		return r * r * Math.PI;
	}
	
	@Override
	default ShapedBounds3D atMinPosition(V3D minPos){
		var s = this;
		return new SphereBounds(){
			@Override
			public V3D getMinPosition(){
				return minPos;
			}
			
			@Override
			public double getRadius(){
				return s.getRadius();
			}
		};
	}
}
