package zusass.game.things.entities.mobs;

import static org.lwjgl.glfw.GLFW.*;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera3D;
import zgame.core.graphics.image.ImageManager;
import zgame.core.input.InputHandler;
import zgame.core.input.InputHandlers;
import zgame.core.input.InputType;
import zgame.core.sound.SoundManager;
import zgame.core.utils.ZMath;
import zgame.physics.ZVector3D;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zgame.stat.modifier.TypedModifier;
import zgame.things.entity.mobility.MobilityEntity3D;
import zgame.world.Room3D;
import zusass.ZusassDebugFlags;
import zusass.ZusassGame;
import zusass.game.magic.MultiSpell;
import zusass.game.magic.ProjectileSpell;
import zusass.game.magic.Spell;
import zusass.game.magic.effect.SpellEffectStatusEffect;
import zusass.game.status.StatEffect;
import zusass.game.things.ZusassTags;

import static zusass.game.stat.ZusassStat.*;

/** A player inside the {@link ZusassGame} */
public class ZusassPlayer extends ZusassMob{
	
	/** The type of movement the camera should have relative to the player */
	private enum CameraState{
		/** The camera is kept to the player's eye level */
		FIRST_PERSON(true, false, 0.05),
		/** The camera looks above and down onto the player */
		THIRD_PERSON(true, false, -1.3),
		/** The camera looks below and towards */
		THIRD_PERSON_REVERSE(true, true, -1.0),
		/** The camera is not controlled by the player */
		FREEZE_CAMERA(false, false, 0),
		/** The camera is separately controlled by input, irrespective of the player */
		FREE_CAM(false, false, 0),
		;
		
		/** true if this is a state where the camera should follow the player, false otherwise */
		private final boolean follow;
		
		/** true if the camera should look backwards, false otherwise */
		private final boolean reverse;
		
		/** Value to set {@link MobilityEntity3D#getVisionForwardDistance()} to */
		private final double forwardDistance;
		
		CameraState(boolean follow, boolean reverse, double forwardDistance){
			this.follow = follow;
			this.reverse = reverse;
			this.forwardDistance = forwardDistance;
		}
		/** @return See {@link #follow} */
		public boolean isFollow(){
			return this.follow;
		}
		
		/** @return See {@link #reverse} */
		public boolean isReverse(){
			return this.reverse;
		}
		
		/** @return See {@link #forwardDistance} */
		public double getForwardDistance(){
			return this.forwardDistance;
		}
	}
	
	/** The object tracking what is input used by the player */
	private InputHandlers inputHandlers;
	
	/** true if player input is disabled, false otherwise */
	private boolean inputDisabled;
	
	/** true if this {@link ZusassPlayer} is in spell casting mode, false for weapon mode */
	private boolean casting;
	
	/** the current way the camera should be positioned relative to the player */
	private CameraState cameraState;
	/** The state the camera was in last time */
	private CameraState previousCameraState;
	
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
		super(0, 0, 0, 0.2, 0.7);
		this.casting = false;
		this.previousCameraState = CameraState.FIRST_PERSON;
		this.cameraState = CameraState.FIRST_PERSON;
		
		this.inputDisabled = false;
		this.addTags(ZusassTags.CAN_ENTER_LEVEL_DOOR, ZusassTags.MUST_CLEAR_LEVEL_ROOM, ZusassTags.HUB_ENTER_RESTORE);
		
		this.defaultControls();
		
		this.setStat(STRENGTH, 10);
		this.setStat(ENDURANCE, 10);
		this.setStat(INTELLIGENCE, 30);
		this.setStat(ATTACK_SPEED, 3);
		this.setStat(STAMINA_REGEN, 5);
		this.setResourcesMax();
		
