package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.core.utils.FunctionMap;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.things.entity.EntityThing3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A 3D implementation of a projectile which represents a thing flying through the air */
public abstract class Projectile3D extends EntityThing3D implements Projectile<HitBox3D, EntityThing3D, ZVector3D, Room3D, CollisionResult3D>{
	
	/**
	 * A mapping of the functions to call when certain object types are hit. This essentially replaces manually defining abstract functions in this class, allowing a specific
	 * function to be called for a specific type
	 */
	private final FunctionMap mappedFuncs;
	
	/** The amount of distance this projectile can travel before being deleted, negative value to never be deleted */
	private double range;
	
	/** The total distance this projectile has traveled since creation */
	private double totalDistance;
	
	/** true if the projectile should be removed in the next tick, false otherwise */
	private boolean willRemove;
	
	/** true if this is a {@link Projectile3D} which destroys itself when it hits anything, false otherwise */
	private boolean onHit;
	
	/** The uuid of the thing which created this projectile, or null if nothing created it, i.e. the source of this projectile */
	private String sourceId;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param z The initial z position of the projectile
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public Projectile3D(double x, double y, double z, ZVector3D launchVelocity){
		super(x, y, z, 1);
		this.addVelocity(launchVelocity);
		this.mappedFuncs = new FunctionMap();
		this.range = -1;
		this.totalDistance = 0;
		this.willRemove = false;
		this.onHit = false;
	}
	
	/** @return See {@link #mappedFuncs} */
	@Override
	public FunctionMap getMappedFuncs(){
		return this.mappedFuncs;
	}
	
	/** @return See {@link #onHit} */
	@Override
	public boolean isOnHit(){
		return this.onHit;
	}
	
	/** @param onHit See {@link #onHit} */
	public void setOnHit(boolean onHit){
		this.onHit = onHit;
	}
	
	/** @return See {@link #totalDistance} */
	@Override
	public double getTotalDistance(){
		return this.totalDistance;
	}
	
	/** @param totalDistance See {@link #totalDistance} */
	@Override
	public void setTotalDistance(double totalDistance){
		this.totalDistance = totalDistance;
	}
	
	/** @return See {@link #range} */
	@Override
	public double getRange(){
		return this.range;
	}
	
	/** @param range See {@link #range} */
	public void setRange(double range){
		this.range = range;
	}
	
	/** @return See {@link #sourceId} */
	@Override
	public String getSourceId(){
		return this.sourceId;
	}
	
	/** @param sourceId See {@link #sourceId} */
	public void setSourceId(String sourceId){
		this.sourceId = sourceId;
	}
	
	/** Tell this projectile to be removed on the next tick */
	@Override
	public void removeNext(){
		this.willRemove = true;
	}
	
	@Override
	public boolean willRemove(){
		return this.willRemove;
	}
	
	@Override
	public EntityThing3D getEntity(){
		return this;
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		Projectile.super.tick(game, dt);
	}
	
	@Override
	public void checkEntityCollision(Game game, EntityThing3D entity, double dt){
		super.checkEntityCollision(game, entity, dt);
		Projectile.super.checkEntityCollision(game, entity, dt);
	}
	
	@Override
	public void touchFloor(CollisionResult3D result){
		super.touchFloor(result);
		Projectile.super.touchFloor(result);
	}
	
	@Override
	public void touchCeiling(CollisionResult3D result){
		super.touchCeiling(result);
		Projectile.super.touchCeiling(result);
	}
	
	@Override
	public void touchWall(CollisionResult3D result){
		super.touchWall(result);
		Projectile.super.touchWall(result);
	}
	
	@Override
	public void collide(CollisionResult3D result){
		super.collide(result);
		Projectile.super.collide(result);
	}
}
