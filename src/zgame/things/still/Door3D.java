package zgame.things.still;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V3D;
import zgame.things.entity.EntityThing;
import zgame.things.type.GameThing;
import zgame.things.type.bounds.RectPrismClickable;
import zgame.world.Room;
import zgame.world.Room3D;

/**
 * An object that allows other {@link GameThing}s to enter another {@link Room}
 */
public class Door3D extends StaticThing3D implements Door<V3D>, RectPrismClickable{
	
	/** The {@link Room} which this door leads to. Can be null to make this a real fake door */
	private Room3D leadRoom;
	/** The x coordinate to place objects which go through this door */
	private double roomX;
	/** The y coordinate to place objects which go through this door */
	private double roomY;
	/** The z coordinate to place objects which go through this door */
	private double roomZ;
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param w See {@link #width}
	 * @param h See {@link #height}
	 * @param l See {@link #length}
	 */
	public Door3D(double x, double y, double z, double w, double h, double l){
		super(x, y, z, w, h, l);
		
		this.setLeadRoom(null, 0, 0, 0);
	}
	
	/**
	 * Set the place this {@link Door3D} leads to
	 *
	 * @param r See {@link #leadRoom}
	 * @param x See {@link #roomX}
	 * @param y See {@link #roomY}
	 * @param z See {@link #roomZ}
	 */
	public void setLeadRoom(Room3D r, double x, double y, double z){
		this.leadRoom = r;
		this.roomX = x;
		this.roomY = y;
		this.roomZ = z;
	}
	
	/** @return See {@link #leadRoom} */
	public Room3D getLeadRoom(){
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
	
	/** @return See {@link #roomZ} */
	public double getRoomZ(){
		return this.roomZ;
	}
	
	/** @param roomZ See {@link #roomZ} */
	public void setRoomZ(double roomZ){
		this.roomZ = roomZ;
	}
	
	@Override
	public void onEntityEnter(EntityThing<V3D> thing){
		// TODO schedule position update/teleport, or make updating the position a part of the logic for entering a room
		thing.setPos(this.roomX, this.roomY, this.roomZ);
	}
	
	@Override
	public void tick(double dt){}
	
	@Override
	public void render(Renderer r){
		var c = new ZColor(0.35, 0.22, 0);
		r.drawRectPrism(new RectRender3D(this.getBounds()), c, c, c, c, c, c);
	}
	
}
