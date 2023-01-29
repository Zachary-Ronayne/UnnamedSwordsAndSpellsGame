package zgame.core.graphics.buffer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import zgame.core.graphics.Destroyable;

import static org.lwjgl.opengl.GL30.*;

/** A class that handles tracking a single OpenGL vertex buffer, i.e. a block of data on the GPU */
public class VertexBuffer implements Destroyable{
	
	/** The id used by OpenGL to track this {@link VertexBuffer} */
	private final int id;
	
	/** The vertex data of this {@link VertexBuffer} in an array */
	private float[] data;
	
	/** The data of {@link #data} in a float buffer */
	private final FloatBuffer buff;
	
	/** The index which OpenGL uses to refer to this type of vertex data */
	private final int index;
	
	/** The number of numbers in each vertex, i.e. a 3D positional vertex would have 3 values, a color in RGBA would have 4 values, etc. */
	private final int vertexLength;
	
	/** The mode used by glBufferData for the usage parameter. Either GL_STREAM_DRAW, GL_STATIC_DRAW, or GL_DYNAMIC_DRAW */
	private final int drawMode;
	
	/**
	 * Create a basic {@link VertexArray} based on the given values. Calling this constructor will create the buffer ID, assign the data to the created buffer, and assign the
	 * buffer to the current vertex array. The data in this {@link VertexBuffer} makes no guarantees about what will be stored as the initial data
	 *
	 * @param index See {@link #index}
	 * @param vertexLength See {@link #vertexLength}
	 * @param drawMode See {@link #drawMode}
	 * @param vertices The number of vertices in this {@link VertexBuffer}
	 */
	public VertexBuffer(int index, int vertexLength, int drawMode, int vertices){
		this(index, vertexLength, drawMode, new float[vertexLength * vertices]);
	}
	
	/**
	 * Create a basic {@link VertexArray} based on the given values. Calling this constructor will create the buffer ID, assign the data to the created buffer, and assign the
	 * buffer to the current vertex array
	 *
	 * @param index See {@link #index}
	 * @param vertexLength See {@link #vertexLength}
	 * @param data See {@link #data}
	 */
	public VertexBuffer(int index, int vertexLength, float[] data){
		this(index, vertexLength, GL_DYNAMIC_DRAW, data);
	}
	
	/**
	 * Create a basic {@link VertexArray} based on the given values. Calling this constructor will create the buffer ID, assign the data to the created buffer, and assign the
	 * buffer to the current vertex array
	 *
	 * @param index See {@link #index}
	 * @param vertexLength See {@link #vertexLength}
	 * @param drawMode See {@link #drawMode}
	 * @param data See {@link #data}
	 */
	public VertexBuffer(int index, int vertexLength, int drawMode, float[] data){
		this.index = index;
		this.vertexLength = vertexLength;
		this.drawMode = drawMode;
		this.id = glGenBuffers();
		this.buff = BufferUtils.createFloatBuffer(data.length);
		this.updateData(data);
		glBufferData(GL_ARRAY_BUFFER, this.buff, this.drawMode);
		this.applyToVertexArray();
	}
	
	/** Clear up any resources used by this {@link VertexBuffer} */
	@Override
	public void destroy(){
		glDeleteBuffers(this.id);
	}
	
	/**
	 * Update the data on the GPU to the current state of the data in this {@link VertexBuffer}
	 *
	 * @param data See {@link #data}
	 */
	public void updateData(float[] data){
		this.data = data;
		this.bind();
		this.buff.put(this.data).flip();
		this.buff.position(0);
		// For some reason I'm too stupid to understand, if this is glBufferData, then using this object to render text breaks, but only sometimes,
		// glBufferSubData makes it work, all the time?
		glBufferSubData(GL_ARRAY_BUFFER, 0, this.buff);
	}
	
	/** Put this {@link VertexBuffer} into the currently bound vertex array */
	public void applyToVertexArray(){
		glVertexAttribPointer(index, this.vertexLength, GL_FLOAT, false, this.vertexLength * Float.BYTES, 0);
		glEnableVertexAttribArray(index);
	}
	
	/** Use this {@link VertexBuffer} for related operations */
	public void bind(){
		glBindBuffer(GL_ARRAY_BUFFER, this.id);
	}
	
}
