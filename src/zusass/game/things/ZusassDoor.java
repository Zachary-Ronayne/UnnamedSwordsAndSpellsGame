package zusass.game.things;

import zgame.core.Game;
import zgame.core.graphics.*;
import zgame.core.sound.SoundSource;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZRect3D;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zgame.things.still.Door3D;
import zgame.things.type.bounds.ModifiableRectDims3D;
import zgame.world.Direction3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;
import zusass.graphics.ZusassTexCoordsRectPrism;
import zusass.utils.ZusassImages;
import zusass.utils.ZusassSounds;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door3D implements ZThingClickDetector, ModifiableRectDims3D{
	
	/** Texture coordinates used to define how this door is drawn */
	private final ZusassTexCoordsRectPrism textureCoordinates;
	
	/** The direction this door should be facing towards */
	private final Direction3D facingDirection;
	
	/** Source for playing a sound when the door opens */
	private SoundSource doorOpenSound;
	
	/**
	 * Create a new door at the given position
	 *
	 * @param x See {@link #x}
	 * @param y See {@link #y}
	 * @param z See {@link #z}
	 * @param direction The facing direction of the door, must be one of the cardinal directions
	 */
	public ZusassDoor(double x, double y, double z, Direction3D direction){
		super(x, y, z, 1, 1, 1);
		if(!direction.isCardinal()){
			var defaultDirection = Direction3D.NORTH;
			ZConfig.error("ZusassDoor cannot use non cardinal direction ", direction.name(), " defaulting to ", defaultDirection);
			direction = defaultDirection;
		}
		this.facingDirection = direction;
		this.textureCoordinates = new ZusassTexCoordsRectPrism(ZusassImages.DOOR, this, 1.0 / 2.0, 1.0, 1.0 / 8.0, this.facingDirection);
		
		this.doorOpenSound = new SoundSource();
	}
	
	/**
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 *
	 * @param room The room used by the tick method
	 */
	@Override
	public void handleZusassPress(ZusassRoom room){
		var zgame = ZusassGame.get();
		var player = zgame.getPlayer();
		this.enterRoom(zgame.getCurrentRoom(), player);
	}
	
	@Override
	public boolean enterRoom(Room3D r, EntityThing3D thing){
		var success = super.enterRoom(r, thing);
		
		if(success){
			// Use the position of the thing that entered the room, rather than the door itself
			this.doorOpenSound.updatePosition(thing.getX(), thing.centerY(), thing.getZ());
			this.doorOpenSound.updatePitch(0.9 + 0.2 * Math.random());
			Game.get().playEffect(this.doorOpenSound, ZusassSounds.DOOR_OPEN);
		}
		
		return success;
	}
	
	/** Convenience method that calls {@link #enterRoom(Room3D, EntityThing3D)} without a need to type cast */
	public boolean enterRoom(ZusassRoom r, EntityThing3D thing){
		return this.enterRoom((Room3D)r, thing);
	}
	
	/** @return See {@link #facingDirection} */
	public Direction3D getFacingDirection(){
		return this.facingDirection;
	}
	
	@Override
	public void render(Renderer r){
		this.textureCoordinates.renderSelectable(r, this);
	}
	
	@Override
	public ZRect3D getBounds(){
		return super.getBounds();
	}
	
}
