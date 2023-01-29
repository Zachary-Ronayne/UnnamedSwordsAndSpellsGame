package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZMath;
import zusass.ZusassGame;

/** A generic mob which uses health, status, etc, and is not a player */
public class Npc extends ZusassMob{
	
	/**
	 * Create a new Npc with the given bounds
	 *
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 * @param width The mob's width
	 * @param height The mob's height
	 */
	public Npc(double x, double y, double width, double height){
		super(x, y, width, height);
		
		this.setWalkSpeedMax(100);
		this.getStats().setAttackSpeed(1);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		ZusassGame zgame = (ZusassGame)game;
		
		// Simplistic ai to move to the player
		ZusassPlayer player = zgame.getCurrentRoom().getPlayer();
		double playerX = player.centerX();
		double thisX = this.centerX();
		if(Math.abs(playerX - thisX) > this.getWidth() * 0.5){
			if(playerX > this.centerX()) this.walkRight();
			else this.walkLeft();
		}
		else this.stopWalking();
		// If the AI has an attack available, begin attacking
		if(this.getAttackTime() <= 0) this.beginAttack(ZMath.lineAngle(this.centerX(), this.centerY(), playerX, player.centerY()));
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// Temporary simple rendering
		r.setColor(0, .5, 0);
		r.drawRectangle(this.getBounds());
		
		// issue#23 make a way of drawing a health bar above the mob, accounting for how this health bar will not be a part of the mob itself, but above it
		
		// Draw a bar to represent its remaining health
		r.setColor(1, 0, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth() * .25, this.getHeight() * this.currentHealthPerc());
		
		// Draw an attack timer
		r.setColor(0, .5, 0);
		this.renderAttackTimer(game, r);
	}
	
	@Override
	public Npc asNpc(){
		return this;
	}
	
}
