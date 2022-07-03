package zgame.things.entity;

import java.util.ArrayList;
import java.util.Collection;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.utils.ZMathUtils;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.things.HitBox;
import zgame.things.PositionedThing;
import zgame.things.Room;

/**
 * A {@link PositionedThing} which keeps track of an entity, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 */
public abstract class EntityThing extends PositionedThing implements GameTickable, HitBox{
	
	/** The acceleration of gravity */
	public static final double GRAVITY_ACCELERATION = 800;
	
	/** The current velocity of this {@link EntityThing} */
	private ZVector velocity;
	
	/** The current force of gravity on this {@link EntityThing} */
	private ZVector gravity;
	
	/** The current force of friction on this {@link EntityThing} */
	private ZVector frictionForce;
	
	/** Every force currently acting on this {@link EntityThing} */
	private Collection<ZVector> forces;
	
	/** true if this {@link EntityThing} was on the ground in the past {@link #tick(Game, double)}, false otherwise */
	private boolean onGround;
	
	/** The value of {@link #getX()} from the last tick */
	private double px;
	/** The value of {@link #getY()} from the last tick */
	private double py;
	
	/** The mass, i.e. weight, of this {@link EntityThing} */
	private double mass;
	
	/**
	 * Create a new empty entity at (0, 0) with a mass of 1
	 */
	public EntityThing(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity with a mass of 100
	 * 
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public EntityThing(double x, double y){
		this(x, y, 100);
	}
	
	/**
	 * Create a new empty entity
	 * 
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 * @param mass See {@link #mass}
	 */
	public EntityThing(double x, double y, double mass){
		super(x, y);
		this.velocity = new ZVector(0, 0);
		
		this.forces = new ArrayList<ZVector>();
		
		this.gravity = new ZVector(0, 0);
		this.addForce(gravity); // TODO add terminal velocity for gravity
		this.setMass(mass);
		
		this.frictionForce = new ZVector(0, 0);
		this.addForce(frictionForce);
		
		this.onGround = false;
		this.px = this.getX();
		this.py = this.getY();
	}
	
	@Override
	public void tick(Game game, double dt){
		// Account for frictional force based on current velocity
		this.updateFrictionForce(dt);
		
		// Find the current acceleration
		ZVector acceleration = this.getForce().scale(1.0 / this.getMass());
		
		// Add the acceleration to the current velocity
		this.addVelocity(acceleration.scale(dt));
		
		// Move the entity based on the current velocity and acceleration
		this.px = this.getX();
		this.py = this.getY();
		ZVector moveVec = this.getVelocity().scale(dt).add(acceleration.scale(dt * dt * 0.5));
		this.addX(moveVec.getX());
		this.addY(moveVec.getY());
		
		// Now check the collision for what the entity is colliding with
		this.checkCollision(game.getCurrentRoom());
	}
	
	/**
	 * Determine the current amount of friction on this {@link EntityThing} and update the force
	 * 
	 * @param dt The amount of time that will pass the next time the frictional force is applied
	 */
	public void updateFrictionForce(double dt){
		// Determining direction
		double mass = this.getMass();
		ZVector force = this.getForce();
		double vx = this.getVX();
		double moveDirection = vx;
		double xf = force.getX() - this.frictionForce.getX();
		if(moveDirection == 0) moveDirection = xf;
		
		// TODO Base these numbers on the material the EntityThing is on, including air friction as air resistance
		// Find the total constant for friction, i.e. the amount of acceleration from friction, based on the surface and the entity's friction
		double surfaceFriction = this.isOnGround() ? 500.0 : 0.0;
		double newFrictionForce = (this.getFrictionConstant() * surfaceFriction) * mass;
		
		// If the total force is positive, then the constant needs to be negative, it will otherwise remain positive for a negative total force
		if(moveDirection > 0) newFrictionForce *= -1;
		
		double forceFactor = dt * dt / mass;
		double velFactor = vx * dt;
		// Find the signed distance that will be traveled if friction is not applied
		double oldDist = xf * forceFactor  + velFactor;
		// Find the signed distance that will be traveled if friction is applied
		double newDist = (newFrictionForce + xf) * forceFactor + velFactor;
		// If those distances are in opposing directions, then the frictional force should be such that it stops all velocity on the next tick
		if(!ZMathUtils.sameSign(newDist, oldDist) && Math.abs(newDist) > Math.abs(oldDist)) newFrictionForce = -oldDist / forceFactor;
		
		// Apply the new friction
		this.frictionForce = this.replaceForce(this.frictionForce, newFrictionForce, 0);
	}
	
	/**
	 * @return The number determining how much friction applies to this {@link EntityThing}.
	 *         Higher values mean more friction, lower values mean less friction, 0 means no friction, 1 means no movement on a surface can occur.
	 *         Behavior is undefined for negative return values
	 */
	public abstract double getFrictionConstant();
	
	/** @return A {@link ZVector} representing the total of all forces on this object */
	public ZVector getForce(){
		ZVector force = new ZVector(0, 0);
		for(ZVector f : this.forces) force = force.add(f);
		return force;
	}
	
	/** @return See {@link #velocity} */
	public ZVector getVelocity(){
		return this.velocity;
	}
	
	/** @return See {@link #gravity} */
	public ZVector getGravity(){
		return this.gravity;
	}
	
	/** @return See {@link #mass} */
	public double getMass(){
		return this.mass;
	}
	
	/** @param mass See {@link #mass} */
	public void setMass(double mass){
		this.mass = mass;
		this.gravity = this.replaceForce(this.gravity, 0, GRAVITY_ACCELERATION * this.getMass());
	}
	
	/** @return See {@link #onGround} */
	public boolean isOnGround(){
		return this.onGround;
	}
	
	@Override
	public void leaveFloor(){
		this.onGround = false;
	}
	
	@Override
	public void touchFloor(){
		// TODO add bouncing on the floor
		
		// Reset the y velocity to 0, only if the entity is moving downwards
		if(this.getVY() > 0) this.velocity = new ZVector(this.getVX(), 0);
		
		// Touching a floor means this entity is on the ground
		this.onGround = true;
	}
	
	@Override
	public void touchCeiling(){
		// TODO add bouncing on the ceiling
		
		// Reset the y velocity to 0, only if the entity is moving upwards
		if(this.getVY() < 0) this.velocity = new ZVector(this.getVX(), 0);
	}
	
	@Override
	public void touchWall(){
		// TODO make this based on a bounce amount based on the thing collided with
		this.addVX(-this.getVelocity().getX() * 1.2);
		
		// TODO add the ability to slide down a wall based on a value?
	}
	
	/**
	 * Collide this {@link EntityThing} with the given room. Can override this to perform custom collision
	 * 
	 * @param room The room to collide with
	 */
	public void checkCollision(Room room){
		room.collide(this);
	}
	
	@Override
	public void collide(CollisionResponse r){
		this.addX(r.x());
		this.addY(r.y());
		if(r.wall()) this.touchWall();
		if(r.ceiling()) this.touchCeiling();
		if(r.floor()) this.touchFloor();
	}
	
	/** @return The velocity of this {@link EntityThing} on the x axis */
	public double getVX(){
		return this.getVelocity().getX();
	}
	
	/** @return The velocity of this {@link EntityThing} on the y axis */
	public double getVY(){
		return this.getVelocity().getY();
	}
	
	/** @param x the new x velocity of this {@link EntityThing} */
	public void setVX(double x){
		this.velocity = new ZVector(x, this.getVY());
	}
	
	/** @param y the new y velocity of this {@link EntityThing} */
	public void setVY(double y){
		this.velocity = new ZVector(this.getVX(), y);
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
	public void addVX(double x){
		this.addVelocity(new ZVector(x, 0));
	}
	
	/**
	 * Add the given amount of velocity to the y component
	 * 
	 * @param y The velocity to add
	 */
	public void addVY(double y){
		this.addVelocity(new ZVector(0, y));
	}
	
	/**
	 * Determine if this {@link EntityThing} has the given force object
	 * 
	 * @param force The object to check for
	 * @return true if this {@link EntityThing} has the given force, false otherwise
	 */
	public boolean hasForce(ZVector force){
		return this.forces.contains(force);
	}
	
	/**
	 * Add the given {@link ZForce} to the forces acting on this {@link EntityThing}
	 * 
	 * @param force The force to add
	 */
	public void addForce(ZVector force){
		this.forces.add(force);
	}
	
	/**
	 * Remove the specified {@link ZForce} object from this {@link EntityThing}'s forces
	 * 
	 * @param force The force to remove
	 */
	public void removeForce(ZVector force){
		this.forces.remove(force);
	}
	
	/**
	 * Replace the given force with a force build from the given components
	 * 
	 * @param force The force object to remove
	 * @param x The x component
	 * @param y The y component
	 * @return The newly added vector object
	 */
	public ZVector replaceForce(ZVector force, double x, double y){
		this.removeForce(force);
		ZVector v = new ZVector(x, y);
		this.addForce(v);
		return v;
	}
	
	/** @return The x coordinate of this {@link EntityThing} where it was in the previous instance of time, based on its current velocity */
	public double getPX(){
		return px;
	}
	
	/** @return The y coordinate of this {@link EntityThing} where it was in the previous instance of time, based on its current velocity */
	public double getPY(){
		return py;
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
