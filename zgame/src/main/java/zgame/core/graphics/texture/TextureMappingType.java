package zgame.core.graphics.texture;

import com.google.gson.JsonObject;

import java.util.function.BiFunction;

/** The types of mapped textures that can be used */
public enum TextureMappingType{
	/** A rectangular prism */
	RECT_3D(TextureMappingRect3DMapping::new),
	;
	
	/** A function which defines how to create a new texture mapping from the given data */
	private final BiFunction<String, JsonObject, TextureMapping> loadMappingFromJson;
	
	/** @param loadMappingFromJson See {@link #loadMappingFromJson} */
	TextureMappingType(BiFunction<String, JsonObject, TextureMapping> loadMappingFromJson){
		this.loadMappingFromJson = loadMappingFromJson;
	}
	
	/**
	 * Apply {@link #loadMappingFromJson} to the given parameters
	 *
	 * @param path The path to use
	 * @param obj The object to use
	 * @return The converted texture mapping
	 */
	public TextureMapping loadMappingFromJson(String path, JsonObject obj){
		return this.loadMappingFromJson.apply(path, obj);
	}
	
}