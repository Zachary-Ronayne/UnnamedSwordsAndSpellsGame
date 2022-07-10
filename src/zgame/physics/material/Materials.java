package zgame.physics.material;

import zgame.world.Room;

/** A class containing instances of {@link Material} for ease of use */
public final class Materials{

	/** A {@link Material} with no bounce values, used for the boundary of a {@link Room} */
	public static final Material BOUNDARY = new MaterialConst(MaterialConst.DEFAULT_FRICTION, 0, 0, 0);
	/** A {@link Material} using all the default values */
	public static final Material DEFAULT = new MaterialConst();
	/** A {@link Material} with high bounce values */
	public static final Material BOUNCE = new MaterialConst(100, 0.9, 0.9, 0.9);
	/** A {@link Material} with a very large friction constant */
	public static final Material HIGH_FRICTION = new MaterialConst(1000000, 0);

	/** Cannot instantiate {@link Materials} */
	private Materials(){
	}

}
