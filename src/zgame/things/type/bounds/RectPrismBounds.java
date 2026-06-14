package zgame.things.type.bounds;

import zgame.physics.V3D;

/** An object with the bounds of an axis aligned rectangular prism */
public interface RectPrismBounds extends Bounds3D{
	
	@Override
	default V3D getMaxPosition(){
		return new V3D(this.getX() + this.getWidth() * 0.5, this.getY() + this.getHeight(), this.getZ() + this.getLength() * 0.5);
	}
	
	@Override
	default V3D getMinPosition(){
		return new V3D(this.getX() - this.getWidth() * 0.5, this.getY(), this.getZ() - this.getLength() * 0.5);
	}
	
	@Override
	default V3D getCenterPosition(){
		return new V3D(this.getX(), this.getY() + this.getHeight() * 0.5, this.getZ());
	}
	
}
