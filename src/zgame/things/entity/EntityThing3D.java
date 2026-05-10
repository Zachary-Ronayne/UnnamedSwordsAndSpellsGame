package zgame.things.entity;

import zgame.core.graphics.camera.GameCamera3D;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.things.entity.state.vector.ForceSetElement;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/**
 * An {@link EntityThing} in 3D
 */
public abstract class EntityThing3D extends EntityThing<HitBox3D, EntityThing3D, ZVector3D, Room3D, CollisionResult3D> implements HitBox3D{
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass The initial mass of the entity
	 */
	public EntityThing3D(double mass){
		this(0, 0, 0, mass);
	}
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param x Initial x coordinate of this thing
	 * @param y Initial x coordinate of this thing
	 * @param z Initial x coordinate of this thing
	 * @param mass The initial mass of the entity
	 */
	public EntityThing3D(double x, double y, double z, double mass){
		super(mass);
		this.getNext().attemptSetPosition(new ZVector3D(x, y, z));
	}
	
	@Override
	public void moveEntity(ZVector3D distance){
		this.addX(distance.getX());
		this.addY(distance.getY());
		this.addZ(distance.getZ());
	}
	
	@Override
	public double getHorizontalVel(){
		return this.getVelocity().getHorizontal();
	}
	
	@Override
	public double getVerticalVel(){
		return this.getVelocity().getY();
	}
	
	@Override
	public void collide(CollisionResult3D r){
		super.collide(r);
		this.addX(r.x());
		this.addY(r.y());
		this.addZ(r.z());
	}
	
	@Override
	public void touchWall(CollisionResult3D result){
		super.touchWall(result);
		// TODO test this formally and make sure this new approach makes sense
		var currentVel = this.getVelocity();

		// Determine the amount of velocity on each axis
		double wallAngle = result.wallAngle();
		double currentAngle = currentVel.getYaw();
		/*
		I don't really understand how to explain in an intuitive way why this works for finding the bounce angle,
		but see the bottom of this file for the working out I did by looking for patterns in the 8 scenarios of hitting axis aligned walls
		 */
		double bounceAngle = wallAngle * 2 - currentAngle;

		// The new horizontal velocity will be the bounce factor times the current velocity
		double velocityMag = currentVel.getHorizontal() * result.material().getWallBounce() * this.getMaterial().getWallBounce();
		double velX = velocityMag * Math.cos(bounceAngle);
		double velZ = velocityMag * Math.sin(bounceAngle);

		this.getNext().attemptSetVelocity(new ZVector3D(velX, currentVel.getY(), velZ, true));
		// TODO potentially move this to the abstract parent method if 3D and 2D don't need a distinction
//		this.getNext().scaleVelocity(-1 * result.material().getWallBounce() * this.getMaterial().getWallBounce());
	}
	
	// TODO figure out if it even makes sense to have previous and next x y z, for now just returning same thing as current x y z
	@Override
	public double getPX(){
		return this.getX();
	}
	
	@Override
	public double getPY(){
		return this.getY();
	}
	
	@Override
	public double getPZ(){
		return this.getZ();
	}
	
	/** @return Current x coordinate of this thing */
	@Override
	public double getX(){
		return this.getPosition().getX();
	}
	
	/** @return Current y coordinate of this thing */
	@Override
	public double getY(){
		return this.getPosition().getY();
	}
	
	/** @return Current z coordinate of this thing */
	@Override
	public double getZ(){
		return this.getPosition().getZ();
	}
	
	// TODO consolidate calls to add and set to be on all axes into one update
	// TODO change all add and set position values to use updates
	// TODO have a formal way to initialize position without having to go through the state system
	// TODO need to figure out where this inheritance is used for setting and what relies on it, restructure so that setting is not relied on, just a delta
	// TODO do the same fixes to EntityThing2D
	/** @param x New x coordinate of this thing */
	@Override
	public void setX(double x){
		this.getNext().attemptSetSingleCoord(new ForceSetElement.X3D(x));
	}
	
	/** @param y New y coordinate of this thing */
	@Override
	public void setY(double y){
		this.getNext().attemptSetSingleCoord(new ForceSetElement.Y3D(y));
	}
	
