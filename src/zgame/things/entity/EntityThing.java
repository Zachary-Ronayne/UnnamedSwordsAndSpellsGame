package zgame.things.entity;

import java.util.ArrayList;
import java.util.Collection;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.things.HitBox;
import zgame.things.PositionedThing;

/**
 * A {@link PositionedThing} which keeps track of an entity, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 */
public abstract class EntityThing extends PositionedThing implements GameTickable, HitBox{
	
	/** The ZVector determining how fast gravity applies to objects */
	public static final ZVector GRAVITY = new ZVector(0, 1000);
	
	/** The current velocity of this Entity */
	private ZVector velocity;
	
	/** Every force currently acting on this {@link EntityThing} */
	private Collection<ZVector> forces;
	
	/**
	 * Create a new empty entity at (0, 0)
	 */
	public EntityThing(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity
	 * 
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public EntityThing(double x, double y){
		super(x, y);
		this.velocity = new ZVector(0, 0);
		this.forces = new ArrayList<ZVector>();
		this.addForce(GRAVITY);
	}
	
	@Override
	public void tick(Game game, double dt){
		// Find the current acceleration
		ZVector acceleration = new ZVector(0, 0);
		for(ZVector f : this.forces) acceleration = acceleration.add(f);
		
		// Add the acceleration to the current velocity
		this.addVelocity(acceleration.scale(dt));
		
		// Move the entity based on the current velocity and acceleration
		ZVector moveVec = this.getVelocity().scale(dt).add(acceleration.scale(dt * dt * 0.5));
		this.addX(moveVec.getX());
		this.addY(moveVec.getY());
	}
	
	/** @return See {@link #velocity} */
	public ZVector getVelocity(){
		return this.velocity;
	}
	
	@Override
	public void touchFloor(){
		// Reset the y velocity to 0, only if the entity is moving downwards
		if(this.velocity.getY() > 0) this.velocity = new ZVector(this.velocity.getX(), 0);
	}
	
	@Override
	public void touchCeiling(){
		// Reset the y velocity to 0, only if the entity is moving upwards
		if(this.velocity.getY() < 0) this.velocity = new ZVector(this.velocity.getX(), 0);
	}
	
	@Override
	public void touchWall(){
	}
	
	@Override
	public void collide(CollisionResponse r){
		this.addX(r.x());
		this.addY(r.y());
		if(r.wall()) this.touchWall();
		if(r.ceiling()) this.touchCeiling();
		if(r.floor()) this.touchFloor();
	}
	
	/**
	 * Add the given velocity to {@link #velocity}
	 * 
	 * @param vec The velocity to add
	 */
	public void addVelocity(ZVector vec){
		this.velocity = this.velocity.add(vec);
	}
	
	/**
	 * Add the given amount of velocity to the x component
	 * 
	 * @param x The velocity to add
	 */
	public void addVelocityX(double x){
		this.addVelocity(new ZVector(x, 0));
	}
	
	/**
	 * Add the given amount of velocity to the y component
	 * 
	 * @param y The velocity to add
	 */
	public void addVelocityY(double y){
		this.addVelocity(new ZVector(0, y));
	}
	
	/**
	 * Add the given {@link ZForce} to the forces acting on this {@link EntityThing}
	 * 
	 * @param force The force to add
	 */
	public void addForce(ZVector force){
		this.forces.add(force);
	}
	
	/** @return The x coordinate of this {@link EntityThing} where it was in the previous instance of time, based on its current velocity */
	public double getPX(){
		return this.getX() - this.getVelocity().getX();
	}
	
	/** @return The y coordinate of this {@link EntityThing} where it was in the previous instance of time, based on its current velocity */
	public double getPY(){
		return this.getY() - this.getVelocity().getY();
	}
	
	/**
	 * Determine if this {@link EntityThing} intersects the given rectangular bounds
	 * 
	 * @param x The x coordinate upper left hand corner of the bounds
	 * @param y The y coordinate upper left hand corner of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @return true if this {@link EntityThing} intersects the given bounds
	 */
	public abstract boolean intersects(double x, double y, double w, double h);
	
}
