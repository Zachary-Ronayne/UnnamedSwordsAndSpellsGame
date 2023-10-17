package zgame.core.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** A class holding a map which provides methods to convert {@link JsonElement} objects into a particular type */
public class JsonLoadMap{
	/** The mapping of objects to methods */
	private static final Map<Class<?>, Function<JsonElement, ?>> loadMap = new HashMap<>();
	
	static{
		loadMap.put(BigDecimal.class, JsonElement::getAsBigDecimal);
		loadMap.put(BigInteger.class, JsonElement::getAsBigInteger);
		loadMap.put(Number.class, JsonElement::getAsNumber);
		
		loadMap.put(Double.class, JsonElement::getAsDouble);
		loadMap.put(double.class, JsonElement::getAsDouble);
		
		loadMap.put(Float.class, JsonElement::getAsFloat);
		loadMap.put(float.class, JsonElement::getAsFloat);
		
		loadMap.put(Long.class, JsonElement::getAsLong);
		loadMap.put(long.class, JsonElement::getAsLong);
		
		loadMap.put(Integer.class, JsonElement::getAsInt);
		loadMap.put(int.class, JsonElement::getAsInt);
		
		loadMap.put(Short.class, JsonElement::getAsShort);
		loadMap.put(short.class, JsonElement::getAsShort);
		
		loadMap.put(Byte.class, JsonElement::getAsByte);
		loadMap.put(byte.class, JsonElement::getAsByte);
		
		loadMap.put(Character.class, JsonElement::getAsCharacter);
		loadMap.put(char.class, JsonElement::getAsCharacter);
		
		loadMap.put(boolean.class, JsonElement::getAsBoolean);
		loadMap.put(Boolean.class, JsonElement::getAsBoolean);
		
		loadMap.put(String.class, JsonElement::getAsString);
		loadMap.put(JsonObject.class, JsonElement::getAsJsonObject);
		loadMap.put(JsonArray.class, JsonElement::getAsJsonArray);
	}
	
	/**
	 * Get the given {@link JsonElement} as the given type
	 *
	 * @param clazz The class of the type, accepts primitives, numbers, {@link String}, {@link JsonObject}, and {@link JsonArray }
	 * @param e The object to get
	 * @param <T> The type of clazz
	 * @return The value, or null if either clazz is not supported, or the element could not be cast
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz, JsonElement e){
		try{
			var f = loadMap.get(clazz);
			if(f == null) return null;
			return (T)f.apply(e);
		}catch(ClassCastException err) {
			return null;
		}
	}
}
