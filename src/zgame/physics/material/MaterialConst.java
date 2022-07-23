package zgame.physics.material;

import zgame.core.utils.ZStringUtils;

/** An implementation of {@link Material} which uses constant values */
public class MaterialConst implements Material{
	
	public static final double DEFAULT_FRICTION = 0.1;
	public static final double DEFAULT_WALL_BOUNCE = 0.5;
	public static final double DEFAULT_FLOOR_BOUNCE = 0;
	public static final double DEFAULT_CEILING_BOUNCE = 0;
	
	/** See {@link Material#getFriction()} */
	private double friction;
	
	/** See {@link Material#getWallBounce()} */
	private double wallBounce;
	
	/** See {@link Material#getFloorBounce()} */
	private double floorBounce;
	
	/** See {@link Material#getCeilingBounce()} */
	private double ceilingBounce;
	
	/** Create a new material with all the default values */
	public MaterialConst(){
		this(DEFAULT_FRICTION, DEFAULT_WALL_BOUNCE, DEFAULT_FLOOR_BOUNCE, DEFAULT_CEILING_BOUNCE);
	}
	
	/**
	 * Create a new material using the given fields and no floor or ceiling bounce
	 * 
	 * @param friction See {@link #friction}
	 * @param wallBounce The value for {@link #leftWallBounce} and {@link #rightWallBounce}
	 */
	public MaterialConst(double friction, double wallBounce){
		this(friction, wallBounce, DEFAULT_FLOOR_BOUNCE, DEFAULT_CEILING_BOUNCE);
	}
	
	/**
	 * Create a new material using the given fields, with the same left and right wall bounce values
	 * 
	 * @param friction See {@link #friction}
	 * @param wallBounce The value for {@link #leftWallBounce} and {@link #rightWallBounce}
	 * @param floorBounce See {@link #floorBounce}
	 * @param ceilingBounce See {@link #ceilingBounce}
	 */
	public MaterialConst(double friction, double wallBounce, double floorBounce, double ceilingBounce){
		this.friction = friction;
		this.wallBounce = wallBounce;
		this.floorBounce = floorBounce;
		this.ceilingBounce = ceilingBounce;
	}
	
	/** @return See {@link Material#getFriction()} */
	@Override
	public double getFriction(){
		return this.friction;
	}
	
	/** @return See {@link Material#getWallBounce()} */
	@Override
	public double getWallBounce(){
		return this.wallBounce;
	}
	
	/** @return See {@link Material#getFloorBounce()} */
	@Override
	public double getFloorBounce(){
		return this.floorBounce;
	}
	
	/** @return See {@link Material#getCeilingBounce()} */
	@Override
	public double getCeilingBounce(){
		return this.ceilingBounce;
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[MaterialConst | friction: ", this.getFriction(), ", wallBounce: ", this.getWallBounce(), ", floorBounce: ", this.getFloorBounce(),
				", ceilingBounce: ", this.getCeilingBounce(), "]");
	}
	
}
