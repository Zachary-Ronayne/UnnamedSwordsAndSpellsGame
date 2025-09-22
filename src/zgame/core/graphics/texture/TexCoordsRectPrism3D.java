package zgame.core.graphics.texture;

import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.RotRender3D;
import zgame.core.graphics.image.GameImage;
import zgame.core.graphics.image.ImageManager;
import zgame.core.utils.ZMath;
import zgame.things.type.bounds.ModifiableRectDims3D;
import zgame.world.Direction3D;

/**
 * An object holding texture coordinates for a rectangular prism
 * <br/>
 * The vertices will be flattened to a 1D array. Indexed as data[face][vertex][coordinate],
 * <br/>
 * face, length 6, is indexed as, [0-5], [front, back, left, right, top, bottom],
 * Where with no rotations, this object is facing north, and the front face is the north most face of the object,
 * meaning if an observer is also facing north and can see the object, i.e. the observer is behind the object, then
 * the observer will see the back face of the object
 * <br/>
 * vertex, length 4, is indexed as {@link Renderer}'s standard texture coordinates, bottom left, bottom right, upper right, upper left
 * <br/>
 * coordinate, length 2, is indexed as x then y
 */
public class TexCoordsRectPrism3D extends TexCoords<TextureMappingRect3DMapping>{
	
	/** The width, in pixels, of the texture mapped to the object */
	private int pixelWidth;
	/** The height, in pixels, of the texture mapped to the object */
	private int pixelHeight;
	/** The height, in pixels, of the texture mapped to the object */
	private int pixelLength;
	
	/** The object holding data used for rendering this object */
	private RectRender3D rectRender;
	
	/**
	 * Build a new {@link TexCoordsRectPrism3D}, where the mappings of the textures are the front, back, left, and right faces aligned on the x axis,
	 * and the bottom and top faces below the front and back faces.
	 *
	 * @param name The name to use for both the texture and mapping, both must've been loaded before this method can process correctly
	 * @param bounds The object used to obtain bounds. This constructor will set the bounds of this object to be scaled such that the height is 1, and the width and length
	 * 		are scaled relative to the pixel size of the textures of this object
	 * @param scaleX The size of the x axis of the object based on its dimensions from the image and mapping
	 * @param scaleY The size of the y axis of the object based on its dimensions from the image and mapping
	 * @param scaleZ The size of the z axis of the object based on its dimensions from the image and mapping
	 * @param direction The direction this object should face
	 */
	public TexCoordsRectPrism3D(String name, ModifiableRectDims3D bounds, double scaleX, double scaleY, double scaleZ, Direction3D direction){
		this(ImageManager.image(name), TextureMappingManager.rect3D(name), bounds, scaleX, scaleY, scaleZ, direction);
	}
	
	/**
	 * Build a new {@link TexCoordsRectPrism3D}, where the mappings of the textures are the front, back, left, and right faces aligned on the x axis,
	 * and the bottom and top faces below the front and back faces.
	 *
	 * @param texture The texture which will be mapped onto the object
	 * @param textureMapping See {@link #textureMapping}
	 * @param bounds The object used to obtain bounds. This constructor will set the bounds of this object to be scaled such that the height is 1, and the width and length
	 * 		are scaled relative to the pixel size of the textures of this object
	 * @param scaleX The size of the x axis of the object based on its dimensions from the image and mapping
	 * @param scaleY The size of the y axis of the object based on its dimensions from the image and mapping
	 * @param scaleZ The size of the z axis of the object based on its dimensions from the image and mapping
	 * @param direction The direction this object should face
	 */
	public TexCoordsRectPrism3D(GameImage texture, TextureMappingRect3DMapping textureMapping, ModifiableRectDims3D bounds, double scaleX, double scaleY, double scaleZ, Direction3D direction){
		// 6 faces, 4 vertices per face, 2 coordinates per vertex
		super(6 * 4 * 2, texture, textureMapping);
		this.initData();
		this.initRectRender(bounds, scaleX, scaleY, scaleZ, direction);
	}
	
	/**
	 * Initialize {@link #rectRender} for use in rendering. Once initialized, the size of the render bounds can be used to set the dimensions of the object using these texture
	 * coordinates
	 *
	 * @param bounds The object used to obtain bounds. This method will set the bounds of this object to be scaled such that the height is 1, and the width and length are
	 * 		scaled relative to the pixel size of the textures of this object
	 * @param scaleX The size of the x axis of the object based on its dimensions from the image and mapping
	 * @param scaleY The size of the y axis of the object based on its dimensions from the image and mapping
	 * @param scaleZ The size of the z axis of the object based on its dimensions from the image and mapping
	 * @param direction The direction this object should face
	 */
	public void initRectRender(ModifiableRectDims3D bounds, double scaleX, double scaleY, double scaleZ, Direction3D direction){
		this.rectRender = new RectRender3D(bounds.getBounds());
		
		double height = scaleY;
		// By default, the long side is the width, i.e. x axis
		double longSide = scaleX;
		// By default, the short side is the length, i.e. z axis
		double shortSide = scaleZ;
		
		// Set the length or width appropriately depending on which axis this is facing
		boolean facingZ = direction == Direction3D.NORTH || direction == Direction3D.SOUTH;
		if(facingZ){
			bounds.setWidth(longSide);
			bounds.setLength(shortSide);
		}
		else{
			bounds.setWidth(shortSide);
			bounds.setLength(longSide);
		}
		bounds.setHeight(height);
		
		// Rotation will be based on the facing direction. For rendering, the long side will always be the width, regardless of rotation
		// If hitboxes are implemented to allow rotations, i.e. not just axis aligned, then this process will need to be changed
		this.rectRender.setWidth(longSide);
		this.rectRender.setHeight(height);
		this.rectRender.setLength(shortSide);
		var rot = new RotRender3D();
		rot.setRotY(direction.getYaw() - ZMath.PI_BY_2);
		this.rectRender.setRot(rot);
	}
	
	/** Based on the values set in this class, initialize {@link #data} appropriately */
	@Override
	public void initData(){
		
		// Constants from the image size
		// Image width
		final float IW = this.getTexture().getWidth();
		// Image height
		final float IH = this.getTexture().getHeight();
		// Height of texture on object
		final float H = this.getTextureMapping().getHeight();
		// Length of texture on object, based on texture format
		final float L = IH - H;
		// Width of texture on object
		final float W = IW * 0.5f - L;
		
		this.pixelWidth = (int)W;
		this.pixelHeight = (int)H;
		this.pixelLength = (int)L;
		
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
		var data = new float[6][4][2];
		
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
		data[LEFT][BOT_LEFT][X] = (W + W) / IW;
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
		
		var vertices = this.getData();
		
		int i = 0;
		for(int f = 0; f < 6; f++){
			for(int v = 0; v < 4; v++){
				for(int c = 0; c < 2; c++){
					vertices[i] = data[f][v][c];
					i++;
				}
			}
		}
	}
	
	/** @return See {@link #pixelWidth} */
	public int getPixelWidth(){
		return this.pixelWidth;
	}
	
	/** @return See {@link #pixelHeight} */
	public int getPixelHeight(){
		return this.pixelHeight;
	}
	
	/** @return See {@link #pixelLength} */
	public int getPixelLength(){
		return this.pixelLength;
	}
	
	/** @return See {@link #rectRender} */
	public RectRender3D getRectRender(){
		return this.rectRender;
	}
}
