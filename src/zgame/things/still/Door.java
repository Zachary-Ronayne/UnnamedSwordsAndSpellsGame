package zgame.things.still;

import java.util.Collection;
import java.util.UUID;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.things.entity.EntityThing;
import zgame.things.type.GameThing;
import zgame.things.type.PositionedHitboxThing;
import zgame.things.type.PositionedRectangleThing;
import zgame.world.Room;

/** An object that allows other {@link GameThing}s to enter another {@link Room} */
public class Door extends PositionedRectangleThing implements GameTickable{
	
	/** The default value of {@link #width} */
	public static final double WIDTH = 70;
	/** The default value of {@link #height} */
	public static final double HEIGHT = 150;
	
	/** The uuid of this door */
	private final String uuid;
	
	/** The {@link Room} which this door leads to. Can be null to make this a real fake door */
	private Room leadRoom;
	/** The x coordinate to place objects which go through this door */
	private double roomX;
	/** The y coordinate to place objects which go through this door */
	private double roomY;
	
	/** true if entities which touch this door should automatically enter it, false otherwise */
	private boolean autoEnter;
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public Door(double x, double y){
		this(x, y, true);
	}
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 * @param autoEnter See {@link #autoEnter}
	 */
	public Door(double x, double y, boolean autoEnter){
		super(x, y);
		this.uuid = UUID.randomUUID().toString();
		
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setAutoEnter(autoEnter);
		
		this.setLeadRoom(null, 0, 0);
	}
	
	/**
	 * Set the place this {@link Door} leads to
	 *
	 * @param r See {@link #leadRoom}
	 * @param x See {@link #roomX}
	 * @param y See {@link #roomY}
	 */
	public void setLeadRoom(Room r, double x, double y){
		this.leadRoom = r;
		this.roomX = x;
		this.roomY = y;
	}
	
	/** @return See {@link #leadRoom} */
	public Room getLeadRoom(){
		return this.leadRoom;
	}
	
	/** @return See {@link #roomX} */
	public double getRoomX(){
		return this.roomX;
	}
	
	/** @param roomX See {@link #roomX} */
	public void setRoomX(double roomX){
		this.roomX = roomX;
	}
	
	/** @return See {@link #roomY} */
	public double getRoomY(){
		return this.roomY;
	}
	
	/** @param roomY See {@link #roomY} */
	public void setRoomY(double roomY){
		this.roomY = roomY;
	}
	
	/** @return See {@link #autoEnter} */
	public boolean isAutoEnter(){
		return this.autoEnter;
	}
	
	/** @param autoEnter See {@link #autoEnter} */
	public void setAutoEnter(boolean autoEnter){
		this.autoEnter = autoEnter;
	}
	
	/**
	 * Move the given {@link PositionedHitboxThing} from the given room to {@link #leadRoom}, only if it's able to enter this door
	 *
	 * @param r The room which thing is coming from, can be null if there is no room the thing is coming from
	 * @param thing The thing to move
	 * @param game The {@link Game} where this room entering takes place
	 * @return true if thing entered this room, false otherwise
	 */
	public boolean enterRoom(Room r, PositionedHitboxThing thing, Game game){
		if(this.leadRoom != null && !this.leadRoom.canEnter(thing)) return false;
		
		if(!this.canEnter(thing)) return false;
		// If the thing can leave the room, remove it
		if(r != null && r.canLeave(thing)) r.removeThing(thing);
			// Otherwise, do not allow the thing to enter the room
		else return false;
		if(this.leadRoom != null){
			thing.setX(this.roomX);
			thing.setY(this.roomY);
			thing.enterRoom(r, this.leadRoom, game);
		}
		else return false;
		return true;
	}
	
	/**
	 * Determine if thing is able to enter this door.
	 * Always returns true by default, can override to implement custom behavior
	 *
	 * @param thing The thing
	 * @return true if thing can enter the door, false otherwise
	 */
	public boolean canEnter(PositionedHitboxThing thing){
		return true;
	}
	
	@Override
	public void tick(Game game, double dt){
		if(!this.isAutoEnter()) return;
		
		// Check every entity and if it touches this door, move it to this Room
		Collection<EntityThing> entities = game.getCurrentRoom().getEntities();
		for(EntityThing e : entities){
			if(e.intersectsRect(this.getX(), this.getY(), this.getWidth(), this.getHeight())){
				this.enterRoom(game.getCurrentRoom(), e, game);
			}
		}
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(.25, .125, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	@Override
	public final GameTickable asTickable(){
		return this;
	}
	
	@Override
	public String getUuid(){
		return this.uuid;
	}
	
}
