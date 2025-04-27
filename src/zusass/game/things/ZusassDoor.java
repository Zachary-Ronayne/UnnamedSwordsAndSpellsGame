package zusass.game.things;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZConfig;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zgame.things.still.Door3D;
import zgame.world.Direction3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door3D implements ZThingClickDetector{
	
	/** The direction this door should be facing towards */
	private final Direction3D facingDirection;
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param direction The facing direction of the door, must be one of the cardinal directions
	 */
	public ZusassDoor(double x, double y, double z, Direction3D direction){
		super(x, y, z, 1, 1, 1);
		if(!direction.isCardinal()){
			direction = Direction3D.NORTH;
			ZConfig.error("ZusassDoor cannot use non cardinal direction ", direction.name(), " defaulting to ", direction.name());
		}
		this.facingDirection = direction;
		// Set width and height based on direction
		double longSide = 0.5;
		double shortSide = 0.1;
		if(direction == Direction3D.NORTH || direction == Direction3D.SOUTH){
			this.setWidth(longSide);
			this.setLength(shortSide);
		}
		else{
			this.setWidth(shortSide);
			this.setLength(longSide);
		}
	}
	
	/**
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 *
	 * @param room The room used by the tick method
	 */
	@Override
	public void handleZusassPress(ZusassRoom room){
		var zgame = ZusassGame.get();
		var player = zgame.getPlayer();
		this.enterRoom(zgame.getCurrentRoom(), player);
	}
	
	@Override
	public boolean enterRoom(Room3D r, EntityThing3D thing){
		return super.enterRoom(r, thing);
	}
	
	/** Convenience method that calls {@link #enterRoom(Room3D, EntityThing3D)} without a need to type cast */
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing){
		return this.enterRoom((Room3D)r, thing);
	}
	
	/** @return See {@link #facingDirection} */
	public Direction3D getFacingDirection(){
		return this.facingDirection;
	}
	
	@Override
	public void render(Renderer r){
		var zgame = ZusassGame.get();
		double clickDistance = this.findClickDistance(zgame.getPlayer());
		double maxClickRange = zgame.getPlayer().getClickRange();
		
		// Check for tiles
		double tileDistance = -1;
		if(clickDistance >= 0){
			var room = zgame.getCurrentRoom();
			if(room != null) tileDistance = room.findTileClickDistance(zgame.getPlayer());
		}
		
		boolean canClick = clickDistance <= maxClickRange && clickDistance >= 0 && (tileDistance < 0 || tileDistance > clickDistance);
		var c = canClick ? new ZColor(0.2, 0.14, 0) : new ZColor(0.35, 0.22, 0);
		r.drawRectPrism(new RectRender3D(this.getBounds()), c, c, c, c, c, c);
	}
	
}
