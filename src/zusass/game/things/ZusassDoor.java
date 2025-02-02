package zusass.game.things;

import zgame.core.Game;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zgame.things.still.Door3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door3D implements ZThingClickDetector{
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 */
	public ZusassDoor(double x, double y, double z){
		super(x, y, z, 0.5, 1, 0.1);
	}
	
	/**
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 *
	 * @param zgame The game used by the tick method
	 * @param room The room used by the tick method
	 */
	@Override
	public void handleZusassPress(ZusassGame zgame, ZusassRoom room){
		var player = zgame.getPlayer();
		this.enterRoom(zgame.getCurrentRoom(), player, zgame);
	}
	
	@Override
	public boolean enterRoom(Room3D r, EntityThing3D thing, Game game){
		return super.enterRoom(r, thing, game);
	}
	
	/** Convenience method that calls {@link #enterRoom(Room3D, EntityThing3D, Game)} without a need to type cast */
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing, Game game){
		return this.enterRoom((Room3D)r, thing, game);
	}
	
	@Override
	public void render(Game game, Renderer r){
		var zgame = (ZusassGame)game;
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
