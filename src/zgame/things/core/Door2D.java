package zgame.things.core;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.V2D;

// TODO needs to implement a RectBounds, or make this abstract to define bounds, make a RectDoor class
/** A door for 2D rooms */
public class Door2D extends Door<V2D>{
	
	/** The default value of {@link #width */
	public static final double WIDTH = 70;
	/** The default value of {@link #height} */
	public static final double HEIGHT = 150;
	
	/** true if entities which touch this door should automatically enter it, false otherwise */
	private final boolean autoEnter;
	
	/**
	 * @param leadRoom The room this door leads to
	 * @param roomPos The position in the room this door leads to
	 * @param autoEnter See {@link #autoEnter}
	 */
	public Door2D(Room<V2D> leadRoom, V2D roomPos, boolean autoEnter){
		super(leadRoom, roomPos);
		this.autoEnter = autoEnter;
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
			
			var state = this.getCurrent();
			var pos = state.getMinPosition();
			var dims = state.getDimensions();
			if(entity.intersectsRect(pos.getX(), pos.getY(), dims.getWidth(), dims.getHeight())){
				this.enterRoom(room, entity);
			}
		}
	}
	
	@Override
	public void render(Renderer r){
		r.setColor(.25, .125, 0);
		var state = this.getCurrent();
		var pos = state.getMinPosition();
		var dims = state.getDimensions();
		r.drawRectangle(pos.getX(), pos.getY(), dims.getWidth(), dims.getHeight());
	}
	
	/**
	 * See {@link #enterRoom(Room, EntityThing)}
	 */
	public boolean enterRoom(Room2D r, EntityThing2D thing){
		return super.enterRoom(r, thing);
	}
	
	@Override
	void onRoomAdd(Room<V2D> to){
		this.onRoomAdd((Room2D)to);
	}
	
	/**
	 * Run when this thing enters a room, does nothing by default, provide custom implementation for behavior
	 * @param to The room this was added to
	 */
	public void onRoomAdd(Room2D to){}
}
