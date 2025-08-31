package zusass.game.things;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.TexCoordsRectPrism3D;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.image.ImageManager;
import zgame.core.utils.ZConfig;
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
		// Set width and height based on direction
		double longSide = 0.5;
		double shortSide = 0.125;
		var direction = this.getFacingDirection();
		boolean facingZ = direction == Direction3D.NORTH || direction == Direction3D.SOUTH;
		boolean facingPos = direction == Direction3D.NORTH || direction == Direction3D.WEST;
		if(facingZ){
			this.setWidth(longSide);
			this.setLength(shortSide);
		}
		else{
			this.setWidth(shortSide);
			this.setLength(longSide);
		}
		
		var data = new float[6][4][2];
		
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
		
		// Indexes for coordinates
		final int X = 0;
		final int Y = 1;
		
		// TODO find a better way to handle rotations, probably render based always on the same north facing rect bounds, but let the hitbox change
		final int FRONT = facingPos ? (facingZ ? 0 : 2) : (facingZ ? 1 : 3);
		final int BACK = facingPos ? (facingZ ? 1 : 3) : (facingZ ? 0 : 2);
		final int LEFT = facingPos ? (facingZ ? 2 : 1) : (facingZ ? 3 : 0);
		final int RIGHT = facingPos ? (facingZ ? 3 : 0) : (facingZ ? 2 : 1);
		final int TOP = 4;
		final int BOTTOM = 5;
		
		final int TOP_X1Y1 = facingPos ? (facingZ ? 0 : 1) : (facingZ ? 2 : 3);
		final int TOP_X2Y1 = facingPos ? (facingZ ? 1 : 2) : (facingZ ? 3 : 0);
		final int TOP_X2Y2 = facingPos ? (facingZ ? 2 : 3) : (facingZ ? 0 : 1);
		final int TOP_X1Y2 = facingPos ? (facingZ ? 3 : 0) : (facingZ ? 1 : 2);
		
		final int BOT_X1Y1 = facingPos ? (facingZ ? 0 : 3) : (facingZ ? 2 : 1);
		final int BOT_X2Y1 = facingPos ? (facingZ ? 1 : 0) : (facingZ ? 3 : 2);
		final int BOT_X2Y2 = facingPos ? (facingZ ? 2 : 1) : (facingZ ? 0 : 3);
		final int BOT_X1Y2 = facingPos ? (facingZ ? 3 : 2) : (facingZ ? 1 : 0);
		
		// Front face
		data[FRONT][0][X] = 0;
		data[FRONT][0][Y] = L / IH;
		data[FRONT][1][X] = W / IW;
		data[FRONT][1][Y] = L / IH;
		data[FRONT][2][X] = W / IW;
		data[FRONT][2][Y] = 1;
		data[FRONT][3][X] = 0;
		data[FRONT][3][Y] = 1;
		
		// Back face
		data[BACK][0][X] = W / IW;
		data[BACK][0][Y] = L / IH;
		data[BACK][1][X] = (W + W) / IW;
		data[BACK][1][Y] = L / IH;
		data[BACK][2][X] = (W + W) / IW;
		data[BACK][2][Y] = 1;
		data[BACK][3][X] = W / IW;
		data[BACK][3][Y] = 1;
		
		// Left face
		data[LEFT][0][X] = (W + W ) / IW;
		data[LEFT][0][Y] = L / IH;
		data[LEFT][1][X] = (W + W + L) / IW;
		data[LEFT][1][Y] = L / IH;
		data[LEFT][2][X] = (W + W + L) / IW;
		data[LEFT][2][Y] = 1;
		data[LEFT][3][X] = (W + W) / IW;
		data[LEFT][3][Y] = 1;
		
		// Right face
		data[RIGHT][0][X] = (W + W + L) / IW;
		data[RIGHT][0][Y] = L / IH;
		data[RIGHT][1][X] = (W + W + L + L) / IW;
		data[RIGHT][1][Y] = L / IH;
		data[RIGHT][2][X] = (W + W + L + L) / IW;
		data[RIGHT][2][Y] = 1;
		data[RIGHT][3][X] = (W + W + L) / IW;
		data[RIGHT][3][Y] = 1;
		
		// Top face
		data[TOP][TOP_X1Y1][X] = 0;
		data[TOP][TOP_X1Y1][Y] = 0;
		data[TOP][TOP_X2Y1][X] = W / IW;
		data[TOP][TOP_X2Y1][Y] = 0;
		data[TOP][TOP_X2Y2][X] = W / IW;
		data[TOP][TOP_X2Y2][Y] = L / IH;
		data[TOP][TOP_X1Y2][X] = 0;
		data[TOP][TOP_X1Y2][Y] = L / IH;
		
		// Bottom face
		data[BOTTOM][BOT_X1Y1][X] = W / IW;
		data[BOTTOM][BOT_X1Y1][Y] = 0;
		data[BOTTOM][BOT_X2Y1][X] = (W + W) / IW;
		data[BOTTOM][BOT_X2Y1][Y] = 0;
		data[BOTTOM][BOT_X2Y2][X] = (W + W) / IW;
		data[BOTTOM][BOT_X2Y2][Y] = L / IH;
		data[BOTTOM][BOT_X1Y2][X] = W / IW;
		data[BOTTOM][BOT_X1Y2][Y] = L / IH;
		
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
			r.setColor(new ZColor(0.8));
		}
		r.drawRectPrismTex(new RectRender3D(this.getBounds()), ImageManager.image("door"), this.textureCoordinates);
		if(canClick) r.popShader();
	}
	
}
