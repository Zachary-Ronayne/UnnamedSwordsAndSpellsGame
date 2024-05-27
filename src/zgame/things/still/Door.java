package zgame.things.still;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.things.entity.EntityThing2D;
import zgame.things.entity.StaticThing2D;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zgame.world.Room2D;

// TODO abstract this to 2D and 3D

/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 */
public class Door extends StaticThing2D implements GameTickable{
	
	/** The default value of {@link #width} */
	public static final double WIDTH = 70;
	/** The default value of {@link #height} */
	public static final double HEIGHT = 150;
	
	/** The {@link Room} which this door leads to. Can be null to make this a real fake door */
	private Room2D leadRoom;
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
		super(x, y, WIDTH, HEIGHT);
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
	public void setLeadRoom(Room2D r, double x, double y){
		this.leadRoom = r;
		this.roomX = x;
		this.roomY = y;
	}
	
	/** @return See {@link #leadRoom} */
	public Room2D getLeadRoom(){
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
	 * Move the given {@link EntityThing2D} from the given room to {@link #leadRoom}, only if it's able to enter this door
	 *
	 * @param r The room which thing is coming from, can be null if there is no room the thing is coming from
	 * @param thing The thing to move
	 * @param game The {@link Game} where this room entering takes place
	 * @return true if thing entered this room, false otherwise
	 */
	public boolean enterRoom(Room2D r, EntityThing2D thing, Game game){
		if(this.leadRoom != null && !this.leadRoom.canEnter(thing)) return false;
		
		if(!this.canEnter(thing)) return false;
		// If the thing can leave the room, remove it
		if(r != null && r.canLeave(thing)) r.removeThing(thing);
		// Otherwise, do not allow the thing to enter the room
		else {
			return false;
		}
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
	public boolean canEnter(EntityThing2D thing){
		return thing.canEnterRooms();
	}
	
	@Override
	public void tick(Game game, double dt){
		if(!this.isAutoEnter()) return;
		
		// Check every entity and if it touches this door, move it to this Room
		var room = (Room2D)game.getCurrentRoom();
		var entities = room.getEntities();
		for(var entity : entities){
			// TODO avoid needing this double check call to canEnter
			if(!this.canEnter(entity)) continue;
			
			if(entity.intersectsRect(this.getX(), this.getY(), this.getWidth(), this.getHeight())){
				this.enterRoom(room, entity, game);
			}
		}
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(.25, .125, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}
