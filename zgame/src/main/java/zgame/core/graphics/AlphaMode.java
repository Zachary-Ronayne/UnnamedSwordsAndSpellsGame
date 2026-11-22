package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

/**
 * Represents which glBlendFunc configuration should be used.
 * Source refers to the alpha value in the range [0, 1] of the fragment, basically the pixel, color which will be drawn, essentially the new color.
 * Dest refers to the same kind of alpha value, but for the fragment which already exists in a buffer, essentially the old color, defining how they are mixed.
 * The NORMAL mode, with source: GL_SRC_ALPHA, and dest: GL_ONE_MINUS_SRC_ALPHA, means that when a pixel is rendered,
 * the new color will be mixed with its full alpha value, and the old color will be mixed with the rest of the alpha value.
 * So, if source is fully opaque, i.e. the alpha value is 1, then the final pixel color will completely override the old pixel color.
 * If the source is 90% opaque, i.e. the alpha value is 0.9, then the final pixel color will mostly be the new color, with a small influence from the old color.
 */
public enum AlphaMode{
	
	/** For standard rendering */
	NORMAL(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA),
	/** For rendering no transparency, always use the source, never the destination */
	NONE(GL_ONE, GL_ZERO);
	
	/** The value for the source of the glBlendFunc */
	private final int source;
	/** The value for the destination of the glBlendFunc */
	private final int dest;
	
	/**
	 * Initialize an alpha mode enum
	 *
	 * @param source See {@link #source}
	 * @param dest See {@link #dest}
	 */
	AlphaMode(int source, int dest){
		this.source = source;
		this.dest = dest;
	}
	
	/** Use this enum's source and dest for gl blending */
	public void use(){
		glBlendFunc(this.source, this.dest);
	}
	
}
