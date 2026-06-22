package zgame.things.core;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V3D;
import zgame.things.still.door.DoorState3D;
import zgame.things.type.bounds.RectPrismClickable;

// TODO all static values should be in Door, all mutable data should be in DoorState

// TODO how should doors vs door state be able to access positions?
public class Door3D extends Door<V3D, DoorState3D> implements RectPrismClickable{
	
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
}
