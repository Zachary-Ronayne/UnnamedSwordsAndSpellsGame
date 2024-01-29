package zgame.world;

import zgame.physics.collision.CollisionResponse;
import zgame.things.type.HitBox;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room{
	
	// TODO implement and use in the game demo
	
	public Room3D(){
		super();
	}
	
	@Override
	public double maxX(){
		return 0;
	}
	
	@Override
	public double maxY(){
		return 0;
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
	public CollisionResponse collide(HitBox obj){
		return new CollisionResponse();
	}
}
