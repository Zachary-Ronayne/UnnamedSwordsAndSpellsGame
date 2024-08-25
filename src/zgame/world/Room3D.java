package zgame.world;

import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Materials;
import zgame.things.entity.EntityThing3D;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox3D;

import java.util.Arrays;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room<HitBox3D, EntityThing3D, ZVector3D, Room3D> implements Bounds3D{
	
	/** An array of 6 elements representing which of the 6 boundary walls are enabled for collision */
	private final boolean[] enabledBoundaries;
	
	/** An array of 6 elements representing how far along each axis the boundary exists from the origin (0, 0, 0) */
	private final double[] boundarySizes;
	
	/**
	 * Create a new empty room in 3D space
	 */
	public Room3D(){
		super();
		this.enabledBoundaries = new boolean[6];
		this.setAllBoundaries(true);
		this.boundarySizes = new double[6];
		this.setAllBoundaries(4);
		this.setBoundary(Directions3D.DOWN, 0);
	}
	
	/** The position of a room will always be the origin (0, 0, 0) */
	@Override
	public final double getX(){
		return 0;
	}
	
	/** The position of a room will always be the origin (0, 0, 0) */
	@Override
	public final double getY(){
		return 0;
	}
	
	/** The position of a room will always be the origin (0, 0, 0) */
	@Override
	public final double getZ(){
		return 0;
	}
	
	@Override
	public double getWidth(){
		return this.boundarySizes[Directions3D.EAST] + this.boundarySizes[Directions3D.WEST];
	}
	
	@Override
	public double getHeight(){
		return this.boundarySizes[Directions3D.UP] + this.boundarySizes[Directions3D.DOWN];
	}
	
	@Override
	public double getLength(){
		return this.boundarySizes[Directions3D.NORTH] + this.boundarySizes[Directions3D.SOUTH];
	}
	
	/**
	 * Enable the given boundary if it is disabled, otherwise disable it
	 *
	 * @param direction The boundary to modify, defined in {@link Directions3D}
	 */
	public void toggleBoundary(int direction){
		this.setBoundary(direction, !this.boundaryEnabled(direction));
	}
	
	/**
	 * Enable or disable the given boundary
	 *
	 * @param direction The boundary to modify, defined in {@link Directions3D}
	 * @param enabled true to enable the boundary, false to disable it
	 */
	public void setBoundary(int direction, boolean enabled){
		this.enabledBoundaries[direction] = enabled;
	}
	
	/**
	 * @param direction The boundary to check for, defined in {@link Directions3D}
	 * @return true if the boundary is enabled, false otherwise
	 */
	public boolean boundaryEnabled(int direction){
		return this.enabledBoundaries[direction];
	}
	
	/**
	 * Enable or disable all boundaries
	 *
	 * @param enabled true to enabled, false to disable
	 */
	public void setAllBoundaries(boolean enabled){
		Arrays.fill(this.enabledBoundaries, enabled);
	}
	
	
	/**
	 * Update the size of the every boundary
	 *
	 * @param size The new size for every boundary
	 */
	public void setAllBoundaries(double size){
		Arrays.fill(this.boundarySizes, size);
	}
	
	/**
	 * Update the size of the given boundary
	 *
	 * @param direction The boundary to modify, defined in {@link Directions3D}
	 * @param size The new size for the boundary
	 */
	public void setBoundary(int direction, double size){
		this.boundarySizes[direction] = size;
	}
	
	/**
	 * @param direction The boundary to find, defined in {@link Directions3D}
	 * @return The size of the given boundary
	 */
	public double getBoundary(int direction){
		return this.boundarySizes[direction];
	}
	
	/**
	 * Set the dimensions of this room to be evenly split on the two given boundaries, to be evenly divided by the given size
	 *
	 * @param pos The positive direction
	 * @param neg The negative direction
	 * @param totalSize The new size to be split
	 */
	private void setEqualSize(int pos, int neg, double totalSize){
		double size = totalSize * 0.5;
		this.setBoundary(pos, size);
		this.setBoundary(neg, size);
	}
	
	/**
	 * Set the width of this room i.e. the boundaries on the x axis, to be evenly divided by the given size
	 *
	 * @param width The new width of the room, width/2 will be the boundary size on both axes
	 */
	public void setEqualWidth(double width){
		this.setEqualSize(Directions3D.EAST, Directions3D.WEST, width);
	}
	
	/**
	 * Set the height of this room i.e. the boundaries on the y axis, to be evenly divided by the given size
	 *
	 * @param height The new height of the room, height/2 will be the boundary size on both axes
	 */
	public void setEqualHeight(double height){
		this.setEqualSize(Directions3D.UP, Directions3D.DOWN, height);
	}
	
	/**
	 * Set the length of this room i.e. the boundaries on the z axis, to be evenly divided by the given size
	 *
	 * @param length The new length of the room, length/2 will be the boundary size on both axes
	 */
	public void setEqualLength(double length){
		this.setEqualSize(Directions3D.NORTH, Directions3D.SOUTH, length);
	}
	
	@Override
	public CollisionResponse collide(HitBox3D obj){
		// x axis, i.e. east west
		double widthOffset = obj.getWidth() * 0.5;
		if(this.boundaryEnabled(Directions3D.EAST) && obj.getX() > this.getBoundary(Directions3D.EAST) - widthOffset){
			obj.setX(this.getBoundary(Directions3D.EAST) - widthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		else if(this.boundaryEnabled(Directions3D.WEST) && obj.getX() < -this.getBoundary(Directions3D.WEST) + widthOffset){
			obj.setX(-this.getBoundary(Directions3D.WEST) + widthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		
		// z axis, i.e. north south
		double lengthOffset = obj.getLength() * 0.5;
		if(this.boundaryEnabled(Directions3D.NORTH) && obj.getZ() > this.getBoundary(Directions3D.NORTH) - lengthOffset){
			obj.setZ(this.getBoundary(Directions3D.NORTH) - lengthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		else if(this.boundaryEnabled(Directions3D.SOUTH) && obj.getZ() < -this.getBoundary(Directions3D.SOUTH) + lengthOffset){
			obj.setZ(-this.getBoundary(Directions3D.SOUTH) + lengthOffset);
			obj.touchWall(Materials.BOUNDARY);
		}
		
		// y axis, i.e. up down
		double height = obj.getHeight();
		if(this.boundaryEnabled(Directions3D.UP) && obj.getY() > this.getBoundary(Directions3D.UP) - height){
			obj.setY(this.getBoundary(Directions3D.UP) - height);
			obj.touchCeiling(Materials.BOUNDARY);
		}
		else if(this.boundaryEnabled(Directions3D.DOWN) && obj.getY() < -this.getBoundary(Directions3D.DOWN)){
			obj.setY(-this.getBoundary(Directions3D.DOWN));
			obj.touchFloor(Materials.BOUNDARY);
		}
		// TODO also call this when the thing touches the ground once proper collision is implemented
		else if(obj.isOnGround()) {
			if(obj.getY() != obj.getPX()) obj.leaveFloor();
			else obj.touchFloor(obj.getFloorMaterial());
		}
		
		return new CollisionResponse();
	}
	
	@Override
	public Class<HitBox3D> getHitBoxType(){
		return HitBox3D.class;
	}
	
	@Override
	public Class<EntityThing3D> getEntityClass(){
		return EntityThing3D.class;
	}
	
}
