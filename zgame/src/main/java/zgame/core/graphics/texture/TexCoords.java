package zgame.core.graphics.texture;

import zgame.core.graphics.buffer.VertexBuffer;
import zgame.core.graphics.image.GameImage;

/**
 * An object holding texture coordinates, along with a source texture and mapping
 *
 * @param <M> The type of mapping this object will represent
 */
public abstract class TexCoords<M extends TextureMapping>{
	
	/** The texture that will be mapped to an object */
	private final GameImage texture;
	
	/** The data defining how the texture will be mapped */
	private final M textureMapping;
	
	/** The vertices for the texture coordinates */
	private final float[] data;
	
	/**
	 * Build a new {@link TexCoords}, where the mappings of the textures are the front, back, left, and right faces aligned on the x axis,
	 * and the bottom and top faces below the front and back faces.
	 *
	 * @param dataSize The number of values in the vertices for the texture coordinates
	 * @param texture The texture which will be mapped onto the object
	 * @param textureMapping See {@link #textureMapping}
	 */
	public TexCoords(int dataSize, GameImage texture, M textureMapping){
		this.texture = texture;
		this.textureMapping = textureMapping;
		this.data = new float[dataSize];
		this.initData();
	}
	
	/** Based on the values set in this class, initialize {@link #data} appropriately */
	public abstract void initData();
	
	/** @return See {@link #data} */
	public float[] getData(){
		return this.data;
	}
	
	/** @return See {@link #texture} */
	public GameImage getTexture(){
		return this.texture;
	}
	
	/** @return See {@link #textureMapping} */
	public M getTextureMapping(){
		return this.textureMapping;
	}
	
	/**
	 * Update the given buffer so that the data of this object is assigned to the buffer
	 *
	 * @param buffer The buffer to update the data to
	 */
	public void updateVertexBuffer(VertexBuffer buffer){
		buffer.updateData(this.getData());
	}
	
}
