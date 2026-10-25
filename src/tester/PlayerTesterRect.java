package tester;

import zgame.core.graphics.Renderer;
import zgame.things.type.bounds.twoDee.bounds.shape.RectangleBounds;

public class PlayerTesterRect extends PlayerTester implements RectangleBounds{
	/**
	 * Create a new {@link PlayerTesterRect} of the given size
	 *
	 * @param x The x coordinate of the {@link PlayerTesterRect}
	 * @param y The y coordinate of the {@link PlayerTesterRect}
	 * @param width The width of the {@link PlayerTesterRect} hit box
	 * @param height The height of the {@link PlayerTesterRect} hit box
	 */
	public PlayerTesterRect(double x, double y, double width, double height){
		super(x, y, width, height);
	}
	
	@Override
	public void render(Renderer r){
		r.setColor(1, 0, 0);
		var pos = this.getMinPosition();
		r.drawRectangle(pos.getX(), pos.getY(), this.getWidth(), this.getHeight());
	}
}
