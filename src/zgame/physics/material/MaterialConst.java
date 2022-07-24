package zgame.physics.material;

import zgame.core.utils.ZStringUtils;

/** An implementation of {@link Material} which uses constant values */
public class MaterialConst implements Material{
	
	/** The default value of {@link #friction} */
	public static final double DEFAULT_FRICTION = 0.1;
	/** The default value of {@link #wallBounce} */
	public static final double DEFAULT_WALL_BOUNCE = 0.5;
	/** The default value of {@link #floorBounce} */
	public static final double DEFAULT_FLOOR_BOUNCE = 0;
	/** The default value of {@link #ceilingBounce} */
	public static final double DEFAULT_CEILING_BOUNCE = 0;
	/** The default value of {@link #slipperinessSpeed} */
	public static final double DEFAULT_SLIPPERINESS_SPEED = 100;
	/** The default value of {@link #slipperinessAcceleration} */
	public static final double DEFAULT_SLIPPERINESS_ACCELERATION = 400000;
	
	/** See {@link Material#getFriction()} */
	private double friction;
	
	/** See {@link Material#getWallBounce()} */
	private double wallBounce;
	
	/** See {@link Material#getFloorBounce()} */
	private double floorBounce;
	
	/** See {@link Material#getCeilingBounce()} */
	private double ceilingBounce;
	
	/** See {@link Material#getSlipperinessSpeed()} */
	private double slipperinessSpeed;
	
	/** See {@link Material#getSlipperinessAcceleration()} */
	private double slipperinessAcceleration;
	
	/** Create a new material with all the default values */
	public MaterialConst(){
		this(DEFAULT_FRICTION, DEFAULT_WALL_BOUNCE);
	}
	
	/**
	 * Create a new material using the given fields and no floor or ceiling bounce
	 * 
	 * @param friction See {@link #friction}
	 * @param wallBounce The value for {@link #leftWallBounce} and {@link #rightWallBounce}
	 */
	public MaterialConst(double friction, double wallBounce){
		this(friction, wallBounce, DEFAULT_FLOOR_BOUNCE, DEFAULT_CEILING_BOUNCE, DEFAULT_SLIPPERINESS_SPEED, DEFAULT_SLIPPERINESS_ACCELERATION);
	}
	
	/**
	 * Create a new material using the given fields
	 * 
	 * @param friction See {@link #friction}
	 * @param wallBounce The value for {@link #leftWallBounce} and {@link #rightWallBounce}
	 * @param floorBounce See {@link #floorBounce}
	 * @param ceilingBounce See {@link #ceilingBounce}
	 */
	public MaterialConst(double friction, double wallBounce, double floorBounce, double ceilingBounce){
		this(friction, wallBounce, floorBounce, ceilingBounce, DEFAULT_SLIPPERINESS_SPEED, DEFAULT_SLIPPERINESS_ACCELERATION);
	}
	
	/**
	 * Create a new material using the given fields
	 * 
	 * @param friction See {@link #friction}
	 * @param wallBounce The value for {@link #leftWallBounce} and {@link #rightWallBounce}
	 * @param floorBounce See {@link #floorBounce}
	 * @param ceilingBounce See {@link #ceilingBounce}
	 * @param slipperinessSpeed See {@link #slipperinessSpeed}
	 */
	public MaterialConst(double friction, double wallBounce, double floorBounce, double ceilingBounce, double slipperinessSpeed, double slipperinessAcceleration){
		this.friction = friction;
		this.wallBounce = wallBounce;
		this.floorBounce = floorBounce;
		this.ceilingBounce = ceilingBounce;
		this.slipperinessSpeed = slipperinessSpeed;
		this.slipperinessAcceleration = slipperinessAcceleration;
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
	
	/** @return See {@link Material#getSlipperinessSpeed()()} */
	@Override
	public double getSlipperinessSpeed(){
		return this.slipperinessSpeed;
	}
	
	/** @return See {@link Material#getSlipperinessAcceleration()()} */
	@Override
	public double getSlipperinessAcceleration(){
		return this.slipperinessAcceleration;
	}
	
	@Override
	public String toString(){
		return ZStringUtils.concat("[MaterialConst | friction: ", this.getFriction(), ", wallBounce: ", this.getWallBounce(), ", floorBounce: ", this.getFloorBounce(),
				", ceilingBounce: ", this.getCeilingBounce(), ", slipperiness speed: ", this.getSlipperinessSpeed(), ", slipperiness acceleration: ",
				this.getSlipperinessAcceleration(), "]");
	}
	
}
