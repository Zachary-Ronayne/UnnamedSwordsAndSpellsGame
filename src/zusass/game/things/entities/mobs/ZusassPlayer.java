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
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.magic.MultiSpell;
import zusass.game.magic.ProjectileSpell;
import zusass.game.magic.Spell;
import zusass.game.magic.effect.SpellEffectStatusEffect;
import zusass.game.status.StatEffect;
import zusass.game.things.ZThingClickDetector;
import zusass.game.things.ZusassTags;

import static zusass.game.stat.ZusassStat.*;

/** A player inside the {@link ZusassGame} */
public class ZusassPlayer extends ZusassMob{
	
	/** The object tracking what is input used by the player */
	private InputHandlers inputHandlers;
	
	/** true if player input is disabled, false otherwise */
	private boolean inputDisabled;
	
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
		super(0, 0, 0, 0.15, 0.5);
		this.inputDisabled = false;
		this.addTags(ZusassTags.CAN_ENTER_LEVEL_DOOR, ZusassTags.MUST_CLEAR_LEVEL_ROOM, ZusassTags.HUB_ENTER_RESTORE);
		
		this.defaultControls();
		
		this.setStat(STRENGTH, 10);
		this.setStat(ENDURANCE, 10);
		this.setStat(INTELLIGENCE, 30);
		this.setStat(ATTACK_SPEED, 3);
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
	}
	
	/** Set the input buttons to be the default values */
	public void defaultControls(){
		// issue#27 avoid hard coding these individual values by using the settings system when it gets added
		this.inputHandlers = new InputHandlers(
				new InputHandler(InputType.MOUSE_BUTTONS, GLFW_MOUSE_BUTTON_LEFT),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_F10),
				new InputHandler(InputType.MOUSE_BUTTONS, GLFW_MOUSE_BUTTON_RIGHT),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_E),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_R),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_LEFT_BRACKET),
				new InputHandler(InputType.KEYBOARD, GLFW_KEY_RIGHT_BRACKET)
		);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		
		if(!this.isInputDisabled()) this.checkInput(game, dt);
		
		// Move the camera to the player after repositioning the player
		this.updateCameraPos(game.getCamera3D());
	}
	
	/**
	 * Perform any actions needed for player input
	 * @param game The game the input took place in
	 * @param dt The amount of time, in seconds, passed in the tick representing this input
	 */
	private void checkInput(Game game, double dt){
		var ki = game.getKeyInput();
		var left = ki.buttonDown(GLFW_KEY_A);
		var right = ki.buttonDown(GLFW_KEY_D);
		var forward = ki.buttonDown(GLFW_KEY_W);
		var backward = ki.buttonDown(GLFW_KEY_S);
		var up = ki.buttonDown(GLFW_KEY_Q);
		var down = ki.buttonDown(GLFW_KEY_Z);
		var cam = game.getCamera3D();
		// TODO fix walking being glitchy
		this.handleMobilityControls(dt, cam.getRotY(), cam.getRotX(), left, right, forward, backward, up, down);
		
		// Toggle walking
		if(this.inputHandlers.tick(game, GLFW_KEY_E)) this.toggleWalking();
		
		// Toggle casting or attacking
		if(this.inputHandlers.tick(game, GLFW_KEY_R)) this.toggleCasting();
		
		// Go to next or previous spell
		if(this.inputHandlers.tick(game, GLFW_KEY_RIGHT_BRACKET)) this.getSpells().previousSpell();
		if(this.inputHandlers.tick(game, GLFW_KEY_LEFT_BRACKET)) this.getSpells().nextSpell();
	}
	
	/** See {@link GameInteractable#mouseAction(Game, int, boolean, boolean, boolean, boolean)} */
	public boolean mouseAction(ZusassGame zgame, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		if(this.isInputDisabled()) return false;
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
		if(this.isInputDisabled()) return false;
		return this.inputHandlers.pressed(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	@Override
	public void render(Game game, Renderer r){
		// TODO decide what to render for the player
//		r.setColor(0, 0, .5);
//		r.drawRectangle(this.getBounds());
//		this.renderAttackTimer(game, r);
	}
	
	@Override
	public void die(ZusassGame zgame){
		super.die(zgame);
		// Remove all non-persistent effects on death, as well as all modifiers, and restore resources and attributes
		this.getEffects().removeAllTemporary(this);
		var statArr = this.getStats().getArr();
		for(var s : statArr) s.reset();
		
		this.setResourcesMax();
		
		zgame.getPlayState().enterHub(zgame);
		zgame.getData().checkAutoSave(zgame);
	}
	
	@Override
	public void enterRoom(Room3D from, Room3D to, Game game){
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
	}
	
	/** @return See {@link #inputDisabled} */
	public boolean isInputDisabled(){
		return this.inputDisabled;
	}
	
	/** @param inputDisabled See {@link #inputDisabled} */
	public void setInputDisabled(boolean inputDisabled){
		this.inputDisabled = inputDisabled;
	}
}
