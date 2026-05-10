package zgame.things.entity.state.vector;

import zgame.physics.ZVector;
import zgame.physics.ZVector2D;
import zgame.physics.ZVector3D;

// TODO does it make sense to have a set update? May want to rethink how these updates will work

/** A vector update to be some amount for a single coordinate */
public abstract class ForceSetElement<V extends ZVector<V>> implements VectorUpdate<V>{
	
	/** The new value to set the coordinate to. Priority will be dictated by the magnitude, higher magnitude means being applied last */
	private final double position;
	
	/** @param position See {@link #position} */
	public ForceSetElement(double position){
		this.position = position;
	}
	
	/** @return See {@link #position} */
	public double getPosition(){
		return this.position;
	}
	
	@Override
	public double priority(){
		return 11 + (1.0 / Math.abs(this.position));
	}
	
	// TODO remove all this, explicit force setting shouldn't need to be a thing, doing this as a temporary transition to moving to the state system
	public static class X2D extends ForceSetElement<ZVector2D>{
		public X2D(double position){
			super(position);
		}
		@Override
		public ZVector2D apply(ZVector2D existing){
			return new ZVector2D(this.getPosition(), existing.getY());
		}
	}
	public static class Y2D extends ForceSetElement<ZVector2D>{
		public Y2D(double position){
			super(position);
		}
		@Override
		public ZVector2D apply(ZVector2D existing){
			return new ZVector2D(existing.getX(), this.getPosition());
		}
	}
	public static class X3D extends ForceSetElement<ZVector3D>{
		public X3D(double position){
			super(position);
		}
		@Override
		public ZVector3D apply(ZVector3D existing){
			return new ZVector3D(this.getPosition(), existing.getY(), existing.getZ());
		}
	}
	public static class Y3D extends ForceSetElement<ZVector3D>{
		public Y3D(double position){
			super(position);
		}
		@Override
		public ZVector3D apply(ZVector3D existing){
			return new ZVector3D(existing.getX(), this.getPosition(), existing.getZ());
		}
	}
	public static class Z3D extends ForceSetElement<ZVector3D>{
		public Z3D(double position){
			super(position);
		}
		@Override
		public ZVector3D apply(ZVector3D existing){
			return new ZVector3D(existing.getX(), existing.getY(), this.getPosition());
		}
	}
	
}
