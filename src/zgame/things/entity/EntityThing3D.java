package zgame.things.entity;

import zgame.core.graphics.camera.GameCamera3D;
import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResult3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/**
 * An {@link EntityThing} in 3D
 */
public abstract class EntityThing3D extends EntityThing<HitBox3D, EntityThing3D, ZVector3D, Room3D, CollisionResult3D> implements HitBox3D{
	
	/** The x coordinate of the bottom center of this entity thing */
	private double x;
	/** The y coordinate of the bottom center of this entity thing */
	private double y;
	/** The z coordinate of the bottom center of this entity thing */
	private double z;
	
	/** The value of the x coordinate from the last tick */
	private double px;
	/** The value of the y coordinate from the last tick */
	private double py;
	/** The value of the z coordinate from the last tick */
	private double pz;
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param mass See {@link #mass}
	 */
	public EntityThing3D(double mass){
		this(0, 0, 0, mass);
	}
	
	/**
	 * Create a new empty entity with the given mass
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param mass See {@link #mass}
	 */
	public EntityThing3D(double x, double y, double z, double mass){
		super(mass);
		this.x = x;
		this.y = y;
		this.z = z;
		this.px = x;
		this.py = y;
		this.pz = z;
	}
	
	@Override
	public void moveEntity(ZVector3D distance){
		this.px = this.getX();
		this.py = this.getY();
		this.pz = this.getZ();
		this.addX(distance.getX());
		this.addY(distance.getY());
		this.addZ(distance.getZ());
	}
	
	/**
	 * Set the given force name with a force built from the given components.
	 * If the given name doesn't have a force mapped to it yet, then this method automatically adds it to the map
	 *
	 * @param name The name of the force to set
	 * @param x The x component
	 * @param y The y component
	 * @param z The z component
	 * @return The newly set vector object
	 */
	public ZVector3D setForce(String name, double x, double y, double z){
		return this.setForce(name, new ZVector3D(x, y, z));
	}
	
	@Override
	public ZVector3D setVerticalForce(String name, double f){
		return this.setForce(name, 0, f, 0);
	}
	
	@Override
	public double getHorizontalVel(){
		return this.getVelocity().getHorizontal();
	}
	
	@Override
	public void setHorizontalVel(double v){
		var oldVel = this.getVelocity();
		var oldAngle = oldVel.getYaw();
		var newVel = new ZVector3D(Math.cos(oldAngle) * v, oldVel.getY(), Math.sin(oldAngle) * v, true);
		
		this.setVelocity(newVel);
	}
	
	@Override
	public double getVerticalVel(){
		return this.getVelocity().getY();
	}
	
	@Override
	public void setVerticalVel(double v){
		var vel = this.getVelocity();
		this.setVelocity(new ZVector3D(vel.getX(), v, vel.getZ()));
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
		
		this.setVelocity(new ZVector3D(velX, currentVel.getY(), velZ, true));
	}
	
	@Override
	public double getPX(){
		return this.px;
	}
	
	@Override
	public double getPY(){
		return this.py;
	}
	
	@Override
	public double getPZ(){
		return this.pz;
	}
	
	/** @return See {@link #x} */
	@Override
	public double getX(){
		return this.x;
	}
	
	/** @param x See {@link #x} */
	@Override
	public void setX(double x){
		this.x = x;
	}
	
	/**
	 * Add the given value to the x coordinate
	 * @param x The amount to add
	 */
	public void addX(double x){
		this.setX(this.getX() + x);
	}
	
	/** @return See {@link #y} */
	@Override
	public double getY(){
		return this.y;
	}
	
	/** @param y See {@link #y} */
	@Override
	public void setY(double y){
		this.y = y;
	}
	
	/**
	 * Add the given value to the y coordinate
	 * @param y The amount to add
	 */
	public void addY(double y){
		this.setY(this.getY() + y);
	}
	
	/** @return See {@link #z} */
	@Override
	public double getZ(){
		return this.z;
	}
	
	/** @param z See {@link #z} */
	@Override
	public void setZ(double z){
		this.z = z;
	}
	
	/**
	 * Add the given value to the z coordinate
	 * @param z The amount to add
	 */
	public void addZ(double z){
		this.setZ(this.getZ() + z);
	}
	
	/**
	 * Set the position of this camera to the top center of this thing
	 * @param camera The camera to set
	 */
	public void updateCameraPos(GameCamera3D camera){
		camera.setX(this.getX());
		camera.setY(this.getY() + this.getHeight());
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