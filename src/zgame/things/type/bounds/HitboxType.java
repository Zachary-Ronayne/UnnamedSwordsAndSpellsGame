package zgame.things.type.bounds;

/** The type of hitbox that tells an object what hitbox collision method to use */
public enum HitboxType{
	/** A non rotating rectangular hitbox */
	RECT,
	/** A circle hitbox */
	CIRCLE,
	/** A non rotating upwards facing cylinder */
	CYLINDER,
	/** An axis aligned rectangular prism */
	RECT_PRISM
}
