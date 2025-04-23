package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.NotNullList;
import zgame.core.utils.ZMath;
import zgame.stat.modifier.ModifierType;
import zusass.ZusassGame;
import zusass.game.magic.ProjectileSpell;
import zusass.game.magic.Spell;
import zusass.game.magic.effect.SpellEffectStatAdd;
import zusass.game.stat.ZusassStat;

import static zusass.game.stat.ZusassStat.*;

/** A generic mob which uses health, status, etc, and is not a player */
public class Npc extends ZusassMob{
	
	/** The amount of time, in seconds since the last spell cast */
	private double spellTime;
	
	/**
	 * Create a new Npc with the given bounds
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param z See {@link #getZ()}
	 * @param radius See {@link #radius}
	 * @param height See {@link #height}
	 */
	public Npc(double x, double y, double z, double radius, double height){
		super(x, y, z, radius, height);
		
		this.setStat(MOVE_SPEED, 0.6);
		this.setStat(ATTACK_SPEED, 1);
		this.setStat(STAMINA_REGEN, 1);
		this.setStat(ENDURANCE, 4);
		this.setStat(INTELLIGENCE, 5);
		
		// Set a default spell as speed
		this.getSpells().addSpell(Spell.selfEffect(MOVE_SPEED, 1.5, 1, ModifierType.MULT_MULT).named("Small Speed"));
		var hurtSpell = new ProjectileSpell(new NotNullList<>(new SpellEffectStatAdd(HEALTH, -10)), 0.2, 1.5, 0.6).named("hurt");
		this.getSpells().addSpell(hurtSpell);
		this.getSpells().setSelectedSpellIndex(0);
		
		this.spellTime = 0;
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		double playerDx = player.getX() - this.getX();
		double playerDy = (player.getY() + player.getHeight() * 0.5) - (this.getY() + this.getHeight());
		double playerDz = player.getZ() - this.getZ();
		double playerDist = Math.sqrt(playerDx * playerDx + playerDz * playerDz);
		double playerYaw = ZMath.PI_BY_2 + ZMath.atan2Normalized(playerDz, playerDx);
		double playerPitch = -ZMath.atan2Normalized(playerDy, playerDist);
		
		// Simplistic ai to move to the player
		boolean inRange = playerDist < this.stat(ATTACK_RANGE) * 0.9;
		this.handleMobilityControls(dt, playerYaw, playerPitch, false, false, !inRange, false, false, false);
		// Stop moving once close enough
		if(inRange) this.stopWalking();
		
		// If the AI has an attack available, and stamina is at least 75%, begin attacking
		var staminaPerc = this.currentStaminaPerc();
		if(this.getAttackTime() <= 0 && inRange && staminaPerc > .75){
			this.beginAttack(zgame);
		}
		
		// If not in range, use the speed spell, otherwise use the damage spell
		if(!inRange) this.getSpells().setSelectedSpellIndex(0);
		else this.getSpells().setSelectedSpellIndex(1);
		
		this.spellTime += dt;
		
		// Try to cast a spell if enough time has passed
		double intelligence = this.stat(INTELLIGENCE);
		if(intelligence > 0 && this.spellTime > 10 / intelligence) this.castSpell(zgame);
		
		// If sprinting and stamina is low, stop sprinting
		var sprinting = this.isSprinting();
		if(staminaPerc < .25 && sprinting || inRange) this.setSprinting(false);
		// If stamina is above 75% and not sprinting, start sprinting
		else if(staminaPerc > .75 && !sprinting) this.setSprinting(true);
	}
	
	@Override
	public boolean castSpell(ZusassGame zgame){
		var success = super.castSpell(zgame);
		if(success) this.spellTime = 0;
		return success;
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// Draw an attack timer
		r.setColor(0, 0.7, 0);
		this.renderAttackTimer(game, r);
		
		double facingAngle = this.getMobilityData().getFacingYaw() + ZMath.PI_BY_2;
		
		// Render a billboard texture
		r.drawPlaneBufferSide(
				this.getX(), this.getY() + this.getHeight() * 0.5, this.getZ(), this.getWidth(), this.getHeight(),
				// TODO why does this need to be negative facing angle?
				-facingAngle, game.getImage("goblin").getId());
		
		// issue#23 make a way of drawing a health bar above the mob, accounting for how this health bar will not be a part of the mob itself, but above it
		
		// Draw bars to represent its remaining health, stamina, and mana

		var zgame = (ZusassGame)game;
		this.drawResourceBar(r, zgame, ZusassStat.HEALTH, ZusassStat.HEALTH_MAX, 0, new ZColor(1.5, 0, 0));
		this.drawResourceBar(r, zgame, ZusassStat.STAMINA, ZusassStat.STAMINA_MAX, 1, new ZColor(0, 1, 0));
		this.drawResourceBar(r, zgame, ZusassStat.MANA, ZusassStat.MANA_MAX, 2, new ZColor(0, 0, 1.5));
	}
	
	private void drawResourceBar(Renderer r, ZusassGame zgame, ZusassStat current, ZusassStat max, int index, ZColor color){
		var c = this.stat(current);
		var m = this.stat(max);
		double facingAngle = this.getMobilityData().getFacingYaw() + ZMath.PI_BY_2;
		double space = 0.032;
		
		r.pushTextureTintShader();
		// TODO probably abstract out some of this rendering
		double width = this.getWidth() * 1.2;
		double height = 0.03;
		double textureSize = 0.1;
		double shiftX = ((System.currentTimeMillis() / 14000.0) % width) / width + index * index * 0.5;
		double shiftY = ((System.currentTimeMillis() / 300000.0) % height) / height + index * index * 0.2;
		
		// TODO make the health bars line up on the left, not centered
		
		// TODO add the border to the health bars? Render the health bar to a buffer with the border, then render the buffer
//		double barOffset = 0.005;
//		r.setColor(.5, .5, .5);
//		r.drawRepeatingPlaneBuffer(
//				this.getX() + barOffset, this.getY() + this.getHeight() + 0.09 - index * 0.03, this.getZ() + barOffset, this.getWidth() + barOffset * 2, height + barOffset * 2,
//				-facingAngle, -ZMath.PI_BY_2, 0, 0, 0, 0, false,
//				textureSize, textureSize, shiftX, shiftY,
//				zgame.getImage("resourceBar").getId());
		
		r.setColor(color);
		
		r.drawRepeatingPlaneBufferSide(
				this.getX(), this.getY() + this.getHeight() + 0.095 - index * space, this.getZ(), this.getWidth() * c / m, height, -facingAngle,
				// TODO fix broken texture scaling
				textureSize, textureSize, shiftX, shiftY,
				zgame.getImage("resourceBar").getId());
		
		r.popShader();
	}
	
}
