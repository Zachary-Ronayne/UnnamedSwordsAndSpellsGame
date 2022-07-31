package zgame.things.type;

/** An interface which describe a hitbox with a width and height */
public interface RectangleHitBox extends HitBox, RectangleBounds{

	@Override
	public default double centerX(){
		return this.getX() + this.getWidth() * 0.5;
	}

	@Override
	public default double centerY(){
		return this.getY() + this.getHeight() * 0.5;
	}

}
