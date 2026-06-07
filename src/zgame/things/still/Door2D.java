package zgame.things.still;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.V2D;
import zgame.things.entity.EntityThing;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zgame.world.Room2D;

/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 */
public class Door2D extends StaticThing2D implements Door<V2D>{
	
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
	public Door2D(double x, double y){
		this(x, y, true);
	}
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 * @param autoEnter See {@link #autoEnter}
	 */
	public Door2D(double x, double y, boolean autoEnter){
		super(x, y, WIDTH, HEIGHT);
		this.setAutoEnter(autoEnter);
		
		this.setLeadRoom(null, 0, 0);
	}
	
	/**
	 * Set the place this {@link Door2D} leads to
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
	@Override
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
	
	@Override
	public void onEntityEnter(EntityThing<V2D> thing){
		// TODO schedule position update/teleport, or make updating the position a part of the logic for entering a room
		thing.setPos(this.roomX, this.roomY);
	}
	
	@Override
	public void tick(double dt){
		if(!this.isAutoEnter()) return;
		
		// Check every entity and if it touches this door, move it to this Room
		var room = (Room2D)Game.get().getCurrentRoom();
		var entities = room.getEntities();
		for(var entity : entities){
			// issue#49 avoid needing this double check call to canEnter
			if(!this.canEnter(entity)) continue;
			
			if(entity.intersectsRect(this.getX(), this.getY(), this.getWidth(), this.getHeight())){
				this.enterRoom(room, entity);
			}
		}
	}
	
	@Override
	public void render(Renderer r){
		r.setColor(.25, .125, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}
