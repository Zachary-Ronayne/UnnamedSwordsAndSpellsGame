package zgame.core.graphics.buffer;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * An object that represents a single index buffer in OpenGL, using integers.
 * This is almost an exact duplicate of {@link IndexByteBuffer}, however there's no good way of abstracting them out without introducing a lot of overhead when copying buffers
 */
public class IndexIntBuffer{
	
	/** The number OpenGL uses to track this index buffer */
	private final int id;
	
	/** The index data used by this {@link IndexIntBuffer} */
	private final int[] data;
	
	/** The buffer holding {@link #data} */
	private final IntBuffer buff;
	
	/** The mode used by glBufferData for the usage parameter. Either GL_STREAM_DRAW, GL_STATIC_DRAW, or GL_DYNAMIC_DRAW */
	private final int drawMode;
	
	/**
	 * Create a new {@link IndexIntBuffer} with the given data and buffer the indexes
	 *
	 * @param data See {@link #data}
	 */
	public IndexIntBuffer(int[] data){
		this(GL_STATIC_DRAW, data);
	}
	
	/**
	 * Create a new {@link IndexIntBuffer} with the given values and buffer the indexes
	 *
	 * @param drawMode See {@link #drawMode}
	 * @param data See {@link #data}
	 */
	public IndexIntBuffer(int drawMode, int[] data){
		this.drawMode = drawMode;
		this.data = data;
		this.buff = BufferUtils.createIntBuffer(this.data.length);
		this.buff.put(this.data).flip();
		this.id = glGenBuffers();
	}
	
	/** Bind then buffer the data for this index buffer */
	public void bufferData(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buff, this.drawMode);
	}
	
	/** Bind this {@link IndexIntBuffer} for using indexes */
	public void bind(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	/** @return See {@link #buff} */
	public IntBuffer getBuff(){
		return this.buff;
	}
	
}
