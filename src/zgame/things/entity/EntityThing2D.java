package zgame.things.entity;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.physics.ZVector;
import zgame.physics.ZVector2D;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.type.PositionedHitboxThing2D;
import zgame.things.type.PositionedThing;

/**
 * A {@link PositionedThing} which keeps track of an entity, i.e. an object which can regularly move around in space and exist at an arbitrary location.
 * This is for things like creatures, dropped items, projectiles, etc.
 */
public abstract class EntityThing2D extends PositionedHitboxThing2D implements GameTickable{
	
	// TODO make a 3D version of this
	
	// issue#21 allow for multiple hitboxes, so a hitbox for collision and one for rendering, and one for hit detection

	// TODO figure out how EntityThing and EntityThing2D need to be combined
	// TODO make up a naming structure that makes sense
	/** The {@link Entity} used by this {@link EntityThing2D} */
	private final Entity2D entity;
	
	/**
	 * Create a new empty entity at (0, 0) with a mass of 1
	 */
	public EntityThing2D(){
		this(0, 0);
	}
	
	/**
	 * Create a new empty entity with a mass of 100
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 */
	public EntityThing2D(double x, double y){
		this(x, y, 100);
	}
	
	/**
	 * Create a new empty entity
	 *
	 * @param x The x coordinate of the entity
	 * @param y The y coordinate of the entity
	 * @param mass See {@link Entity#mass}
	 */
	public EntityThing2D(double x, double y, double mass){
		super(x, y);
		this.entity = this.createEntity(mass);
	}
	
	/**
	 * Create a new entity for this object. The object returned by this method will be used as this object's entity.
	 * Override to provide a custom entity
	 * @param mass The starting mass of the entity
	 * @return The new entity object
	 */
	public Entity2D createEntity(double mass){
		return new Entity2D(this, mass);
	}
	
	// TODO should this be in the abstract Entity?
	/** @return See {@link #entity} */
	public Entity2D getEntity(){
		return this.entity;
	}
	
	@Override
	public void tick(Game game, double dt){
		this.entity.tick(game, dt);
	}
	
	@Override
	public void collide(CollisionResponse r){
		this.entity.collide(r);
	}
	
	// TODO abstract out and or condense these velocity and force methods
	/**
	 * @param x The new x velocity of this {@link EntityThing2D}
	 * @param y The new y velocity of this {@link EntityThing2D}
	 */
	public void setVelocity(double x, double y){
		this.getEntity().setVelocity(new ZVector2D(x, y));
	}
	
	/** @return The velocity of this {@link EntityThing2D} on the x axis */
	public double getVX(){
		return this.getEntity().getHorizontalVel();
	}
	
	/** @return The velocity of this {@link EntityThing2D} on the y axis */
	public double getVY(){
		return this.getEntity().getVerticalVel();
	}
	
	/** @param x the new x velocity of this {@link EntityThing2D} */
	public void setVX(double x){
		this.setVelocity(x, this.getVY());
	}
	
	/** @param y the new y velocity of this {@link EntityThing2D} */
	public void setVY(double y){
		this.setVelocity(this.getVX(), y);
	}
	
	/**
	 * Add the given velocity to the current velocity
	 *
	 * @param vec The velocity to add
	 */
	public void addVelocity(ZVector vec){
		this.getEntity().setVelocity(this.entity.getVelocity().add(vec));
	}
	
	/**
	 * Add the given amount of velocity to the x component
	 *
	 * @param x The velocity to add
	 */
	public void addVX(double x){
		this.addVelocity(new ZVector2D(x, 0));
	}
	
	/**
	 * Add the given amount of velocity to the y component
	 *
	 * @param y The velocity to add
	 */
	public void addVY(double y){
		this.addVelocity(new ZVector2D(0, y));
	}
	
	// TODO figure out what this is needed for
	@Override
	public final GameTickable asTickable(){
		return this;
	}
	
	@Override
	public String getUuid(){
		return this.entity.getUuid();
	}
	
	// TODO should this be an abstract method here or somewhere else?
	/**
	 * @return The number determining how much friction applies to this {@link Entity}.
	 * Higher values mean more friction, lower values mean less friction, 0 means no friction, 1 means no movement on a surface can occur.
	 * Behavior is undefined for negative return values
	 */
	public abstract double getFrictionConstant();
	
	// TODO make it less stupid to have all this implemented
	@Override
	public Material getMaterial(){
		return this.getEntity().getMaterial();
	}
	
	@Override
	public void touchFloor(Material touched){
		this.entity.touchFloor(touched);
	}
	
	@Override
	public void leaveFloor(){
		this.entity.leaveFloor();
	}
	
	@Override
	public void touchCeiling(Material touched){
		this.entity.touchCeiling(touched);
	}
	
	@Override
	public void leaveCeiling(){
		this.entity.leaveCeiling();
	}
	
	@Override
	public void touchWall(Material touched){
		this.entity.touchWall(touched);
	}
	
	@Override
	public void leaveWall(){
		this.entity.leaveWall();
	}
	
	@Override
	public boolean isOnGround(){
		return this.entity.isOnGround();
	}
	
	@Override
	public boolean isOnCeiling(){
		return this.entity.isOnCeiling();
	}
	
	@Override
	public boolean isOnWall(){
		return this.entity.isOnWall();
	}
	
	@Override
	public double getPX(){
		return this.entity.getPX();
	}
	
	@Override
	public double getPY(){
		return this.entity.getPY();
	}
	
}
