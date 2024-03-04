package zgame.world;

import zgame.physics.collision.CollisionResponse;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox;
import zgame.things.type.bounds.HitBox3D;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room<HitBox3D> implements Bounds3D{
	
	// TODO implement and use in the game demo
	
	public Room3D(){
		super();
	}
	
	@Override
	public double getX(){
		return 0;
	}
	
	@Override
	public double getY(){
		return 0;
	}
	
	@Override
	public double getZ(){
		return 0;
	}
	
	@Override
	public double getWidth(){
		return 0;
	}
	
	@Override
	public double getHeight(){
		return 0;
	}
	
	@Override
	public double getLength(){
		return 0;
	}
	
	@Override
	public CollisionResponse collide(HitBox<HitBox3D> obj){
		return new CollisionResponse();
	}
	
	@Override
	public Class<HitBox3D> getHitBoxType(){
		return HitBox3D.class;
	}
}
