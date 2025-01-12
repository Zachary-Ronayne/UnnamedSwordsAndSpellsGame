package zusass.game.things;

import zgame.core.Game;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.physics.ZVector3D;
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
	 *
	 * @return true if the door was entered, false otherwise
	 */
	@Override
	public boolean handleZusassPress(ZusassGame zgame, Room3D room){
		var player = zgame.getPlayer();
		return this.enterRoom(zgame.getCurrentRoom(), player, zgame);
	}
	
	@Override
	public double getMaxClickRange(){
		// TODO implement this based on the player or something
		return 1;
	}
	
	@Override
	public boolean enterRoom(Room3D r, EntityThing3D thing, Game game){
		return super.enterRoom(r, thing, game);
	}
	
	/** Convenience method that calls {@link #enterRoom(Room3D, EntityThing3D, Game)} without a need to type cast */
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing, Game game){
		return this.enterRoom((Room3D)r, thing, game);
	}
	
	
	
	
	
	// TODO make a way proper of just checking if something can be clicked on without actually clicking on it
	private boolean hover = false;
	private boolean near = false;
	
	@Override
	public void tick(Game game, double dt){
		var player = ((ZusassGame)game).getPlayer();
		var clickDirection = new ZVector3D(player.getMobilityData().getFacingHorizontalAngle(), player.getMobilityData().getFacingVerticalAngle(), 1, false);
		var dist = this.rayDistance(player.getX(), player.getY() + player.getHeight(), player.getZ(),
				clickDirection.getX(), clickDirection.getY(), clickDirection.getZ());
		hover = dist >= 0;
		near = hover && dist <= this.getMaxClickRange();
	}
	
	@Override
	public void render(Game game, Renderer r){
		var c = near ? new ZColor(1, 0, 0) : hover ? new ZColor(0, 0, 1) : new ZColor(0.35, 0.22, 0);
		r.drawRectPrism(new RectRender3D(this.getBounds()), c, c, c, c, c, c);
	}
	
}
