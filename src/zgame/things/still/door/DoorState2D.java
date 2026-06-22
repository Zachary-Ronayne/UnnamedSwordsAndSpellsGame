package zgame.things.still.door;

import zgame.physics.V2D;
import zgame.things.core.GameThing;
import zgame.things.type.bounds.Bounds2D;
import zgame.things.core.Room;

/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 */
public class DoorState2D extends DoorState<V2D> implements Bounds2D{
	
	/** The default value of {@link #width */
	public static final double WIDTH = 70;
	/** The default value of {@link #height} */
	public static final double HEIGHT = 150;
	
	/** true if entities which touch this door should automatically enter it, false otherwise */
	private boolean autoEnter;
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public DoorState2D(double x, double y){
		this(x, y, true);
	}
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 * @param autoEnter See {@link #autoEnter}
	 */
	public DoorState2D(double x, double y, boolean autoEnter){
		super(x, y, WIDTH, HEIGHT);
		this.setAutoEnter(autoEnter);
	}
	
	/** @return See {@link #autoEnter} */
	public boolean isAutoEnter(){
		return this.autoEnter;
	}
	
	/** @param autoEnter See {@link #autoEnter} */
	public void setAutoEnter(boolean autoEnter){
		this.autoEnter = autoEnter;
	}
	
}
