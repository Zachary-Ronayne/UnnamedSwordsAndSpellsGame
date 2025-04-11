package zgame.things.type.bounds;

/** An object which has bounds that can be clicked in 3D */
public interface Clickable3D extends Bounds3D{
	/**
	 * Determine the distance the given ray is from this hitbox
	 * @param rx The x coordinate of the ray
	 * @param ry The y coordinate of the ray
	 * @param rz The z coordinate of the ray
	 * @param dx The x component of the direction of the ray
	 * @param dy The y component of the direction of the ray
	 * @param dz The z component of the direction of the ray
	 * @return the distance from the ray to this hitbox, or a negative number when the ray doesn't intersect
	 */
	double rayDistance(double rx, double ry, double rz, double dx, double dy, double dz);
}
