package zgame.physics.material;

/** An implementation of {@link Material} which uses constant values */
public class MaterialConst implements Material{
	
	public static final double DEFAULT_FRICTION = 100.0;
	public static final double DEFAULT_LEFT_WALL_BOUNCE = 0.2;
	public static final double DEFAULT_RIGHT_WALL_BOUNCE = DEFAULT_LEFT_WALL_BOUNCE;
	public static final double DEFAULT_FLOOR_BOUNCE = 0;
	public static final double DEFAULT_CEILING_BOUNCE = 0;
	
	/** See {@link #getFriction()} */
	private double friction;
	
	/** See {@link #getLeftWallBounce()} */
	private double leftWallBounce;
	
	/** See {@link #getRightWallBounce()} */
	private double rightWallBounce;
	
	/** See {@link #getFloorBounce()} */
	private double floorBounce;
	
	/** See {@link #getCeilingBounce()} */
	private double ceilingBounce;
	
	/** Create a new material with all the default values */
	public MaterialConst(){
		this(DEFAULT_FRICTION, DEFAULT_LEFT_WALL_BOUNCE, DEFAULT_RIGHT_WALL_BOUNCE, DEFAULT_FLOOR_BOUNCE, DEFAULT_CEILING_BOUNCE);
	}
	
	/**
	 * Create a new material using the given fields and no floor or ceiling bounce
	 * 
	 * @param friction See {@link #friction}
	 * @param wallBounce The value for {@link #leftWallBounce} and {@link #rightWallBounce}
	 */
	public MaterialConst(double friction, double wallBounce){
		this(friction, wallBounce, wallBounce, DEFAULT_FLOOR_BOUNCE, DEFAULT_CEILING_BOUNCE);
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
		this(friction, wallBounce, wallBounce, floorBounce, ceilingBounce);
	}
	
	/**
	 * Create a new material using the given fields
	 * 
	 * @param friction See {@link #friction}
	 * @param leftWallBounce See {@link #leftWallBounce}
	 * @param rightWallBounce See {@link #rightWallBounce}
	 * @param floorBounce See {@link #floorBounce}
	 * @param ceilingBounce See {@link #ceilingBounce}
	 */
	public MaterialConst(double friction, double leftWallBounce, double rightWallBounce, double floorBounce, double ceilingBounce){
		this.friction = friction;
		this.leftWallBounce = leftWallBounce;
		this.rightWallBounce = rightWallBounce;
		this.floorBounce = floorBounce;
		this.ceilingBounce = ceilingBounce;
	}
	
	/** @return See {@link #friction} */
	@Override
	public double getFriction(){
		return this.friction;
	}
	
	/** @return See {@link #leftWallBounce} */
	@Override
	public double getLeftWallBounce(){
		return this.leftWallBounce;
	}
	
	/** @return See {@link #rightWallBounce} */
	@Override
	public double getRightWallBounce(){
		return this.rightWallBounce;
	}
	
	/** @return See {@link #floorBounce} */
	@Override
	public double getFloorBounce(){
		return this.floorBounce;
	}
	
	/** @return See {@link #ceilingBounce} */
	@Override
	public double getCeilingBounce(){
		return this.ceilingBounce;
	}
	
}
