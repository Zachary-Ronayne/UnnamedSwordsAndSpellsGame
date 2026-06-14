package zgame.things.still.door;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.V3D;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing3D;
import zgame.things.type.bounds.RectPrismClickable;
import zgame.world.Room;
import zgame.world.Room3D;

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
	public V3D getPosition(){
		// TODO implement
	}
	
	@Override
	public V3D getDimensions(){
		// TODO implement
	}
}