	/** @param z New z coordinate of this thing */
	@Override
	public void setZ(double z){
		this.getNext().attemptSetSingleCoord(new ForceSetElement.Z3D(z));
	}
	
	/**
	 * Add the given value to the x coordinate
	 * @param x The amount to add
	 */
	public void addX(double x){
		this.getCurrent().addPosition(new ZVector3D(x, 0, 0));
	}
	
	/**
	 * Add the given value to the y coordinate
	 * @param y The amount to add
	 */
	public void addY(double y){
		this.getCurrent().addPosition(new ZVector3D(0, y, 0));
	}
	
	/**
	 * Add the given value to the z coordinate
	 * @param z The amount to add
	 */
	public void addZ(double z){
		this.getCurrent().addPosition(new ZVector3D(0, 0, z));
	}
	
	/** @return The height from the bottom of this entity where it should be able to "see" from, height of the entity by default */
	public double getEyeHeight(){
		return this.getHeight();
	}
	
	/**
	 * Set the position of this camera to the top center of this thing
	 * @param camera The camera to set
	 */
	public void updateCameraPos(GameCamera3D camera){
		camera.setX(this.getX());
		camera.setY(this.getY() + this.getEyeHeight());
		camera.setZ(this.getZ());
	}
	
	@Override
	public ZVector3D zeroVector(){
		return new ZVector3D();
	}
	
	@Override
	public double getGravityAcceleration(){
		return -9.8;
	}
	
	@Override
	public double getClampVelocity(){
		return 1E-12;
	}
}

/*
Touch wall working out to find the equation for bounce angle

45 diff
east (+x) wall angle 90
-x, +z, up 45 -> 135, +90
-x, -z, down 315 -> 225, -90

west (-x) wall angle 90
+x, +z, up 135 -> 45, -90
+x, -z, down 225 -> 315, +90

north (-z) wall angle 90
+x, +z, left 135 -> 225, +90
-x, +z, right 45 -> 315, -90

south (+z) wall angle 90
+x, -z, left 225 -> 135, -90
-x, -z, right 315 -> 45, +90

       axis
       x    z
-x -z  -90  +90
-x +z  +90  -90
+x +z  -90  +90
+x -z  +90  -90


1 diff
east (+x) wall angle 90
-x, +z, up 89 -> 91, +2
-x, -z, down 271 -> 269, -2

west (-x), wall angle 90
+x, +z, up 91 -> 89, -2
+x, -z, down 269-> 271, +2

north (-z), wall angle 0
+x, +z, left 179 -> 181, +2
-x, +z, right 1 -> 359, -2

south (+z), wall angle 0
+x, -z, left 181 -> 179, -2
-x, -z, right 359 -> 1, +2

       axis
       x    z
-x -z  -2  +2
-x +z  +2  -2
+x +z  -2  +2
+x -z  +2  -2


a diff
east (+x) wall angle 90
-x, +z, up 89 -> 91, +2, a -> ((0 + 90) + (0 + 90 - a))
-x, -z, down 271 -> 269, -2, ((180 + 90) - (a - 90 - 180)

west (-x), wall angle 90
+x, +z, up 91 -> 89, -2, a -> ((0 + 90) + (0 + 90 - a))
+x, -z, down 269-> 271, +2 -> ((180 + 90) - (a - 90 - 180))

north (-z), wall angle 180
+x, +z, left 179 -> 181, +2, a -> ((0 + 180) + (0 + 180 - a))
-x, +z, right 1 -> 359, -2, a -> ((180 + 180) + (180 + 180 - a))

south (+z), wall angle 180
+x, -z, left 181 -> 179, a -> ((0 + 180) + (180 + 0 - a))
-x, -z, right 359 -> 1, +2, a -> ((180 + 180) + (180 + 180 - a))


x axis wall, wall angle w
+x, +z, up -> ((0 + w) + (0 + w - a))
+x, -z, down -> ((180 + w) - (a - w - 180))

z axis wall, wall angle w
+x, -z, left, a -> ((0 + w) + (0 + w - a))
-x, -z, right, a -> ((180 + w) + (w + 180 - a))


wall angle w
-, a -> ((0 + w) + (0 + w - a))
+, a -> ((180 + w) + (w + 180 - a))


wall angle w
a -> (2w - a)
a -> (2w - a)


 */