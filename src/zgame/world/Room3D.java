package zgame.world;

import zgame.physics.collision.CollisionResponse;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room implements Bounds3D{
	
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
	public CollisionResponse collide(HitBox obj){
		return new CollisionResponse();
	}
	
}
