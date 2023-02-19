package zgame.things.entity.projectile;

import zgame.core.Game;
import zgame.core.utils.FunctionMap;
import zgame.physics.ZVector;
import zgame.things.entity.EntityThing;
import zgame.things.still.tiles.Tile;
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
		var hit = false;
		var things = r.getAllThings().get(HitBox.class);
		for(int i = 0; i < things.size(); i++){
			// Ignore the current thing if the projectile will not hit it
			var thing = things.get(i);
			if(!this.willHit(thing)) continue;
			this.hit(game, thing);
			hit = true;
			break;
		}
		
		// Check if this projectile hits any tiles, only if it didn't hit a hitbox
		if(hit) return;
		// TODO find a way to abstract this
		int minX = r.tileX(this.getX());
		int minY = r.tileY(this.getY());
		int maxX = r.tileX(this.maxX());
		int maxY = r.tileY(this.maxY());
		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				// Ignore the current tile if the projectile will not hit it
				var tile = r.getTileUnchecked(x, y);
				if(!this.willHit(tile)) continue;
				this.hit(game, tile);
				break;
			}
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
	 * Called when this {@link Projectile} hits the given {@link Tile}
	 *
	 * @param game The game where tile was hit
	 * @param thing The {@link Tile} which was hit
	 */
	public abstract void hit(Game game, Tile thing);
	
	/**
	 * Determine if this {@link Projectile} will hit the given {@link HitBox} thing when their hitboxes intersect
	 *
	 * @param thing The hitbox to check
	 * @return true thing will hit this, false otherwise
	 */
	public boolean willHit(HitBox thing){
		return this != thing && this.intersects(thing);
	}
	
	/**
	 * Determine if this {@link Projectile} will hit the given {@link Tile} thing when their bounds intersect
	 *
	 * @param tile The tile to check
	 * @return true tile will hit this, false otherwise
	 */
	public abstract boolean willHit(Tile tile);
	
}
