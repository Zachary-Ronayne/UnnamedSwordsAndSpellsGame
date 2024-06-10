package zgame.things.entity.movement;

// TODO maybe rename the Walk class to something else?
/** An enum describing different kinds of walking */
public enum WalkType{
	/** Normal movement on the ground where gravity applies */
	WALKING,
	/** Movement where gravity does not apply and movement on the y axis is allowed */
	FLYING
}
