package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.core.utils.FunctionMap;
import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;
import zgame.things.type.HitBox;

import java.util.function.Consumer;

/** An {@link EntityThing} which represents a thing flying through the air */
public abstract class Projectile extends EntityThing{
	
	/**
	 * A mapping of the functions to call when certain object types are hit. This essentially replaces manually defining abstract functions in this class, allowing a specific
	 * function to be called for a specific type
	 */
	private final FunctionMap mappedFuncs;
	
	/**
	 * Create a projectile at the specified location, moving at the given velocity
	 *
	 * @param x The initial x position of the projectile
	 * @param y The initial y position of the projectile
	 * @param launchVelocity The initial velocity of the projectile
	 */
	public Projectile(double x, double y, ZVector launchVelocity){
		super(x, y);
		this.addVelocity(launchVelocity);
		this.mappedFuncs = new FunctionMap();
	}
	
	/**
	 * Add a new function for {@link #mappedFuncs}
	 * @param clazz The class of the type of the object accepted by the function
	 * @param func The function
	 * @param <T> The type of clazz
	 */
	public <T> void addHitFunc(Class<T> clazz, Consumer<T> func){
		this.mappedFuncs.addFunc(clazz, func);
	}
	
	/**
	 * Call a function from {@link #mappedFuncs}.
	 * Does nothing if no function exists
	 *
	 * @param clazz The class of the type of the object accepted by the function
	 * @param thing The object to pass to the function
	 * @param <T> The type of clazz
	 */
	public <T> void hit(Class<T> clazz, T thing){
		this.mappedFuncs.func(clazz, thing);
	}
	
	@Override
	public void tick(Game game, double dt){
		// Do the regular update first
		super.tick(game, dt);
		
		// Check if this projectile hits any of the hitbox things in the game
		// TODO replace this with checkEntityCollision?
		var r = game.getCurrentRoom();
		var things = r.getAllThings().get(HitBox.class);
		for(int i = 0; i < things.size(); i++){
			// Ignore the current thing if the projectile will not hit it
			var thing = things.get(i);
			if(!this.willHit(thing)) continue;
			this.hit(game, thing);
			break;
		}
	}
	
	/**
	 * Called when this {@link Projectile} hits the given {@link HitBox}
	 *
	 * @param game The game where thing was hit
	 * @param thing The {@link HitBox} which was hit
	 */
	public abstract void hit(Game game, HitBox thing);
	
	/**
	 * Determine if this {@link Projectile} will hit the given {@link HitBox} thing when their hitboxes intersect
	 *
	 * @param thing The hitbox to check
	 * @return true thing will hit this, false otherwise
	 */
	public boolean willHit(HitBox thing){
		return this != thing && this.intersects(thing);
	}
	
}
