package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.stat.modifier.ModifierType;
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
		
		this.setStat(MOVE_SPEED, 100);
		this.setStat(ATTACK_SPEED, 1);
		this.setStat(STAMINA_REGEN, 1);
		this.setStat(ENDURANCE, 4);
		this.setStat(INTELLIGENCE, 0.5);
		
		// Set a default spell as speed
		this.getSpells().addSpell(Spell.selfEffect(MOVE_SPEED, 2, 1, ModifierType.MULT_MULT).named("Small Speed"));
		this.getSpells().setSelectedSpellIndex(0);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		
		// TODO implement basic AI
//		ZusassGame zgame = (ZusassGame)game;
//
//		// Simplistic ai to move to the player
//		ZusassPlayer player = zgame.getPlayer();
//		double playerX = player.centerX();
//		double thisX = this.centerX();
//		double playerDist = Math.abs(playerX - thisX);
//		if(playerDist > this.getWidth() * 0.5){
//			if(playerX > this.centerX()) this.walkRight();
//			else this.walkLeft();
//		}
//		else this.stopWalking();
//
//		// If the AI has an attack available, and stamina is at least about a third, begin attacking
//		var staminaPerc = this.currentStaminaPerc();
//		if(this.getAttackTime() <= 0 && playerDist < this.stat(ATTACK_RANGE) * 1.5 && staminaPerc > .33){
//			this.beginAttackOrSpell(zgame, ZMath.lineAngle(this.centerX(), this.centerY(), playerX, player.centerY()));
//		}
//
//		// Try to cast a spell
//		this.castSpell(zgame);
//
//		// If running and stamina is below half, stop running
//		var walking = this.isWalking();
//		if(staminaPerc < .5 && !walking) this.setWalking(true);
//		// If stamina is above 75% and walking, start running
//		else if(staminaPerc > .75 && walking) this.setWalking(false);
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// TODO figure out how this mob will be rendered
		
//		// Temporary simple rendering
//		r.setColor(0, 0, 0);
//		r.drawRectangle(this.getBounds());
//		r.setColor(.5, .3, 0);
//		r.drawRectangle(this.getBounds().pad(-2));
//
//		// issue#23 make a way of drawing a health bar above the mob, accounting for how this health bar will not be a part of the mob itself, but above it
//
//		// Draw bars to represent its remaining health, stamina, and mana
//		var x = this.getX() + 4;
//		var y = this.getY() + 4;
//		var w = this.getWidth() * .2;
//		var h = this.getHeight() - 8;
//		r.setColor(1, 0, 0);
//		r.drawRectangle(x, y, w, h * this.currentHealthPerc());
//		r.setColor(0, 1, 0);
//		r.drawRectangle(x + w, y, w, h * this.currentStaminaPerc());
//		r.setColor(0, 0, 1);
//		r.drawRectangle(x + w * 2, y, w, h * this.currentManaPerc());
//
//		// Draw an attack timer
//		r.setColor(.7, 0, 0);
//		this.renderAttackTimer(game, r);
	}
	
}
