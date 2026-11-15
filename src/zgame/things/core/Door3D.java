package zgame.things.core;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V3D;
import zgame.things.type.bounds.threeDee.bounds.shape.RectPrismBounds;
import zgame.things.type.bounds.threeDee.clickable.RectPrismClickable;

// TODO potentially move position and dims to a state, for now just making them static
/** A door for 3D rooms */
public class Door3D extends Door<V3D> implements RectPrismClickable, RectPrismBounds{
	
	/** The position of this door */
	private final V3D doorPos;
	/** The dimensions of this door */
	private final V3D doorDims;
	
	/**
	 * Initialize this door to lead to the given room
	 *
	 * @param pos The position of this door
	 * @param dims The dimensions of this door
	 * @param leadRoom See {@link #leadRoom}
	 * @param roomPos See {@link #roomPos}
	 */
	public Door3D(V3D pos, V3D dims, Room3D leadRoom, V3D roomPos){
		super(leadRoom, roomPos);
		
		this.doorPos = pos;
		this.doorDims = dims;
	}
	
	/**
	 * See {@link #enterRoom(Room, EntityThing)}
	 */
	public boolean enterRoom(Room3D r, EntityThing3D thing){
		return super.enterRoom(r, thing);
	}
	
	@Override
	public void tick(double dt){}
	
	@Override
	public void render(Renderer r){
		var c = new ZColor(0.35, 0.22, 0);
		r.drawRectPrism(new RectRender3D(this.getBounds()), c, c, c, c, c, c);
	}
	
	/**
	 * Run when this thing enters a room, does nothing by default, provide custom implementation for behavior
	 * @param to The room this was added to
	 */
	public void onRoomAdd(Room3D to){}
	
	// TODO is this the correct value?
	@Override
	public V3D getMinPosition(){
		return this.doorPos;
	}
	
	@Override
	public V3D getDimensions(){
		return this.doorDims;
	}
	
	@Override
	public boolean[] getCollisionFaces(){
		// TODO make a constant for representing no collision, or probably make this an object instead of a plain boolean array
		return new boolean[]{false, false, false, false, false, false};
	}
}
