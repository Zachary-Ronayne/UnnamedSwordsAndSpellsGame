package zgame.things.still.tiles;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.core.GameThing;
import zgame.things.type.Materialable;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.ShapedBounds;

/** A {@link GameThing} with a bounds based on an index in an array. The indexes of this object should directly correlate to its position */
public abstract class Tile<V extends ZVector<V>> extends GameThing implements Bounds<V>, Materialable{
	
	/** @return The tile type that this tile uses for operations related to bounds */
	public abstract TileType<V> getBoundsType();
	
	@Override
	public V getDimensions(){
		return this.getBoundsType().getDimensions();
	}
	
	@Override
	public boolean intersectsDimensions(Bounds<V> bounds){
		return this.atMinPositionDimension(this.getMinPosition()).intersectsDimensions(bounds);
	}
	
	@Override
	public ShapedBounds<V> atMinPositionDimension(V pos){
		return this.getBoundsType().atMinPositionDimension(pos);
	}
	
	@Override
	public ShapedBounds<V> asShapedBoundsDimension(){
		return this.atMinPositionDimension(this.getDimensions());
	}
	
	@Override
	public Collision<V> collideAtDimensions(V currentMinPos, Bounds<V> bounds){
		return this.asShapedBoundsDimension().collideAtDimensions(currentMinPos, bounds);
	}
	
	@Override
	public double getGravityDragReferenceArea(){
		return this.asShapedBoundsDimension().getGravityDragReferenceArea();
	}
}
