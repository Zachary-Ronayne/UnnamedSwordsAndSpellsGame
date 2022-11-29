package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zusass.ZusassGame;

/** A generic mob which uses health, status, etc, and is not a player */
public class Npc extends ZusassMob{
	
	// Keeping track of where the mob should be moving, temporary code for simplistic AI
	private double attackTime;
	private double maxAttackTime;
	
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
		this.attackTime = 2;
		this.maxAttackTime = 2;
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		ZusassGame zgame = (ZusassGame)game;
		
		// Simplistic ai to move to the player
		double playerX = zgame.getCurrentRoom().getPlayer().centerX();
		double thisX = this.centerX();
		if(Math.abs(playerX - thisX) > this.getWidth() * 0.5){
			if(playerX > this.centerX()) this.walkRight();
			else this.walkLeft();
		}
		// Ai to attack the player every every interval
		if(this.attackTime <= 0){
			this.attackTime = this.maxAttackTime;
			this.attackNearest(game);
		}
		else this.attackTime -= dt;
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// Temporary simple rendering
		r.setColor(0, .5, 0);
		r.drawRectangle(this.getBounds());
		
		// TODO make a way of drawing a health bar above the mob, accounting for how this health bar will not be a part of the mob itself, but above it
		
		// Draw a bar to represent it's remaining health
		r.setColor(1, 0, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth() * .25, this.getHeight() * this.currentHealthPerc());
		
		// Draw a bar to represent the time until an attack
		r.setColor(0, 0, 1);
		r.drawRectangle(this.getX() + this.getWidth() * 0.75, this.getY(), this.getWidth() * .25, this.getHeight() * (this.attackTime / this.maxAttackTime));
	}
	
	@Override
	public Npc asNpc(){
		return this;
	}
	
}
