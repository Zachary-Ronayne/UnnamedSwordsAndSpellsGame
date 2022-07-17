package zgame.things.entity;

import java.util.HashMap;
import java.util.Map;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.utils.ZMathUtils;
import zgame.physics.ZVector;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.HitBox;
import zgame.things.PositionedThing;
import zgame.world.Room;

/**
 * A {@link PositionedThing} which keeps track of an entity, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 */
public abstract class EntityThing extends PositionedThing implements GameTickable, HitBox{
	
	/** The string used to identify the force of gravity in {@link #forces} */
	public static final String FORCE_NAME_GRAVITY = "gravity";
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_NAME_FRICTION = "friction";
	
	/** The acceleration of gravity */
	public static final double GRAVITY_ACCELERATION = 800;
	
	/** The current velocity of this {@link EntityThing} */
	private ZVector velocity;
	
	/** The current force of gravity on this {@link EntityThing} */
	private ZVector gravity;
	
	/** The current force of friction on this {@link EntityThing} */
	private ZVector frictionForce;
	
	/** Every force currently acting on this {@link EntityThing}, mapped by a name */
	private Map<String, ZVector> forces;
	
	/** A {@link ZVector} representing the total force acting on this {@link EntityThing} */
	private ZVector totalForce;
	
	/** The material which this {@link EntityThing} is standing on, or {@link Materials#NONE} if no material is being touched */
	private Material groundMaterial;
	
	/** true if this {@link EntityThing} is on the ground, false otherwise */
	private boolean onGround;
	
	/** The value of {@link #getX()} from the last tick */
	private double px;
	/** The value of {@link #getY()} from the last tick */
	private double py;
	
	/** The mass, i.e. weight, of this {@link EntityThing} */
	private double mass;
	
	/** The Material which this {@link EntityThing} is made of */
	private Material material;
	
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
		
		this.forces = new HashMap<String, ZVector>();
		this.totalForce = new ZVector(0, 0);
		
		this.gravity = new ZVector(0, 0);
		this.addForce(FORCE_NAME_GRAVITY, gravity); // TODO add terminal velocity for gravity
		this.setMass(mass);
		this.material = Materials.DEFAULT_ENTITY;
		
		this.frictionForce = new ZVector(0, 0);
		this.addForce(FORCE_NAME_FRICTION, frictionForce);
		
