package zgame.things.type;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.type.bounds.HitBox3D;

/** A thing which has a position and a hitbox in 3D */
public abstract class PositionedHitboxThing3D extends PositionedThing3D implements HitBox3D{
	
	/** Create a new PositionedHitboxThing at the coordinate (0, 0,0) */
	public PositionedHitboxThing3D(){
		this(0, 0, 0);
	}
	
	/**
	 * Create a new PositionedHitboxThing at the given coordinate
	 *
	 * @param x The x coordinate, see {@link #getX()}
	 * @param y The y coordinate, see {@link #getY()}
	 * @param z The z coordinate, see {@link #getZ()}
	 */
	public PositionedHitboxThing3D(double x, double y, double z){
		super(x, y, z);
	}
	
	@Override
	public boolean shouldRender(Game game, Renderer r){
		// TODO implement
		return true;
	}
	
}
