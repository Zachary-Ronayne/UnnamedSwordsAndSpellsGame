package zgame.things.type.bounds;

/** A clickable object with the hitbox of an upright cylinder */
public interface CylinderClickable extends Clickable3D{
	@Override
	default double rayDistance(double rx, double ry, double rz, double dx, double dy, double dz){
		// issue#59 implement
		return 0;
	}
}
