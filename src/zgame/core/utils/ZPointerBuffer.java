package zgame.core.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

/** A wrapper for {@link PointerBuffer} creating a jvm managed pointer */
public class ZPointerBuffer{
	
	/** The actual buffer to use */
	private final PointerBuffer buffer;
	
	/** @param capacity The capacity of see {@link #buffer} */
	public ZPointerBuffer(int capacity){
		this.buffer = BufferUtils.createPointerBuffer(capacity);
	}
	
	/** @return See {@link #buffer} */
	public PointerBuffer getBuffer(){
		return this.buffer;
	}
	
}
