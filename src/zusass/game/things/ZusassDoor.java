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
		boolean rotate = direction == Direction3D.NORTH || direction == Direction3D.SOUTH;
		if(rotate){
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
		final float IW = 20;
		final float IH = 36;
		final float H = 32;
		final float L = IH - H;
		final float W = IW - L;
		
		// TODO find a better way to handle rotations, including rotating the doorknob correctly, probably make it a config in the file
		final int BACK = rotate ? 0 : 3;
		final int FRONT = rotate ? 1 : 2;
		final int RIGHT = rotate ? 2 : 0;
		final int LEFT = rotate ? 3 : 1;
		boolean swapKnob = direction == Direction3D.SOUTH || direction == Direction3D.WEST;
		final float KNOB_RIGHT = swapKnob ? 0 : W / IW;
		final float KNOB_LEFT = swapKnob ? W / IW : 0;
		
		// Back face
		data[BACK][0][0] = KNOB_LEFT;
		data[BACK][0][1] = L / IH;
		data[BACK][1][0] = KNOB_RIGHT;
		data[BACK][1][1] = L / IH;
		data[BACK][2][0] = KNOB_RIGHT;
		data[BACK][2][1] = 1;
		data[BACK][3][0] = KNOB_LEFT;
		data[BACK][3][1] = 1;
		
		// Front face
		data[FRONT][0][0] = KNOB_LEFT;
		data[FRONT][0][1] = L / IH;
		data[FRONT][1][0] = KNOB_RIGHT;
		data[FRONT][1][1] = L / IH;
		data[FRONT][2][0] = KNOB_RIGHT;
		data[FRONT][2][1] = 1;
		data[FRONT][3][0] = KNOB_LEFT;
		data[FRONT][3][1] = 1;
		
		// Right face
		data[RIGHT][0][0] = W / IW;
		data[RIGHT][0][1] = L / IH;
		data[RIGHT][1][0] = 1;
		data[RIGHT][1][1] = L / IH;
		data[RIGHT][2][0] = 1;
		data[RIGHT][2][1] = 1;
		data[RIGHT][3][0] = W / IW;
		data[RIGHT][3][1] = 1;
		
		// Left face
		data[LEFT][0][0] = W / IW;
		data[LEFT][0][1] = L / IH;
		data[LEFT][1][0] = 1;
		data[LEFT][1][1] = L / IH;
		data[LEFT][2][0] = 1;
		data[LEFT][2][1] = 1;
		data[LEFT][3][0] = W / IW;
		data[LEFT][3][1] = 1;
		
		// Top face
		data[4][0][0] = 0;
		data[4][0][1] = 0;
		data[4][1][0] = W / IW;
		data[4][1][1] = 0;
		data[4][2][0] = W / IW;
		data[4][2][1] = L / IH;
		data[4][3][0] = 0;
		data[4][3][1] = L / IH;
		
		// Bottom face
		data[5][0][0] = 0;
		data[5][0][1] = 0;
		data[5][1][0] = W / IW;
		data[5][1][1] = 0;
		data[5][2][0] = W / IW;
		data[5][2][1] = L / IH;
		data[5][3][0] = 0;
		data[5][3][1] = L / IH;
		
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
