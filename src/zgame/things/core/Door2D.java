package zgame.things.core;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.V2D;
import zgame.things.type.bounds.twoDee.bounds.shape.RectangleBounds;

/** A door for 2D rooms */
public class Door2D extends Door<V2D> implements RectangleBounds{
	
	// TODO probably move these out of the generic door, add these as a parameter
	/** The default value of the width dimension of this door */
	public static final double WIDTH = 70;
	/** The default value of the height dimension of this door */
	public static final double HEIGHT = 150;
	
	/** true if entities which touch this door should automatically enter it, false otherwise */
	private final boolean autoEnter;
	
	/** The position of this door */
	private final V2D doorPos;
	/** The dimensions of this door */
	private final V2D doorDims;
	
	/**
	 * @param leadRoom The room this door leads to
	 * @param roomPos The position in the room this door leads to
	 * @param autoEnter See {@link #autoEnter}
	 */
	public Door2D(V2D doorPos, Room<V2D> leadRoom, V2D roomPos, boolean autoEnter){
		super(leadRoom, roomPos);
		this.autoEnter = autoEnter;
		this.doorPos = doorPos;
		this.doorDims = new V2D(WIDTH, HEIGHT);
	}
	
	// TODO test if this position is correct
	@Override
	public V2D getMinPosition(){
		return this.doorPos;
	}
	
	@Override
	public V2D getDimensions(){
		return this.doorDims;
	}
	
	@Override
	public void tick(double dt){
		if(!this.isAutoEnter()) return;
		
		// Check every entity and if it touches this door, move it to this Room
		var room = Game.get().getCurrentRoom2D();
		var entities = room.getEntities();
		for(var entity : entities){
			// issue#49 avoid needing this double check call to canEnter
			if(!this.canEnter(entity)) continue;
			
			if(entity.intersects(this)){
				this.enterRoom(room, entity);
			}
		}
	}
	
	@Override
	public void render(Renderer r){
		r.setColor(.25, .125, 0);
		var pos = this.getMinPosition();
		var dims = this.getDimensions();
		r.drawRectangle(pos.getX(), pos.getY(), dims.getWidth(), dims.getHeight());
	}
	
	/**
	 * Run when this thing enters a room, does nothing by default, provide custom implementation for behavior
	 * @param to The room this was added to
	 */
	public void onRoomAdd(Room2D to){}
	
	public boolean isAutoEnter(){
		return autoEnter;
	}
	
}
