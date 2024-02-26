package zgame.things.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.type.bounds.HitBox2D;

/** A thing which has a position and a hitbox in 2D */
public abstract class PositionedHitboxThing2D extends PositionedThing2D implements HitBox2D{
	
	/** Create a new PositionedHitboxThing at the coordinate (0, 0) */
	public PositionedHitboxThing2D(){
		super(0, 0);
	}
	
	/**
	 * Create a new PositionedHitboxThing at the given coordinate
	 *
	 * @param x The x coordinate, see {@link #getX()}
	 * @param y The y coordinate, see {@link #getY()}
	 */
	public PositionedHitboxThing2D(double x, double y){
		super(x, y);
	}
	
	@Override
	public boolean shouldRender(Game game, Renderer r){
		return r.gameBoundsInScreen(this.getBounds());
	}
	
	@Override
	public final HitBox2D asHitBox(){
		return this;
	}
	
}
