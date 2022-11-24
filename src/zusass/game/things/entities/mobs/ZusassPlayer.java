package zusass.game.things.entities.mobs;

import java.util.ArrayList;
import java.util.Collection;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;

import zgame.core.Game;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.ZRect;
import zgame.things.entity.EntityThing;
import zgame.things.entity.Player;
import zgame.things.type.GameThing;
import zusass.ZusassGame;
import zusass.game.things.ZusassDoor;

/** A player inside the {@link zusass.ZusassGame} */
public class ZusassPlayer extends Player implements StatThing{
	
	// issue#18
	/** true if the button for entering a room is currently pressed down, and releasing it will count as entering the input */
	private boolean enterRoomPressed;
	/** true if the button for toggling the camera being locked or not is currently pressed down, and it must be released before the next input */
	private boolean toggleCameraPressed;

	/** The status of this player */
	private Stats stats;
	
	/** Create a new default {@link ZusassPlayer} */
	public ZusassPlayer(){
		super(0, 0, 75, 125);
		this.enterRoomPressed = false;
		this.toggleCameraPressed = false;
		
		this.stats = new Stats();
		this.stats.setHealth(100);
	}
	
	@Override
	public void tick(Game game, double dt){
		ZusassGame zgame = (ZusassGame)game;
		this.tickStats(zgame, dt);
		super.tick(game, dt);

		ZRect pBounds = this.getBounds();
		handleDoorPress(zgame, pBounds);
		handleToggleCameraPress(zgame);

		// Right click to attack
		ZMouseInput mi = game.getMouseInput();
		if(mi.rightDown()){
			ArrayList<StatThing> statThings = zgame.getCurrentRoom().getStatThings();
			for(StatThing s : statThings){
				if(s == this || !s.get().getBounds().intersects(pBounds)) continue;
				s.getStats().setHealth(0);
			}
		}
	}
	
	/**
	 * Utility method for {@link #tick(Game, double)} for checking if the player clicked a door
	 * 
	 * @param game The game used by the tick method
	 * @param pBounds The current bounds of the player
	 * @return true if the player entered a door, false otherwise
	 */
	private boolean handleDoorPress(ZusassGame game, ZRect pBounds){
		// If the player clicks on a door, and the player is touching the door, enter it
		ZMouseInput mi = game.getMouseInput();
		if(!this.enterRoomPressed || mi.leftDown()){
			if(mi.leftDown()) this.enterRoomPressed = true;
			return false;
		}
		this.enterRoomPressed = false;
		
		Collection<GameThing> things = game.getCurrentRoom().getThings();
		for(GameThing t : things){
			if(!(t instanceof ZusassDoor)) continue;
			ZusassDoor d = (ZusassDoor)t;
			ZRect dBounds = d.getBounds();
			if(!dBounds.intersects(pBounds) || !dBounds.contains(game.mouseGX(), game.mouseGY())) continue;
			d.enterRoom(game.getCurrentRoom(), this, game);
			return true;
		}
		return false;
	}
	
	/**
	 * Utility method for {@link #tick(Game, double)} for checking if the player pressed the button to change if the camera is locked or not
	 * 
	 * @param game The game used by the tick method
	 * @return true if the camera was toggled, false otherwise
	 */
	private boolean handleToggleCameraPress(ZusassGame game){
		// Toggle the camera being locked to the player
		boolean pressed = game.getKeyInput().buttonDown(GLFW_KEY_F10);
		if(!this.toggleCameraPressed && pressed){
			this.toggleCameraPressed = true;
			this.setLockCamera(!this.isLockCamera());
			return true;
		}
		if(!pressed) this.toggleCameraPressed = false;
		return false;
	}
	
	@Override
	public Stats getStats(){
		return this.stats;
	}
	
	@Override
	public EntityThing get(){
		return this;
	}
	
}
