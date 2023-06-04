package zusass.game.things.entities.mobs;

import static org.lwjgl.glfw.GLFW.*;

import com.google.gson.JsonElement;
import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.graphics.Renderer;
import zgame.core.input.InputHandler;
import zgame.core.input.InputHandlers;
import zgame.core.input.InputType;
import zgame.core.utils.ZMath;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zgame.stat.modifier.TypedModifier;
import zgame.world.Room;
import zusass.ZusassGame;
import zusass.game.magic.MultiSpell;
import zusass.game.magic.ProjectileSpell;
import zusass.game.magic.Spell;
import zusass.game.magic.effect.SpellEffectStatusEffect;
import zusass.game.status.StatEffect;
import zusass.game.things.ZThingClickDetector;
import zusass.game.things.ZusassDoor;
import zusass.game.things.ZusassTags;

import static zusass.game.stat.ZusassStat.*;

/** A player inside the {@link ZusassGame} */
public class ZusassPlayer extends ZusassMob{
	
	/** true to lock the camera to the center of the player, false otherwise */
	private boolean lockCamera;
	
	/** The object tracking what is input used by the player */
	private InputHandlers inputHandlers;
	
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
		
		this.defaultControls();
		
		this.setStat(STRENGTH, 10);
		this.setStat(ENDURANCE, 10);
		this.setStat(INTELLIGENCE, 30);
		this.setStat(ATTACK_SPEED, .3);
		this.setResourcesMax();
		
		this.addStatEffect(this.getUuid(), -1, 5, ModifierType.ADD, STAMINA_REGEN);
		
		// Set the default spell to a damage spell
		var spells = this.getSpells();
		spells.addSpell(Spell.projectileAdd(HEALTH, -10).named("Magic Ball"));
		spells.addSpell(Spell.selfEffect(MOVE_SPEED, 4, 2, ModifierType.MULT_MULT).named("Go Fast"));
		spells.addSpell(new MultiSpell(new MultiSpell(Spell.projectileAdd(HEALTH, -10),
				new ProjectileSpell(new SpellEffectStatusEffect(new StatEffect(5, new TypedModifier(new StatModifier(-10, ModifierType.ADD), HEALTH_REGEN))))),
				new MultiSpell(Spell.selfEffect(MOVE_SPEED, 2, 2, ModifierType.MULT_MULT))).named("Bruh"));
		spells.setSelectedSpellIndex(0);
		
		this.lockCamera = false;
	}
	
	/** Set the input buttons to be the default values */
	public void defaultControls(){
		// issue#27 avoid hard coding these individual values by using the settings system when it gets added
		this.inputHandlers = new InputHandlers(
				new InputHandler(InputType.MOUSE_BUTTONS, GLFW_MOUSE_BUTTON_LEFT),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_F10),
				new InputHandler(InputType.MOUSE_BUTTONS, GLFW_MOUSE_BUTTON_RIGHT),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_SPACE),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_Q),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_LEFT_BRACKET),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_RIGHT_BRACKET)
		);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		
		var ki = game.getKeyInput();
		this.getWalk().handleMovementControls(ki.pressed(GLFW_KEY_A), ki.pressed(GLFW_KEY_D), ki.pressed(GLFW_KEY_W), dt);
		
		if(this.inputHandlers.tick(game, GLFW_KEY_F10)) this.setLockCamera(!this.isLockCamera());
		
		// Toggle walking
		if(this.inputHandlers.tick(game, GLFW_KEY_SPACE)) this.getWalk().toggleWalking();
		
		// Toggle casting or attacking
		if(this.inputHandlers.tick(game, GLFW_KEY_Q)) this.toggleCasting();
		
		// Go to next or previous spell
		if(this.inputHandlers.tick(game, GLFW_KEY_RIGHT_BRACKET)) this.getSpells().previousSpell();
		if(this.inputHandlers.tick(game, GLFW_KEY_LEFT_BRACKET)) this.getSpells().nextSpell();
		
		// Now the camera to the player after repositioning the player
		this.checkCenterCamera(game);
	}
	
	/** See {@link GameInteractable#mouseAction(Game, int, boolean, boolean, boolean, boolean)} */
	public boolean mouseAction(ZusassGame zgame, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		// Left click to interact with something on click
		if(!press && button == GLFW_MOUSE_BUTTON_LEFT) {
			var clickables = zgame.getCurrentRoom().getAllThings().get(ZThingClickDetector.class);
			if(clickables != null){
				for(var c : clickables){
					if(c.handlePress(zgame)) return true;
				}
			}
			return false;
		}
		// Right click to attack in a direction
		else if(press && button == GLFW_MOUSE_BUTTON_RIGHT) {
			this.beginAttackOrSpell(zgame, ZMath.lineAngle(this.centerX(), this.centerY(), zgame.mouseGX(), zgame.mouseGY()));
			return true;
		}
		return false;
	}
	
	/** @return true if the button which means the player should go through a door, is pressed */
	public boolean isEnterRoomPressed(){
		return this.inputHandlers.pressed(GLFW_MOUSE_BUTTON_LEFT);
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
		zgame.getData().checkAutoSave(zgame);
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