		this.leaveFloor();
		this.px = this.getX();
		this.py = this.getY();
	}
	
	@Override
	public void tick(Game game, double dt){
		// Account for frictional force based on current ground material
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
		// TODO fix weird issue with high friction where the friction velocity carries after moving off a platform
		
		// Determining direction
		double mass = this.getMass();
		ZVector force = this.getForce();
		double vx = this.getVX();
		double xf = force.getX() - this.frictionForce.getX();
		double moveDirection = xf;
		if(moveDirection == 0) moveDirection = vx;
		
		// Find the total constant for friction, i.e. the amount of acceleration from friction, based on the surface and the entity's friction
		double newFrictionForce = (this.getFrictionConstant() * this.getGroundMaterial().getFriction()) * mass;
		
		// If the total force is positive, then the constant needs to be negative, it will otherwise remain positive for a negative total force
		if(moveDirection > 0) newFrictionForce *= -1;
		
		double forceFactor = dt * dt / mass;
		double velFactor = vx * dt;
		// Find the signed distance that will be traveled if friction is not applied
		double oldDist = xf * forceFactor + velFactor;
		// Find the signed distance that will be traveled if friction is applied
		double newDist = (newFrictionForce + xf) * forceFactor + velFactor;
		// If those distances are in opposing directions, then the frictional force should be such that it stops all velocity on the next tick
		if(!ZMathUtils.sameSign(newDist, oldDist) && Math.abs(newDist) > Math.abs(oldDist)) newFrictionForce = -oldDist / forceFactor;
		
		// Apply the new friction
		this.frictionForce = this.replaceForce(FORCE_NAME_FRICTION, newFrictionForce, 0);
	}
	
	/**
	 * @return The number determining how much friction applies to this {@link EntityThing}.
	 *         Higher values mean more friction, lower values mean less friction, 0 means no friction, 1 means no movement on a surface can occur.
	 *         Behavior is undefined for negative return values
	 */
	public abstract double getFrictionConstant();
	
	@Override
	public Material getMaterial(){
		return this.material;
	}
	
	/** @return A {@link ZVector} representing the total of all forces on this object */
	public ZVector getForce(){
		return this.totalForce;
	}
	
	/** @return See {@link #velocity} */
	public ZVector getVelocity(){
		return this.velocity;
	}
	
	/** @return See {@link #gravity} */
	public ZVector getGravity(){
		return this.gravity;
	}
	
	/** @return See {@link #frictionForce} */
	public ZVector getFriction(){
		return this.frictionForce;
	}
	
	/** @return See {@link #mass} */
	public double getMass(){
		return this.mass;
	}
	
	/** @param mass See {@link #mass} */
	public void setMass(double mass){
		this.mass = mass;
		this.gravity = this.replaceForce(FORCE_NAME_GRAVITY, 0, GRAVITY_ACCELERATION * this.getMass());
	}
	
	/** @return See {@link #groundMaterial} */
	public Material getGroundMaterial(){
		return this.groundMaterial;
	}
	
	/** @return true if this {@link EntityThing} was on the ground in the past {@link #tick(Game, double)}, false otherwise */
	@Override
	public boolean isOnGround(){
		return this.onGround;
	}
	
	@Override
	public void leaveFloor(){
		this.groundMaterial = Materials.NONE;
		this.onGround = false;
	}
	
	@Override
	public void touchFloor(Material touched){
		// Touching a floor means this entity is on the ground
		this.groundMaterial = touched;
		this.onGround = true;
		
		// Bounce off the floor, or reset the y velocity to 0 if either material has no floor bounciness
		this.setVY(-this.getVY() * touched.getFloorBounce() * this.getMaterial().getFloorBounce());
	}
	
	@Override
	public void touchCeiling(Material touched){
		// Bounce off the ceiling, or reset the y velocity to 0 if either material has no ceiling bounciness
		this.setVY(-this.getVY() * touched.getCeilingBounce() * this.getMaterial().getCeilingBounce());
	}
	
	@Override
	public void touchWall(Material touched){
		// Bounce off the wall based on the touched material and this entity thing
		this.setVX(-this.getVX() * touched.getWallBounce() * this.getMaterial().getWallBounce());
		
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
		if(r.wall()) this.touchWall(r.material());
		if(r.ceiling()) this.touchCeiling(r.material());
		if(r.floor()) this.touchFloor(r.material());
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
	 * Determine if this {@link EntityThing} has the force object mapped to the given name
	 * 
	 * @param name The object to check for
	 * @return true if this {@link EntityThing} has the given force, false otherwise
	 */
	public boolean hasForce(String name){
		return this.forces.containsKey(name);
	}
	
	/**
	 * Add the given {@link ZForce} to the forces acting on this {@link EntityThing}. This method does nothing if the given force name is already used by the entity. To modify the
	 * value of a force, use {@link #replaceForce(String, double, double)}
	 * 
	 * @param force The force to add
	 * @param name The name of this force, used to identity the force
	 * @return true if the force was added, false otherwise
	 */
	public boolean addForce(String name, ZVector force){
		if(this.hasForce(name)) return false;
		
		this.forces.put(name, force);
		this.totalForce = totalForce.add(force);
		return true;
	}
	
	/**
	 * Remove the {@link ZForce} with the specified name object from this {@link EntityThing}'s forces
	 * 
	 * @param name The name of the force to remove
	 * 
	 * @return The removed force vector, or null if the given force was not found
	 */
	public ZVector removeForce(String name){
		ZVector removed = this.forces.remove(name);
		if(removed == null) return null;
		this.totalForce = this.totalForce.add(removed.scale(-1));
		return removed;
	}
	
	/**
	 * Replace the given force with a force build from the given components. If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the
	 * map
	 * 
	 * @param name The name of the force to replace
	 * @param x The x component
	 * @param y The y component
	 * @return The newly added vector object
	 */
	public ZVector replaceForce(String name, double x, double y){
		this.removeForce(name);
		ZVector v = new ZVector(x, y);
		this.addForce(name, v);
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
