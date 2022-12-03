package zusass.game.things.entities.mobs;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.input.mouse.ZMouseInput;
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
		
		this.getStats().setMaxHealth(100);
		this.healToMaxHealth();
		this.getStats().setStrength(10);
		this.lockCamera = false;
	}
	
	@Override
	public void tick(Game game, double dt){
		ZusassGame zgame = (ZusassGame)game;
		
		this.handleMovementControls(zgame, dt);
		
		super.tick(game, dt);
		
		handleDoorPress(zgame);
		handleToggleCameraPress(zgame);
		
		// Right click to attack
		ZMouseInput mi = game.getMouseInput();
		boolean rightPressed = mi.rightDown();
		if(rightPressed) this.attackPressed = true;
		else if(this.attackPressed && !rightPressed){
			this.attackPressed = false;
			this.attackNearest(zgame);
		}
		
		// Now the camera to the player after repositioning the player
		this.checkCenterCamera(game);
	}
	
	public void handleMovementControls(Game game, double dt){
		// Move left and right
		ZKeyInput ki = game.getKeyInput();
		if(ki.buttonDown(GLFW_KEY_LEFT)) this.walkLeft();
		else if(ki.buttonDown(GLFW_KEY_RIGHT)) this.walkRight();
		else this.stopWalking();
		
		// Jump if holding the jump button
		if(ki.buttonDown(GLFW_KEY_UP)) this.jump(dt);
		// For not holding the button
		else{
			// if jumps should be instant, or no jump time is being built up, then stop the jump
			if(this.jumpsAreInstant() || this.getJumpTimeBuilt() == 0) this.stopJump();
			// Otherwise, perform the built up jump
			else this.jumpFromBuiltUp(dt);
			
		}
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
	}
	
	/**
	 * If the camera should be locked to this {@link Player}, then lock the camera, otherwise do nothing
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
