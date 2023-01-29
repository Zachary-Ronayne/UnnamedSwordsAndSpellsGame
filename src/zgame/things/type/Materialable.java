package zgame.things.type;

import zgame.physics.material.Material;

/** An object which uses a {@link Material} */
public interface Materialable{
	
	/** @return The material which this object is made of */
	Material getMaterial();
	
}
