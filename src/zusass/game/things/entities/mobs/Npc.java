package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** A generic mob which uses health, status, etc, and is not a player */
public class Npc extends ZusassMobRect{
	
	// Keeping track of where the mob should be moving, temporary code for simplistic AI
	private boolean moveRight;
	private double initialPosition;
	private double range;
	
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
		this.moveRight = true;
		this.initialPosition = x;
		this.range = 200;
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		
		// Simplistic ai to move back and forth, should be temporary
		double check = this.getX() - this.initialPosition;
		if(check <= -range) this.moveRight = true;
		else if(check >= range) this.moveRight = false;
		
		if(moveRight) this.walkRight();
		else this.walkLeft();
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// Temporary simple rendering
		r.setColor(0, .5, 0);
		r.drawRectangle(this.getBounds());

		// Draw a bar to represent it's remaining health
		r.setColor(1, 0, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth() * .25, this.getHeight() * this.currentHealthPerc());
	}
}
