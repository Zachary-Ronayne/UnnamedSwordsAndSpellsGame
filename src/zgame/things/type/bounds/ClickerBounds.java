package zgame.things.type.bounds;

/** An object which is able to click on a {@link Clickable3D} */
public interface ClickerBounds{

	/** @return The x coordinate where the click of this thing will originate */
	double getClickX();

	/** @return The y coordinate where the click of this thing will originate */
	double getClickY();

	/** @return The z coordinate where the click of this thing will originate */
	double getClickZ();
	
	/** @return The current yaw which this thing is looking */
	double getClickYaw();
	
	/** @return The current pitch which this thing is looking */
	double getClickPitch();
	
	/** @return The distance this thing can be away from {@link Clickable3D} things while being able to click on them */
	double getClickRange();

}
