package zgame.things.type.bounds.threeDee.clickable;

import zgame.core.utils.ZMath;
import zgame.things.type.bounds.threeDee.bounds.shape.RectPrismBounds;

/** A clickable object with the hitbox of an axis aligned rectangular prism */
public interface RectPrismClickable extends Clickable3D, RectPrismBounds{
	
	@Override
	default double rayDistance(double rx, double ry, double rz, double dx, double dy, double dz){
		return ZMath.rayDistanceToRectPrism(rx, ry, rz, dx, dy, dz, this.minX(), this.minY(), this.minZ(), this.maxX(), this.maxY(), this.maxZ());
	}
	
}
