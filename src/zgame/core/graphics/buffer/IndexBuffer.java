package zgame.core.graphics.buffer;

import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/** An object that represents a single index buffer in OpenGL */
public class IndexBuffer{
	
	/** The number OpenGL uses to track this index buffer */
	private final int id;
	
	/** The index data used by this {@link IndexBuffer} */
	private final byte[] data;
	
	/** The buffer holding {@link #data} */
	private final ByteBuffer buff;
	
	/** The mode used by glBufferData for the usage parameter. Either GL_STREAM_DRAW, GL_STATIC_DRAW, or GL_DYNAMIC_DRAW */
	private final int drawMode;
	
	/**
	 * Create a new {@link IndexBuffer} with the given data and buffer the indexes
	 *
	 * @param data See {@link #data}
	 */
	public IndexBuffer(byte[] data){
		this(GL_STATIC_DRAW, data);
	}
	
	/**
	 * Create a new {@link IndexBuffer} with the given values and buffer the indexes
	 *
	 * @param drawMode See {@link #drawMode}
	 * @param data See {@link #data}
	 */
	public IndexBuffer(int drawMode, byte[] data){
		this.drawMode = drawMode;
		this.data = data;
		this.buff = BufferUtils.createByteBuffer(this.data.length);
		this.buff.put(this.data).flip();
		this.id = glGenBuffers();
	}
	
	/** Bind then buffer the data for this index buffer */
	public void bufferData(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buff, this.drawMode);
	}
	
	/** Bind this {@link IndexBuffer} for using indexes */
	public void bind(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return this.id;
	}
	
	/** @return See {@link #buff} */
	public ByteBuffer getBuff(){
		return this.buff;
	}
	
}
