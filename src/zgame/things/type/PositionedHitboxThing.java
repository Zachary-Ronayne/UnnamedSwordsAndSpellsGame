package zgame.things.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** A thing which has a position and a hitbox */
public abstract class PositionedHitboxThing extends PositionedThing implements HitBox{
	
	/** Create a new PositionedHitboxThing at the coordinate (0, 0) */
	public PositionedHitboxThing(){
		super(0, 0);
	}
	
	/**
	 * Create a new PositionedHitboxThing at the given coordinate
	 * @param x The x coordinate, see {@link #getX()}
	 * @param y The y coordinate, see {@link #getY()}
	 */
	public PositionedHitboxThing(double x, double y){
		super(x, y);
	}

	@Override
	public boolean shouldRender(Game game, Renderer r){
		return r.gameBoundsInScreen(this.getBounds());
	}
	
}
