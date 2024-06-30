package zgame.things.entity.movement;

// TODO maybe rename the Walk class to something else?
/** An enum describing different kinds of walking */
public enum WalkType{
	/** Normal movement on the ground where gravity applies */
	WALKING,
	/** Movement where gravity does not apply and movement on the y axis is allowed, movement is based on the direction facing on both axes */
	FLYING,
	/** Movement where gravity does not apply and movement on the y axis is allowed, facing direction only effects horizontal movement */
	FLYING_AXIS
}
