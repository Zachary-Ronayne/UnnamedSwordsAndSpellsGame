package zgame.core.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

/** A safe wrapper for {@link PointerBuffer} that protects against memory access violations */
public class ZPointerBuffer{
	
	/** The actual buffer to use */
	private PointerBuffer buffer;
	
	/** @param capacity The capacity of see {@link #buffer} */
	public ZPointerBuffer(int capacity){
		this.buffer = BufferUtils.createPointerBuffer(capacity);
	}
	
	/** Free {@link #buffer} if it has not already been freed */
	public void free(){
		if(this.buffer == null){
			ZConfig.error("Cannot free buffer: ", this.hashCode(), ", it was already freed");
			return;
		}
		
		this.buffer.free();
		this.buffer = null;
	}
	
	/** @return See {@link #buffer} */
	public PointerBuffer getBuffer(){
		return this.buffer;
	}
	
}
