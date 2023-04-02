package zusass.game.things.entities.mobs;

import static org.lwjgl.glfw.GLFW.*;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.NotNullList;
import zgame.core.utils.ZMath;
import zgame.stat.modifier.ModifierType;
import zgame.world.Room;
import zusass.ZusassGame;
import zusass.game.magic.MultiSpell;
import zusass.game.magic.Spell;
import zusass.game.things.ZusassTags;

import static zusass.game.stat.ZusassStat.*;

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
	/** true if the button for toggling walking and running is currently pressed down, and releasing it will toggle walking and running */
	private boolean walkPressed;
	/** true if the button for toggling between casting a spell and attacking is currently pressed down, and releasing it will toggle */
	private boolean toggleSelectedAction;
	
	/**
	 * Create a new object from json
	 *
	 * @param e The json
	 */
	public ZusassPlayer(JsonElement e){
		this();
		this.load(e);
	}
	
	/** Create a new default {@link ZusassPlayer} */
	public ZusassPlayer(){
		super(0, 0, 75, 125);
		this.addTags(ZusassTags.CAN_ENTER_LEVEL_DOOR, ZusassTags.MUST_CLEAR_LEVEL_ROOM, ZusassTags.HUB_ENTER_RESTORE);
		
		this.enterRoomPressed = false;
		this.toggleCameraPressed = false;
		this.attackPressed = false;
		this.walkPressed = false;
		this.toggleSelectedAction = false;
		
		this.setStat(STRENGTH, 10);
		this.setStat(ENDURANCE, 10);
		this.setStat(INTELLIGENCE, 10);
		this.setStat(ATTACK_SPEED, .3);
		this.setResourcesMax();
		
		this.addStatEffect(this.getUuid(), -1, 5, ModifierType.ADD, STAMINA_REGEN);
		
		// Set the default spell to a damage spell
//		this.setSelectedSpell(Spell.projectileAdd(HEALTH, -10));
//		this.setSelectedSpell(Spell.selfEffect(MOVE_SPEED, 4, 2, ModifierType.MULT_MULT));
		var spells = new NotNullList<Spell>();
		spells.add(Spell.projectileAdd(HEALTH, -10));
		spells.add(Spell.selfEffect(MOVE_SPEED, 1.5, 5, ModifierType.MULT_MULT));
		this.setSelectedSpell(new MultiSpell(spells));
		
		this.lockCamera = false;
	}
	
	@Override
	public void tick(Game game, double dt){
		ZusassGame zgame = (ZusassGame)game;
		
		super.tick(game, dt);
		
		var ki = game.getKeyInput();
		this.getWalk().handleMovementControls(ki.pressed(GLFW_KEY_A), ki.pressed(GLFW_KEY_D), ki.pressed(GLFW_KEY_W), dt);
		
		handleDoorPress(zgame);
		handleToggleCameraPress(zgame);
		
		// Right click to attack in a direction
		ZMouseInput mi = game.getMouseInput();
		boolean rightPressed = mi.rightDown();
		if(this.attackPressed && !rightPressed){
			this.attackPressed = false;
			this.beginAttackOrSpell(zgame, ZMath.lineAngle(this.centerX(), this.centerY(), game.mouseGX(), game.mouseGY()));
		}
		if(rightPressed) this.attackPressed = true;
		
		var walkPressed = ki.pressed(GLFW_KEY_SPACE);
		if(this.walkPressed && !walkPressed){
			this.walkPressed = false;
			this.getWalk().toggleWalking();
		}
		if(walkPressed) this.walkPressed = true;
		
		var castPressed = ki.pressed(GLFW_KEY_Q);
		if(this.toggleSelectedAction && !castPressed){
			this.toggleSelectedAction = false;
			this.toggleCasting();
		}
		if(castPressed) this.toggleSelectedAction = true;
		
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
	public void die(ZusassGame zgame){
		super.die(zgame);
		zgame.getPlayState().enterHub(zgame);
	}
	
	@Override
	public void enterRoom(Room from, Room to, Game game){
		ZusassGame zgame = (ZusassGame)game;
		super.enterRoom(from, to, zgame);
		if(to != null){
			// If this is setting the room to a new room, remove the player from that room, and set the new room
			if(zgame.getCurrentRoom() != to){
				var play = zgame.getPlayState();
				if(play != null) play.setCurrentRoom(to);
			}
			
			// If the player is going through a level, heal to full, and remove all effects
			if(to.hasTag(ZusassTags.IS_LEVEL)){
				this.setResourcesMax();
				this.getEffects().removeAllTemporary(this);
			}
		}
		
		// Center the camera to the player
		this.checkCenterCamera(zgame);
	}
	
}
