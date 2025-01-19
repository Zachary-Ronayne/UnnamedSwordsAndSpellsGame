package zgame.things.type.bounds;

import zgame.core.utils.ZMath;

/** A clickable object with the hitbox of an axis aligned rectangular prism */
public interface RectPrismClickable extends Clickable3D, RectPrismBounds{
	
	@Override
	default double rayDistance(double rx, double ry, double rz, double dx, double dy, double dz){
		return ZMath.rayDistanceToRectPrism(rx, ry, rz, dx, dy, dz, this.minX(), this.minY(), this.minZ(), this.maxX(), this.maxY(), this.maxZ());
	}
	
}
