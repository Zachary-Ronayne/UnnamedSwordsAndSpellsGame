package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

/** Represents which glBlendFunc configuration should be used */
public enum AlphaMode{
	/** For standard rendering */
	NORMAL(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA),
	/** For rendering a buffer to another buffer */
	BUFFER(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
	;
	
	/** The value for the source of the glBlendFunc */
	private final int source;
	/** The value for the destination of the glBlendFunc */
	private final int dest;
	
	/**
	 * Initialize an alpha mode enum
	 * @param source See {@link #source}
	 * @param dest See {@link #dest}
	 */
	AlphaMode(int source, int dest){
		this.source = source;
		this.dest = dest;
	}
	
	/** Apply this enum's source and dest */
	public void apply(){
		glBlendFunc(this.source, this.dest);
	}
	
}
