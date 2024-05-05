package zgame.world;

import zgame.physics.ZVector3D;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.MaterialConst;
import zgame.things.entity.EntityThing3D;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.HitBox3D;

/** A {@link Room} which is made of 3D tiles */
public class Room3D extends Room<HitBox3D, EntityThing3D, ZVector3D, Room3D> implements Bounds3D{
	
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
	public CollisionResponse collide(HitBox3D obj){
		
		// TODO implement real collision
		if(obj.getY() <= 0) {
			obj.setY(0);
			obj.touchFloor(new MaterialConst(0.1, 0, 0, 0));
		}
		else if(obj.isOnGround()) obj.leaveFloor();
		
		return new CollisionResponse();
	}
	
	@Override
	public Class<HitBox3D> getHitBoxType(){
		return HitBox3D.class;
	}
	
	@Override
	public Class<EntityThing3D> getEntityClass(){
		return EntityThing3D.class;
	}
	
}
