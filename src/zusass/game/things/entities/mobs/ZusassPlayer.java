package zusass.game.things.entities.mobs;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.ZMath;
import zusass.game.stat.Stats;
import zgame.world.Room;
import zusass.ZusassGame;

/** A player inside the {@link ZusassGame} */
public class ZusassPlayer extends ZusassMob{
	
	/** true to lock the camera to the center of the player, false otherwise */
	private boolean lockCamera;
	
	// issue#18
	/** true if the button for entering a room is currently pressed down, and releasing it will count as entering the input */
	private boolean enterRoomPressed;
	/** true if the button for toggling the camera being locked or not is currently pressed down, and it must be released before the next input */
	private boolean toggleCameraPressed;
	/** true if the button for attacking is currently pressed down, and releasing it will perform an attack */
	private boolean attackPressed;
	
	/** Create a new default {@link ZusassPlayer} */
	public ZusassPlayer(){
		super(0, 0, 75, 125);
		this.enterRoomPressed = false;
		this.toggleCameraPressed = false;
		this.attackPressed = false;
		
		Stats s = this.getStats();
		s.setMaxHealth(100);
		this.healToMaxHealth();
		s.setStrength(10);
		s.setAttackSpeed(.3);
		
		this.lockCamera = false;
	}
	
	@Override
	public void tick(Game game, double dt){
		ZusassGame zgame = (ZusassGame)game;
		
		super.tick(game, dt);
		
		var ki = game.getKeyInput();
		this.getWalk().handleMovementControls(ki.pressed(GLFW_KEY_LEFT), ki.pressed(GLFW_KEY_RIGHT), ki.pressed(GLFW_KEY_UP), dt);
		
		handleDoorPress(zgame);
		handleToggleCameraPress(zgame);
		
		// Right click to attack in a direction
		ZMouseInput mi = game.getMouseInput();
		boolean rightPressed = mi.rightDown();
		if(rightPressed) this.attackPressed = true;
		if(this.attackPressed){
			this.attackPressed = false;
			this.beginAttack(ZMath.lineAngle(this.centerX(), this.centerY(), game.mouseGX(), game.mouseGY()));
		}
		
		// Now the camera to the player after repositioning the player
		this.checkCenterCamera(game);
	}
	
	/**
	 * Utility method for {@link #tick(Game, double)} for checking if the player clicked a door
	 *
	 * @param game The game used by the tick method
	 */
	private void handleDoorPress(ZusassGame game){
		// If the player clicks on a door, and the player is touching the door, mark it as attempting to enter a door
		ZMouseInput mi = game.getMouseInput();
		if(!this.enterRoomPressed || mi.leftDown()){
			if(mi.leftDown()) this.enterRoomPressed = true;
			return;
		}
		this.enterRoomPressed = false;
	}
	
	/** @return See {@link #enterRoomPressed} */
	public boolean isEnterRoomPressed(){
		return this.enterRoomPressed;
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
	public void render(Game game, Renderer r){
		r.setColor(0, 0, .5);
		r.drawRectangle(this.getBounds());
		this.renderAttackTimer(game, r);
	}
	
	/**
	 * If the camera should be locked to this {@link ZusassPlayer}, then lock the camera, otherwise do nothing
	 *
	 * @param game The game to get the camera from
	 */
	public void checkCenterCamera(Game game){
		if(this.isLockCamera()) this.centerCamera(game);
	}
	
	/** @return See {@link #lockCamera} */
	public boolean isLockCamera(){
		return this.lockCamera;
	}
	
	/** @param lockCamera See {@link #lockCamera} */
	public void setLockCamera(boolean lockCamera){
		this.lockCamera = lockCamera;
	}
	
	/** If the camera is locked, unlock it, otherwise, lock it */
	public void toggleLockCamera(){
		this.setLockCamera(!this.isLockCamera());
	}
	
	@Override
	public void die(Game game){
		super.die(game);
		ZusassGame zgame = (ZusassGame)game;
		zgame.getPlayState().enterHub(zgame);
	}
	
	@Override
	public void enterRoom(Room from, Room to, Game game){
		ZusassGame zgame = (ZusassGame)game;
		super.enterRoom(from, to, zgame);
		if(to != null){
			// If this is setting the room to a new room, remove the player from that room, and set the new room
			if(zgame.getCurrentRoom() != to){
				zgame.getCurrentRoom().setPlayer(null);
				zgame.getPlayState().setCurrentRoom(to);
			}
			zgame.getCurrentRoom().setPlayer(this);
		}
		// Center the camera to the player
		this.checkCenterCamera(zgame);
	}
	
	@Override
	public ZusassPlayer asPlayer(){
		return this;
	}
	
}
