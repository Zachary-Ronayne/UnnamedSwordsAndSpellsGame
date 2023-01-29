package zgame.core.graphics.buffer;

import static org.lwjgl.opengl.GL30.*;

/** A class that handles tracking a single OpenGL vertex array, i.e. an object that represents multiple blocks of data on the GPU */
public class VertexArray{
	
	/** The id used by OpenGL to track this {@link VertexArray} */
	private final int id;
	
	/** Generate a new VertexArray and bind this {@link VertexArray} */
	public VertexArray(){
		this.id = glGenVertexArrays();
		this.bind();
	}
	
	/** Clear any resources used by this {@link VertexArray} */
	public void destroy(){
		glDeleteVertexArrays(this.id);
	}
	
	/** Set this {@link VertexArray} as the currently bound array */
	public void bind(){
		glBindVertexArray(this.id);
	}
	
}
