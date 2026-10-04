package zgame.things.type.bounds;

import zgame.physics.V2D;

/** Represents a shape taking up no area */
public interface NoShape2D extends Shape2D{
	
	@Override
	default V2D getDimensions(){
		return new V2D();
	}
	
	@Override
	default BoundsType getBoundsType(){
		return BoundsType.NONE;
	}
}
