package zgame.things.entity.state;

import zgame.physics.V3D;
import zgame.physics.collision.Collision3D;
import zgame.physics.material.Material;
import zgame.things.core.EntityThing3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.things.type.bounds.HitboxType;

// TODO is the the correct pattern to follow?
public class EntityState3D extends EntityState<V3D> implements HitBox3D{
	
	private final EntityThing3D entity;
	
	public EntityState3D(EntityThing3D entity, V3D zeroVector, double gravityAcceleration, double clampVelocity){
		super(zeroVector, gravityAcceleration, clampVelocity);
		this.entity = entity;
	}
	
	// TODO probably make this not dumb with implementing so many methods, this is a placeholder during the transition to the state system
	@Override
	public double getX(){
		return this.getPosition().getX();
	}
	
	@Override
	public double getY(){
		return this.getPosition().getY();
	}
	
	@Override
	public double getZ(){
		return this.getPosition().getZ();
	}
	
	@Override
	public double maxX(){
		return 0;
	}
	
	@Override
	public double minX(){
		return 0;
	}
	
	@Override
	public double maxY(){
		return 0;
	}
	
	@Override
	public double minY(){
		return 0;
	}
	
	@Override
	public double maxZ(){
		return 0;
	}
	
	@Override
	public double minZ(){
		return 0;
	}
	
	// TODO move dimensions to entity state
	@Override
	public double getWidth(){
		return this.entity.getWidth();
	}
	
	@Override
	public double getHeight(){
		return this.entity.getHeight();
	}
	
	@Override
	public double getLength(){
		return this.entity.getLength();
	}
	
	// TODO remove most of these methods, the entity state shouldn't need to care about them, for now just making them use the entity itself
	@Override
	public HitboxType getHitboxType(){
		return this.entity.getHitboxType();
	}
	
	// TODO state probably shouldn't be uuidable
	@Override
	public String getUuid(){
		return this.entity.getUuid();
	}
	
	@Override
	public double getGravityDragReferenceArea(){
		return this.entity.getGravityDragReferenceArea();
	}
	
	@Override
	public boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		return this.entity.intersectsRect(x, y, z, width, height, length);
	}
	
	@Override
	public boolean intersectsCylinder(double x, double y, double z, double radius, double height){
		return this.entity.intersectsCylinder(x, y, z, radius, height);
	}
	
	@Override
	public boolean intersectsSphere(double x, double y, double z, double radius){
		return this.entity.intersectsSphere(x, y, z, radius);
	}
	
	@Override
	public Collision3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m, boolean[] collisionFaces){
		return this.entity.calculateRectCollision(x, y, z, width, height, length, m, collisionFaces);
	}
}
