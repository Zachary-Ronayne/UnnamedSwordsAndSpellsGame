package zgame.things.entity.mobility;

/** An enum describing different kinds of movement */
public enum MobilityType{
	/** Normal mobility on the ground where gravity applies */
	WALKING,
	/** Mobility where gravity does not apply and movement on the y axis is allowed, movement is based on the direction facing on both axes */
	FLYING,
	/** Mobility where gravity does not apply and movement on the y axis is allowed, facing direction only effects horizontal movement */
	FLYING_AXIS
}
