package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.physics.material.Material;
import zgame.things.type.bounds.CircleHitBox;
import zgame.things.type.bounds.HitBox;

public class PlayerTesterCircle extends PlayerTester implements CircleHitBox{
	
	private final double radius;
	
	/**
	 * Create a new {@link PlayerTesterCircle} of the given size
	 *
	 * @param x The x coordinate of the {@link PlayerTesterCircle}
	 * @param y The y coordinate of the {@link PlayerTesterCircle}
	 * @param r The radius of the hitbox
	 */
	public PlayerTesterCircle(double x, double y, double r){
		super(x, y, r * 2, r * 2);
		this.radius = r;
	}
	
	@Override
	public double getRadius(){
		return this.radius;
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(1, 0, 0);
		r.drawCircle(this.centerX(), this.centerY(), this.radius);
	}
}
