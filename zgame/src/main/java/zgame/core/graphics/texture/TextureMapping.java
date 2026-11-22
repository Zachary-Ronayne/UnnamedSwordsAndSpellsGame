package zgame.core.graphics.texture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import zgame.core.asset.Asset;
import zgame.core.file.Saveable;
import zgame.core.utils.ZAssetUtils;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZFilePaths;
import zgame.core.utils.ZStringUtils;

import java.nio.ByteBuffer;

/** A texture that will be mapped onto an object */
public abstract class TextureMapping extends Asset{
	
	/** The json key used to specify the type of the mapped texture */
	public static final String TYPE_KEY = "type";
	
	/**
	 * Create an asset which comes from the given path
	 *
	 * @param path The file path to the asset
	 * @param obj The data to build the object from
	 */
	protected TextureMapping(String path, JsonObject obj){
		super(path);
		this.init(obj);
	}
	
	/**
	 * Initialize the data needed for this texture from the given json object
	 *
	 * @param obj The object to load from
	 * @return true on a successful load, false otherwise
	 */
	public abstract boolean init(JsonObject obj);
	
	/**
	 * Crate a mapped texture from the data at the given file name from {@link ZFilePaths#textureMappings()}
	 * @param name The name to get the file from
	 * @return The created object
	 */
	public static TextureMapping create(String name){
		var path = ZStringUtils.concat(ZFilePaths.textureMappings(), name);
		
		// Initially load the file and grab the type
		// Load the raw bytes from the file as a string and convert them to json
		ByteBuffer buff = ZAssetUtils.getJarBytes(path);
		var buffBytes = new byte[buff.remaining()];
		buff.get(buffBytes);
		Gson gson = new Gson();
		var objData = new String(buffBytes);
		var obj = gson.fromJson(objData, JsonObject.class);
		var type = Saveable.e(TYPE_KEY, obj, TextureMappingType.class, null);
		
		// If no type is found, there's a problem
		if(type == null){
			ZConfig.error("Failed to load mapped texture with name: ", name, ", invalid type found in object data: ", objData);
			return null;
		}
		
		// Figure out what type to create
		return type.loadMappingFromJson(path, obj);
	}
	
	// Nothing to destroy for mapped textures
	@Override
	public void destroy(){}
}
