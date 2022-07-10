package zgame.physics.material;

import zgame.things.entity.EntityThing;
import zgame.world.Room;

/** A class containing instances of {@link Material} for ease of use */
public final class Materials{
	
	/** A {@link Material} which represents no material, i.e., no friction and no bounciness, essentially for anything taking up empty space */
	public static final Material NONE = new MaterialConst(.1, 0, 0, 0);
	/** A {@link Material} with no bounce values, used for the boundary of a {@link Room} */
	public static final Material BOUNDARY = new MaterialConst(MaterialConst.DEFAULT_FRICTION, 0, 0, 0);
	/** A {@link Material} using all the default values. Mainly for simple walls, floors, and ceilings */
	public static final Material DEFAULT = new MaterialConst();
	/** A {@link Material} meant for {@link EntityThing}s, essentially means the entity will react to the properties of what the entity interacts with */
	public static final Material DEFAULT_ENTITY = new MaterialConst(1, 1, 1, 1);
	/** A {@link Material} with high bounce values */
	public static final Material BOUNCE = new MaterialConst(100, 0.9, 0.9, 0.9);
	/** A {@link Material} with a very large friction constant */
	public static final Material HIGH_FRICTION = new MaterialConst(1000000, 0);
	
	/** Cannot instantiate {@link Materials} */
	private Materials(){
	}
	
}
