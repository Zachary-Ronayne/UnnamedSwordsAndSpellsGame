package zgame.things.still.tiles;

import zgame.core.graphics.Renderer;
import zgame.physics.V3D;
import zgame.physics.material.Material;
import zgame.things.still.tiles.threeDee.Tile3D;
import zgame.things.type.bounds.threeDee.clickable.ClickerBounds;
import zgame.things.type.bounds.threeDee.shape.Shape3D;
import zgame.world.Direction3D;

/** A simple tile which has a constant material */
public non-sealed abstract class TileType3D extends TileType<V3D> implements Shape3D{
	
	/**
	 * Create a new {@link TileType3D} using the given data
	 *
	 * @param id See {@link #getId()}
	 * @param origin See {@link #getOrigin()}
	 * @param shapeOffset See {@link #shapeOffset}
	 * @param material See {@link #getMaterial()}
	 */
	public TileType3D(String id, String origin, V3D shapeOffset, Material material){
		super(id, origin, shapeOffset, material);
	}
	
	@Override
	protected final void renderGeneric(Tile<V3D> t, Renderer r){
		this.render((Tile3D)t, r);
	}
	
	/**
	 * Renders this tile type for the bounds of the given tile
	 * @param t The tile
	 * @param r The renderer to use
	 */
	public void render(Tile3D t, Renderer r){}
	
	/**
	 * @param face The face which is being checked
	 * @return true if this tile can collide with things on the given face, false otherwise
	 */
	public abstract boolean canCollide(Tile3D t, Direction3D face);
	
	// TODO should this be moved to bounds 3D? Probably renamed to something more abstract that isn't directly related to clicking
	/**
	 * Find the distance from the given clicker to click on the given tile
	 * @param t The tile to check
	 * @param clicker The thing doing the clicking
	 * @return The distance, or a negative number if there's no click
	 */
	public abstract double clickDistance(Tile3D t, ClickerBounds clicker);
}
