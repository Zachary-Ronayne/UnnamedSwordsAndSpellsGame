package zgame.things.still.door;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.V2D;
import zgame.things.entity.EntityThing;
import zgame.things.entity.EntityThing2D;
import zgame.world.Room;
import zgame.world.Room2D;

// TODO add docs
public class Door2D extends Door<V2D, DoorState2D>{
	
	@Override
	public void tick(double dt){
		if(!this.getCurrent().isAutoEnter()) return;
		
		// Check every entity and if it touches this door, move it to this Room
		var room = (Room2D)Game.get().getCurrentRoom();
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
}