		// Set the default spell to a damage spell
		var spells = this.getSpells();
		spells.addSpell(Spell.projectileAdd(HEALTH, -10).named("Magic Ball"));
		spells.addSpell(Spell.selfEffect(MOVE_SPEED, 4, 2, ModifierType.MULT_MULT).named("Go Fast"));
		spells.addSpell(new MultiSpell(new MultiSpell(Spell.projectileAdd(HEALTH, -10),
				new ProjectileSpell(new SpellEffectStatusEffect(new StatEffect(5, new TypedModifier(new StatModifier(-10, ModifierType.ADD), HEALTH_REGEN))))),
				new MultiSpell(Spell.selfEffect(MOVE_SPEED, 2, 2, ModifierType.MULT_MULT))).named("Bruh"));
		spells.setSelectedSpellIndex(0);
	}
	
	/** Set the input buttons to be the default values */
	public void defaultControls(){
		// issue#27 avoid hard coding these individual values by using the settings system when it gets added
		this.inputHandlers = new InputHandlers(
				new InputHandler(InputType.MOUSE_BUTTONS, GLFW_MOUSE_BUTTON_LEFT),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_F10),
				new InputHandler(InputType.MOUSE_BUTTONS, GLFW_MOUSE_BUTTON_RIGHT),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_E),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_F),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_R),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_F8),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_F4),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_LEFT_BRACKET),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_RIGHT_BRACKET)
		);
	}
	
	@Override
	public void tick(double dt){
		super.tick(dt);
		
		if(!this.isInputDisabled()) this.checkInput(dt);
		
		var mobilityData = this.getMobilityData();
		
		// Move the camera to the player after repositioning the player
		/*
		 Doing this does leave a small amount of delay from frame to frame for the camera catching up,
		 rather than setting the camera before any drawing operations happen, but it somehow looks glitchier doing it the latter way
		 */
		var game = Game.get();
		this.updateCameraPos(game.getCamera3D());
		
		//issue#61
		// Update the sound listener to the player
		if(SoundManager.initialized()){
			var sm = SoundManager.get();
			sm.updateListenerPos(this.getX(), this.getY(), this.getZ());
			var soundVec = new ZVector3D(mobilityData.getFacingYaw(), mobilityData.getFacingPitch(), 1, false);
			sm.updateListenerDirection(soundVec.getX(), soundVec.getY(), soundVec.getZ());
		}
	}
	
	/**
	 * Perform any actions needed for player input
	 *
	 * @param dt The amount of time, in seconds, passed in the tick representing this input
	 */
	private void checkInput(double dt){
		var game = Game.get();
		var ki = game.getKeyInput();
		var left = ki.buttonDown(GLFW_KEY_A);
		var right = ki.buttonDown(GLFW_KEY_D);
		var forward = ki.buttonDown(GLFW_KEY_W);
		var backward = ki.buttonDown(GLFW_KEY_S);
		var up = ki.buttonDown(GLFW_KEY_Q);
		var down = ki.buttonDown(GLFW_KEY_Z);
		var cam = game.getCamera3D();
		
		// If reversed, also change left and right
		if(this.cameraState.isReverse()){
			left = !left;
			right = !right;
		}
		
		// If in free cam, move the camera instead
		if(this.cameraState == CameraState.FREE_CAM) this.handleFreeCam(dt, cam.getCurrentYaw(), cam.getCurrentPitch(), left, right, forward, backward, up, down);
		// Otherwise handle normal controls
		else this.handleMobilityControls(dt, cam.getYaw(), cam.getPitch(), left, right, forward, backward, up, down);
		
		// Turn sprinting on or off
		this.setSprinting(ki.buttonDown(GLFW_KEY_E));
		
		// Toggle casting or attacking
		if(this.inputHandlers.tick(GLFW_KEY_R)) this.toggleCasting();
		
		// Toggle camera perspectives
		if(this.inputHandlers.tick(GLFW_KEY_F)) {
			if(this.cameraState == CameraState.FIRST_PERSON) this.setCameraState(CameraState.THIRD_PERSON);
			else if(this.cameraState == CameraState.THIRD_PERSON) this.setCameraState(CameraState.THIRD_PERSON_REVERSE);
			else if(this.cameraState == CameraState.THIRD_PERSON_REVERSE) this.setCameraState(CameraState.FIRST_PERSON);
		}
		
		// Toggle following the camera
		if(this.inputHandlers.tick(GLFW_KEY_F8)) {
			if(this.cameraState == CameraState.FREEZE_CAMERA) this.setCameraState(this.previousCameraState);
			else this.setCameraState(CameraState.FREEZE_CAMERA);
		}
		
		// Enter free cam
		if(this.inputHandlers.tick(GLFW_KEY_F4)) {
			if(this.cameraState == CameraState.FREE_CAM) this.setCameraState(this.previousCameraState);
			else this.setCameraState(CameraState.FREE_CAM);
		}
		
		// Go to next or previous spell
		if(this.inputHandlers.tick(GLFW_KEY_RIGHT_BRACKET)) this.getSpells().previousSpell();
		if(this.inputHandlers.tick(GLFW_KEY_LEFT_BRACKET)) this.getSpells().nextSpell();
	}
	
	/** @param cameraState See {@link #cameraState} */
	private void setCameraState(CameraState cameraState){
		// Only change the previous position if it follows the player
		if(this.cameraState.isFollow()) this.previousCameraState = this.cameraState;
		this.cameraState = cameraState;
	}
	
	/**
	 * Update the position of the camera based on movement
	 *
	 * @param dt The amount of time passed during this movement update
	 * @param yaw The current yaw ongle of the camera
	 * @param pitch The current pitch ongle of the camera
	 * @param left true if moving to the left, false otherwise
	 * @param right true if moving to the right, false otherwise
	 * @param forward true if moving forward, false otherwise
	 * @param backward true if moving backwards, false otherwise
	 * @param up true if moving up, false otherwise
	 * @param down true if moving down, false otherwise
	 */
	private void handleFreeCam(double dt, double yaw, double pitch, boolean left, boolean right, boolean forward, boolean backward, boolean up, boolean down){
		// Do nothing if no movement
		if(left == right && forward == backward && up == down) return;
		
		var cam = Game.get().getCamera3D();
		
		// Have to subtract out half of pi because my engine is weird
		yaw -= ZMath.PI_BY_2;
		
		// If moving left or right, then move only on the x plane axis
		if(forward != backward){
			if(backward){
				if(left != right){
					if(left) yaw -= ZMath.PI_BY_4 + ZMath.PI_BY_2;
					else yaw += ZMath.PI_BY_4 + ZMath.PI_BY_2;
				}
				else yaw += Math.PI;
			}
			else{
				if(left != right){
					if(left) yaw -= ZMath.PI_BY_4;
					else yaw += ZMath.PI_BY_4;
				}
				else pitch += Math.PI;
			}
		}
		else if(left != right){
			if(left) yaw -= ZMath.PI_BY_2;
			else yaw += ZMath.PI_BY_2;
		}
		if(left != right) pitch = 0;
		
		// Force move up and down on pitch if those are pressed
		if(up != down){
			if(up) {
				if(left != right || forward != backward) pitch = ZMath.PI_BY_4;
				else pitch = ZMath.PI_BY_2;
			}
			else {
				if(left != right || forward != backward) pitch = -ZMath.PI_BY_4;
				else pitch = -ZMath.PI_BY_2;
			}
		}
		// If not moving up or down, but sprinting, force movement on the same axis
		else if(this.isSprinting()) pitch = 0;
		
		var movement = new ZVector3D(yaw, pitch, dt * 3, false);
		cam.addX(movement.getX());
		cam.addY(movement.getY());
		cam.addZ(movement.getZ());
	}
	
	/** See {@link GameInteractable#mouseAction(int, boolean, boolean, boolean, boolean)} */
	public boolean mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isInputDisabled()) return false;
		// Left click to interact with something on click
		if(!press && button == GLFW_MOUSE_BUTTON_LEFT){
			return ZusassGame.get().getCurrentRoom().attemptClick(this);
		}
		// Right click to attack in a direction
		else if(press && button == GLFW_MOUSE_BUTTON_RIGHT){
			if(casting) this.castSpell();
			else this.beginAttack();
			return true;
		}
		return false;
	}
	
	/** @return true if the button which means the player should go through a door, is pressed */
	public boolean isEnterRoomPressed(){
		if(this.isInputDisabled()) return false;
		return this.inputHandlers.pressed(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	@Override
	public void render(Renderer r){
		r.setColor(new ZColor(0.5));
		this.renderAttackTimer(r);
		
		// Billboard rendering of the player
		r.drawPlaneBufferSide(
				this.getX(), this.getY() + this.getHeight() * 0.5, this.getZ(), this.getWidth(), this.getHeight(),
				this.getMobilityData().getFacingYaw(),
				ImageManager.image("zusassPlayer").getId());
		
		if(ZusassDebugFlags.PLAYER_LOOK_RANGER_MARKER){
			var c = new ZColor(.5, 0, 0);
			var facing = new ZVector3D(this.getMobilityData().getFacingYaw(), this.getMobilityData().getFacingPitch(), this.getClickRange(), false);
			var sx = this.getClickX() + facing.getX();
			var sy = this.getClickY() + facing.getY();
			var sz = this.getClickZ() + facing.getZ();
			r.setColor(c);
			r.drawSphere(sx, sy, sz, 0.01);
		}
	}
	
	@Override
	public void die(){
		super.die();
		// Remove all non-persistent effects on death, as well as all modifiers, and restore resources and attributes
		this.getEffects().removeAllTemporary(this);
		var statArr = this.getStats().getArr();
		for(var s : statArr) s.reset();
		
		// Reset all constant modifiers
		this.initMobStatModifiers();
		
		// Put resources back to max
		this.setResourcesMax();
		
		// Put the player back in the hub
		var zgame = ZusassGame.get();
		zgame.getPlayState().enterHub();
		zgame.getData().checkAutoSave();
	}
	
	@Override
	public void enterRoom(Room3D from, Room3D to){
		super.enterRoom(from, to);
		if(to != null){
			// If this is setting the room to a new room, remove the player from that room, and set the new room
			var zgame = ZusassGame.get();
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
	}
	
	@Override
	public void updateCameraPos(GameCamera3D camera){
		this.setVisionForwardDistance(this.cameraState.getForwardDistance());
		if(cameraState.isReverse()) camera.setYawOffset(Math.PI);
		else camera.setYawOffset(0);
		
		if(this.cameraState.isFollow()) super.updateCameraPos(camera);
		else camera.setPositionOffset(this.cameraState.getForwardDistance());
	}
	
	/** @return See {@link #inputDisabled} */
	public boolean isInputDisabled(){
		return this.inputDisabled;
	}
	
	/** @param inputDisabled See {@link #inputDisabled} */
	public void setInputDisabled(boolean inputDisabled){
		this.inputDisabled = inputDisabled;
	}
	
	/** @return See {@link #casting} */
	public boolean isCasting(){
		return this.casting;
	}
	
	/** @param casting See {@link #casting} */
	public void setCasting(boolean casting){
		this.casting = casting;
	}
	
	/** Toggle the state of {@link #casting} */
	public void toggleCasting(){
		this.setCasting(!this.isCasting());
	}
	
}
