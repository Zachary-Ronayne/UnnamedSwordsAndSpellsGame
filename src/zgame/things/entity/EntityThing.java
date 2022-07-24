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
	/** The string used to identify the force of friction in {@link #forces} */
	public static final String FORCE_NAME_GRAVITY_DRAG = "gravityDrag";
	
	/** The acceleration of gravity */
	public static final double GRAVITY_ACCELERATION = 800;
	
	/** The current velocity of this {@link EntityThing} */
	private ZVector velocity;
	
	/** The current force of gravity on this {@link EntityThing} */
	private ZVector gravity;
	
	/** The current force of friction on this {@link EntityThing}. */
	private ZVector frictionForce;
	
	/** The current force of drag acting against gravity on this {@link #EntityThing()} */
	private ZVector gravityDragForce;
	
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
		this.velocity = new ZVector();
		
		this.forces = new HashMap<String, ZVector>();
		this.totalForce = new ZVector();
		
		this.gravity = new ZVector();
		this.setForce(FORCE_NAME_GRAVITY, gravity);
		this.setMass(mass);
		this.material = Materials.DEFAULT_ENTITY;
		
		this.frictionForce = new ZVector();
		this.setForce(FORCE_NAME_FRICTION, frictionForce);
		
		this.gravityDragForce = new ZVector();
		this.setForce(FORCE_NAME_GRAVITY_DRAG, this.gravityDragForce);
		
		this.leaveFloor();
		this.px = this.getX();
		this.py = this.getY();
	}
	
	@Override
	public void tick(Game game, double dt){
		// Account for frictional force based on current ground material
		this.updateFrictionForce(dt);
		
		// Account for drag going down for terminal velocity
		this.updateGravityDragForce(dt);
		
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
	 * @param dt The amount of time, in seconds, that will pass the next time the frictional force is applied
	 */
	public void updateFrictionForce(double dt){
		// Determining direction
		double mass = this.getMass();
		ZVector force = this.getForce();
		double vx = this.getVX();
		double fx = force.getX() - this.frictionForce.getX();
		// Find the total constant for friction, i.e. the amount of acceleration from friction, based on the surface and the entity's friction
		double newFrictionForce = (this.getFrictionConstant() * this.getGroundMaterial().getFriction()) * Math.abs(this.getGravity().getY());
		
		// Movement direction is based on velocity direction
		double moveDirection = vx;
		// If there is no velocity, then the force of friction is equal and opposite to the current total force without friction, and will not exceed the value based on gravity
		if(moveDirection == 0){
			newFrictionForce = Math.min(newFrictionForce, Math.abs(fx));
			if(fx > 0) newFrictionForce *= -1;
			this.frictionForce = this.setForceX(FORCE_NAME_FRICTION, newFrictionForce);
			return;
		}

		// Need to make the force of friction move in the opposite direction of movement, so make it negative if the direction is positive, otherwise leave it positive
		if(moveDirection > 0) newFrictionForce *= -1;
		
		// If applying the new force would make the velocity go in the opposite direction, then the force should be such that the velocity will be zero
		double oldVel = vx + fx / mass * dt;
		double newVel = vx + (fx + newFrictionForce) / mass * dt;
		if(!ZMathUtils.sameSign(oldVel, newVel)) newFrictionForce = -vx / dt * mass;
		
		this.frictionForce = this.setForceX(FORCE_NAME_FRICTION, newFrictionForce);
		// TODO why can you still move a bit after landing until you stop moving? on really high friction forces
	}
	
	/**
	 * Determine the current amount of drag on this {@link EntityThing} counteracting the force of gravity
	 * 
	 * @param dt The amount of time, in seconds, that will pass the next time the drag force is applied
	 */
	public void updateGravityDragForce(double dt){
		// Determine terminal velocity
		double terminalVelocity = this.getTerminalVelocity();
		
		// If downward velocity exceeds terminal velocity, and terminal velocity is not negative, set the vector to be equal and opposite to gravity
		if(this.getVY() >= terminalVelocity && terminalVelocity > 0){
			// Only set the value if it is not equal and opposite to gravity
			double gravityForce = -this.getGravity().getY();
			if(this.getGravityDragForce().getY() != gravityForce) this.gravityDragForce = this.setForceY(FORCE_NAME_GRAVITY_DRAG, gravityForce);
		}
		// Otherwise, set remove the force
		else{
			// Only remove the force if it is not already zero
			if(this.getGravityDragForce().getY() != 0) this.gravityDragForce = this.setForceY(FORCE_NAME_GRAVITY_DRAG, 0);
		}
	}
	
	/**
	 * @return The terminal velocity of this {@link EntityThing}. By default, based on the mass, the acceleration of gravity, the value of {@link #getSurfaceArea()},
	 *         and the friction of the ground material, which is also the air material when this {@link EntityThing} is not on the ground.
	 *         Returns 0 if this {@link EntityThing} is on the ground.
	 *         If {@link #getSurfaceArea()} returns 0, or is negative, then the value is ignored in the calculation.
	 *         If this method is made to return a negative value, terminal velocity is removed, i.e. the force of gravity will continue to accelerate
	 */
	public double getTerminalVelocity(){
		if(this.isOnGround()) return 0;
		
		Material m = this.getGroundMaterial();
		double s = this.getSurfaceArea();
		double surfaceArea = (s <= 0) ? 1 : s;
		
		// Multiplied by 2.0 because the internet says that constant is there for the equation is for terminal velocity
		// Multiplied by 0.01 as a placeholder for density. For now, everything entities fall through is considered to have that same constant density
		return Math.sqrt(Math.abs((2.0 * this.getMass() * GRAVITY_ACCELERATION) / (m.getFriction() * surfaceArea * 0.01)));
	}
	
	/** @return The surface area of this {@link EntityThing}, by default returns {@link #getWidth()}. Can override to create a custom surface area */
	public double getSurfaceArea(){
		return this.getWidth();
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
	
	/** @return SEE {@link #gravityDragForce} */
	public ZVector getGravityDragForce(){
		return this.gravityDragForce;
	}
	
	/** @return See {@link #mass} */
	public double getMass(){
		return this.mass;
	}
	
	/** @param mass See {@link #mass} */
	public void setMass(double mass){
		this.mass = mass;
		this.gravity = this.setForce(FORCE_NAME_GRAVITY, 0, GRAVITY_ACCELERATION * this.getMass());
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
		// Essentially, do this, but with forces
		// this.setVY(Math.min(this.getVY(), 100));
		// make this also involved with terminal velocity?
		// also add methods for leave ceiling and leave walls
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
	
	/** @param The new current velocity of this {@link EntityThing} */
	public void setVelocity(ZVector velocity){
		this.velocity = velocity;
	}
	
	/**
	 * @param x The new x velocity of this {@link EntityThing}
	 * @param x The new y velocity of this {@link EntityThing}
	 */
	public void setVelocity(double x, double y){
		this.setVelocity(new ZVector(x, y));
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
		this.setVelocity(x, this.getVY());
	}
	
	/** @param y the new y velocity of this {@link EntityThing} */
	public void setVY(double y){
		this.setVelocity(this.getVX(), y);
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
	 * Set the given force name with a force built from the given components. If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the
	 * map
	 * 
	 * @param name The name of the force to set
	 * @param x The x component
	 * @param y The y component
	 * @return The newly set vector object
	 */
	public ZVector setForce(String name, double x, double y){
		return setForce(name, new ZVector(x, y));
	}
	
	/**
	 * Set the given force name to the given force. If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the
	 * map
	 * 
	 * @param name The name of the force to set
	 * @param force The force object to set
	 * @return force
	 */
	public ZVector setForce(String name, ZVector force){
		this.removeForce(name);
		this.forces.put(name, force);
		this.totalForce = totalForce.add(force);
		return force;
	}
	
	/**
	 * The same as see {@link #setForce(String, double, double)}, except only modify the x value, keep the y value the same.
	 * If the force does not already exist, a force is created with the given name using a zero for the y value
	 */
	public ZVector setForceX(String name, double x){
		ZVector v = this.forces.get(name);
		return this.setForce(name, x, (v == null) ? 0 : v.getY());
	}
	
	/**
	 * The same as see {@link #setForce(String, double, double)}, except only modify the y coordinate, keep the x coordinate the same.
	 * If the force does not already exist, a force is created with the given name using a zero for the x value
	 */
	public ZVector setForceY(String name, double y){
		ZVector v = this.forces.get(name);
		return this.setForce(name, (v == null) ? 0 : v.getX(), y);
	}
	
	/** @return The x coordinate of this {@link EntityThing} where it was in the previous instance of time, based on its current velocity */
	@Override
	public double getPX(){
		return px;
	}
	
	/** @return The y coordinate of this {@link EntityThing} where it was in the previous instance of time, based on its current velocity */
	@Override
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
