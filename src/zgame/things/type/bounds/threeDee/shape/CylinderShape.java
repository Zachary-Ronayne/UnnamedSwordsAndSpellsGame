package zgame.things.type.bounds.threeDee.shape;

import zgame.physics.V3D;
import zgame.things.type.bounds.threeDee.bounds.shape.CylinderBounds;
import zgame.things.type.bounds.threeDee.bounds.shape.ShapedBounds3D;

/** A cylinder with an xz axis aligned circular face */
public interface CylinderShape extends Shape3D{
	
	/** @return The radius of the circular face */
	double getRadius();
	
	/** @return The height of this cylinder */
	double getCylinderHeight();
	
	@Override
	default V3D getDimensions(){
		var d = this.getRadius() * 2;
		return new V3D(d, this.getCylinderHeight(), d);
	}
	
	/** @return The area of the axis aligned face of this cylinder */
	default double getFaceArea(){
		var r = this.getRadius();
		return r * r * Math.PI;
	}
	
	@Override
	default ShapedBounds3D atMinPosition(V3D minPos){
		var c = this;
		return new CylinderBounds(){
			@Override
			public V3D getMinPosition(){
				return minPos;
			}
			
			@Override
			public double getRadius(){
				return c.getRadius();
			}
			
			@Override
			public double getCylinderHeight(){
				return c.getCylinderHeight();
			}
		};
	}
}
