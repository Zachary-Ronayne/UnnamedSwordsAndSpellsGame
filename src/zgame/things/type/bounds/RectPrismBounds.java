package zgame.things.type.bounds;

/** An object with the bounds of an axis aligned rectangular prism */
public interface RectPrismBounds extends Bounds3D{
	
	/** @return The bottom center x coordinate of this bounds */
	double getX();
	/** @return The bottom center y coordinate of this bounds */
	double getY();
	/** @return The bottom center z coordinate of this bounds */
	double getZ();
	
	/** @return The width, i.e. x axis size, of this bounds */
	double getWidth();
	
	/** @return The height, i.e. y axis size, of this bounds */
	double getHeight();
	
	/** @return The length, i.e. z axis size, of this bounds */
	double getLength();
	
	@Override
	default double maxX(){
		return this.getX() + this.getWidth() * 0.5;
	}
	
	@Override
	default double minX(){
		return this.getX() - this.getWidth() * 0.5;
	}
	
	@Override
	default double maxY(){
		return this.getY() + this.getHeight();
	}
	
	@Override
	default double minY(){
		return this.getY();
	}
	
	@Override
	default double maxZ(){
		return this.getZ() + this.getLength() * 0.5;
	}
	
	@Override
	default double minZ(){
		return this.getZ() - this.getLength() * 0.5;
	}
	
}
