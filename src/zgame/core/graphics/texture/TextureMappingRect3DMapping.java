package zgame.core.graphics.texture;

import com.google.gson.JsonObject;

/** A {@link TextureMapping} that holds data used for mapping a rectangular prism */
public class TextureMappingRect3DMapping extends TextureMapping{
	
	/** The json key used to specify the {@link #height} */
	public static final String HEIGHT_KEY = "height";
	
	/** The height, in pixels, of the object that will be mapped */
	private int height;
	
	/**
	 * Create an asset which comes from the given path
	 *
	 * @param path The file path to the asset
	 * @param obj The data to build the object from
	 */
	public TextureMappingRect3DMapping(String path, JsonObject obj){
		super(path, obj);
	}
	
	/** @return See {@link #height} */
	public int getHeight(){
		return this.height;
	}
	
	@Override
	public boolean init(JsonObject obj){
		this.height = obj.get(HEIGHT_KEY).getAsInt();
		
		return true;
	}
}
