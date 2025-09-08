package zusass.game.things;

import zgame.core.graphics.*;
import zgame.core.graphics.image.ImageManager;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZMath;
import zgame.things.entity.EntityThing3D;
import zgame.things.still.Door;
import zgame.things.still.Door3D;
import zgame.world.Direction3D;
import zgame.world.Room3D;
import zusass.ZusassGame;
import zusass.game.ZusassRoom;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door3D implements ZThingClickDetector{
	
	/** Texture coordinates used to define how this door is drawn */
	private TexCoordsRectPrism3D textureCoordinates;
	
	/** The direction this door should be facing towards */
	private final Direction3D facingDirection;
	
	/** The bounds used when determining how this door should be rendered */
	private RectRender3D renderRect;
	
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
			direction = Direction3D.NORTH;
			ZConfig.error("ZusassDoor cannot use non cardinal direction ", direction.name(), " defaulting to ", direction.name());
		}
		this.facingDirection = direction;
		this.updateFacingDimensions();
	}
	
	/** Update the dimensions of this door based on the current value of {@link #facingDirection} */
	private void updateFacingDimensions(){
		// TODO load these from a file based on configuration for how big each texture is instead of hard coding all of this crap
		// Constants from the image size
		// Image width
		final float IW = 40;
		// Image height
		final float IH = 36;
		// Width of texture on object
		final float W = 16;
		// Height of texture on object
		final float H = 32;
		// Length of texture on object, based on texture format
		final float L = IH - H;
		
		// Scale door assuming that 1 would be the whole width
		double doorScale = 0.5;
		double longSide = doorScale;
		double shortSide = doorScale / W * L;
		
		// Set width and height based on direction
		var direction = this.getFacingDirection();
		boolean facingZ = direction == Direction3D.NORTH || direction == Direction3D.SOUTH;
		if(facingZ){
			this.setWidth(longSide);
			this.setLength(shortSide);
		}
		else{
			this.setWidth(shortSide);
			this.setLength(longSide);
		}
		
		// Rotation will be based on the facing direction
		var renderBounds = this.getBounds();
		renderBounds.setWidth(longSide);
		renderBounds.setLength(shortSide);
		this.renderRect = new RectRender3D(renderBounds);
		var rot = new RotRender3D();
		rot.setRotY(this.facingDirection.getYaw() - ZMath.PI_BY_2);
		this.renderRect.setRot(rot);
		
		var data = new float[6][4][2];
		
		// Indexes for coordinates
		final int X = 0;
		final int Y = 1;
		final int FRONT = 0;
		final int BACK = 1;
		final int LEFT = 2;
		final int RIGHT = 3;
		final int TOP = 4;
		final int BOTTOM = 5;
		final int BOT_LEFT = 0;
		final int BOT_RIGHT = 1;
		final int TOP_RIGHT = 2;
		final int TOP_LEFT = 3;
		
		// Front face
		data[FRONT][BOT_LEFT][X] = 0;
		data[FRONT][BOT_LEFT][Y] = L / IH;
		data[FRONT][BOT_RIGHT][X] = W / IW;
		data[FRONT][BOT_RIGHT][Y] = L / IH;
		data[FRONT][TOP_RIGHT][X] = W / IW;
		data[FRONT][TOP_RIGHT][Y] = 1;
		data[FRONT][TOP_LEFT][X] = 0;
		data[FRONT][TOP_LEFT][Y] = 1;
		
		// Back face
		data[BACK][BOT_LEFT][X] = W / IW;
		data[BACK][BOT_LEFT][Y] = L / IH;
		data[BACK][BOT_RIGHT][X] = (W + W) / IW;
		data[BACK][BOT_RIGHT][Y] = L / IH;
		data[BACK][TOP_RIGHT][X] = (W + W) / IW;
		data[BACK][TOP_RIGHT][Y] = 1;
		data[BACK][TOP_LEFT][X] = W / IW;
		data[BACK][TOP_LEFT][Y] = 1;
		
		// Left face
		data[LEFT][BOT_LEFT][X] = (W + W ) / IW;
		data[LEFT][BOT_LEFT][Y] = L / IH;
		data[LEFT][BOT_RIGHT][X] = (W + W + L) / IW;
		data[LEFT][BOT_RIGHT][Y] = L / IH;
		data[LEFT][TOP_RIGHT][X] = (W + W + L) / IW;
		data[LEFT][TOP_RIGHT][Y] = 1;
		data[LEFT][TOP_LEFT][X] = (W + W) / IW;
		data[LEFT][TOP_LEFT][Y] = 1;
		
		// Right face
		data[RIGHT][BOT_LEFT][X] = (W + W + L) / IW;
		data[RIGHT][BOT_LEFT][Y] = L / IH;
		data[RIGHT][BOT_RIGHT][X] = (W + W + L + L) / IW;
		data[RIGHT][BOT_RIGHT][Y] = L / IH;
		data[RIGHT][TOP_RIGHT][X] = (W + W + L + L) / IW;
		data[RIGHT][TOP_RIGHT][Y] = 1;
		data[RIGHT][TOP_LEFT][X] = (W + W + L) / IW;
		data[RIGHT][TOP_LEFT][Y] = 1;
		
		// Top face
		data[TOP][BOT_LEFT][X] = 0;
		data[TOP][BOT_LEFT][Y] = 0;
		data[TOP][BOT_RIGHT][X] = W / IW;
		data[TOP][BOT_RIGHT][Y] = 0;
		data[TOP][TOP_RIGHT][X] = W / IW;
		data[TOP][TOP_RIGHT][Y] = L / IH;
		data[TOP][TOP_LEFT][X] = 0;
		data[TOP][TOP_LEFT][Y] = L / IH;
		
		// Bottom face
		data[BOTTOM][BOT_LEFT][X] = W / IW;
		data[BOTTOM][BOT_LEFT][Y] = 0;
		data[BOTTOM][BOT_RIGHT][X] = (W + W) / IW;
		data[BOTTOM][BOT_RIGHT][Y] = 0;
		data[BOTTOM][TOP_RIGHT][X] = (W + W) / IW;
		data[BOTTOM][TOP_RIGHT][Y] = L / IH;
		data[BOTTOM][TOP_LEFT][X] = W / IW;
		data[BOTTOM][TOP_LEFT][Y] = L / IH;
		
		this.textureCoordinates = new TexCoordsRectPrism3D(data);
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
		return super.enterRoom(r, thing);
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
		var zgame = ZusassGame.get();
		double clickDistance = this.findClickDistance(zgame.getPlayer());
		double maxClickRange = zgame.getPlayer().getClickRange();
		
		// Check for tiles
		double tileDistance = -1;
		if(clickDistance >= 0){
			var room = zgame.getCurrentRoom();
			if(room != null) tileDistance = room.findTileClickDistance(zgame.getPlayer());
		}
		
		boolean canClick = clickDistance <= maxClickRange && clickDistance >= 0 && (tileDistance < 0 || tileDistance > clickDistance);
		if(canClick){
			r.pushTextureTintShader();
			r.setColor(new ZColor(0.7));
		}
		r.drawRectPrismTex(this.renderRect, ImageManager.image("door"), this.textureCoordinates);
		if(canClick) r.popShader();
	}
	
}
