package zgame.things.type.bounds;

// TODO probably split this into two types, 2D and 3D
/** Represents the kind of bounds that this object has */
public enum BoundsType{
	/** No bounds */
	NONE,
	
	// 2D
	/** A non-rotating rectangular bounds */
	RECT,
	/** A circle hitbox */
	CIRCLE,
	
	// 3D
	/** A non-rotating upwards facing cylinder */
	CYLINDER,
	/** An axis aligned rectangular prism */
	RECT_PRISM,
	/** A sphere hitbox */
	SPHERE,
}
