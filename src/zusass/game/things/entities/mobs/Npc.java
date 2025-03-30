package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZMath;
import zgame.stat.modifier.ModifierType;
import zusass.ZusassGame;
import zusass.game.magic.Spell;

import static zusass.game.stat.ZusassStat.*;

/** A generic mob which uses health, status, etc, and is not a player */
public class Npc extends ZusassMob{
	
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
		this.setStat(INTELLIGENCE, 0.5);
		
		// Set a default spell as speed
		this.getSpells().addSpell(Spell.selfEffect(MOVE_SPEED, 1.5, 1, ModifierType.MULT_MULT).named("Small Speed"));
		this.getSpells().setSelectedSpellIndex(0);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		double playerDx = player.getX() - this.getX();
		double playerDz = player.getZ() - this.getZ();
		double playerYaw = ZMath.PI_BY_2 + ZMath.atan2Normalized(playerDz, playerDx);
		
		// Simplistic ai to move to the player
		double playerDist = Math.sqrt(playerDx * playerDx + playerDz * playerDz);
		boolean inRange = playerDist < this.stat(ATTACK_RANGE) * 0.9;
		this.handleMobilityControls(dt, playerYaw, 0, false, false, !inRange, false, false, false);
		// Stop moving once close enough
		if(inRange) this.stopWalking();

		// If the AI has an attack available, and stamina is at least 75%, begin attacking
		var staminaPerc = this.currentStaminaPerc();
		if(this.getAttackTime() <= 0 && inRange && staminaPerc > .75){
			this.beginAttackOrSpell(zgame, playerYaw);
		}

		// Try to cast a spell
		if(!inRange) this.castSpell(zgame);

		// If sprinting and stamina is below half, stop sprinting
		var sprinting = this.isSprinting();
		if(staminaPerc < .5 && sprinting || inRange) this.setSprinting(false);
		// If stamina is above 75% and not sprinting, start sprinting
		else if(staminaPerc > .75 && !sprinting) this.setSprinting(true);
	}
	
	@Override
	protected void render(Game game, Renderer r){
		double facingAngle = this.getMobilityData().getFacingYaw() + ZMath.PI_BY_2;
		
		// Temporary simple rendering
		r.setColor(0.5, 0, 0);
		r.drawSidePlaneX(this.getX(), this.getY(), this.getZ(), this.getWidth(), this.getHeight(), facingAngle);

		// issue#23 make a way of drawing a health bar above the mob, accounting for how this health bar will not be a part of the mob itself, but above it

		// Draw bars to represent its remaining health, stamina, and mana
		var x = this.getX() + 4;
		var y = this.getY() + 4;
		var w = this.getWidth() * .2;
		var h = this.getHeight() - 8;
		r.setColor(1, 0, 0);
		r.drawRectangle(x, y, w, h * this.currentHealthPerc());
		r.drawSidePlaneX(this.getX(), this.getY() + this.getHeight() + 0.09, this.getZ(), this.getWidth() * 1.2 * this.currentHealthPerc(), 0.03, facingAngle);
		r.setColor(0, 1, 0);
		r.drawSidePlaneX(this.getX(), this.getY() + this.getHeight() + 0.05, this.getZ(), this.getWidth() * 1.2 * this.currentStaminaPerc(), 0.03, facingAngle);
		r.setColor(0, 0, 1);
		r.drawSidePlaneX(this.getX(), this.getY() + this.getHeight() + 0.01, this.getZ(), this.getWidth() * 1.2 * this.currentManaPerc(), 0.03, facingAngle);
		
		// TODO draw something to show the mob is attacking
//
//		// Draw an attack timer
//		r.setColor(.7, 0, 0);
//		this.renderAttackTimer(game, r);
	}
	
}
