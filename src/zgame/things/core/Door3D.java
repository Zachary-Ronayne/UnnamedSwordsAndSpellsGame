package zgame.things.core;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V3D;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.type.bounds.RectPrismClickable;
import zgame.things.type.bounds.RectPrismHitbox;

// TODO potentially move position and dims to a state, for now just making them static
/** A door for 3D rooms */
public class Door3D extends Door<V3D> implements RectPrismClickable, RectPrismHitbox{
	
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
	
	@Override
	void onRoomAdd(Room<V3D> to){
		this.onRoomAdd((Room3D)to);
	}
	
	/**
	 * Run when this thing enters a room, does nothing by default, provide custom implementation for behavior
	 * @param to The room this was added to
	 */
	public void onRoomAdd(Room3D to){}
	
	// TODO figure out if a door should have material
	@Override
	public Material getMaterial(){
		return Materials.NONE;
	}
	
	@Override
	public V3D getPosition(){
		return this.doorPos;
	}
	
	@Override
	public V3D getDimensions(){
		return this.doorDims;
	}
}
