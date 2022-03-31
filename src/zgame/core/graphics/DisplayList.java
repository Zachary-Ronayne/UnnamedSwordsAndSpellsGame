package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

/**
 * A class that handles a single OpenGL display list, and also contains basic static display lists to use
 * Must call {@link #initLists()} before using any other static methods in this class
 */
public abstract class DisplayList{
	
	/** The id used by OpenGL to track this DisplayList */
	private int id;
	
	/**
	 * Create an empty display list, which will perform the actions of {@link #listFunc()}
	 */
	private DisplayList(){
		this.id = glGenLists(1);
		this.init();
	}
	
	/**
	 * Initialize the display list with {@link #listFunc()}
	 */
	private void init(){
		glNewList(this.getId(), GL_COMPILE);
		glPushAttrib(GL_CURRENT_BIT);
		this.listFunc();
		glPopAttrib();
		glEndList();
	}
	
	/**
	 * Override this function with the methods that OpenGL should perform as a part of the display list.
	 * Only methods permitted by GL_CURRENT_BIT can be used
	 */
	public abstract void listFunc();
	
	/** Draw the display list to the current framebuffer */
	public void draw(){
		glCallList(this.getId());
	}
	
	/** @return See {@link #id} */
	public int getId(){
		return id;
	}
	
	/** Initialize every static display list which can be used from DisplayList. Must call this method before any other static methods in this class */
	public static void initLists(){
		rect = new RectangleList();
		texRect = new TexRectangleList();
	}
	
	/** A static instance to use for drawing a rectangle */
	private static RectangleList rect;
	
	/** A static instance to use for drawing a textured rectangle */
	private static TexRectangleList texRect;
	
	/** A basic implementation of DisplayList which draws a rectangle that takes up the entire base OpenGL screen */
	private static class RectangleList extends DisplayList{
		@Override
		public void listFunc(){
			glBegin(GL_QUADS);
			glVertex2d(-1, -1);
			glVertex2d(1, -1);
			glVertex2d(1, 1);
			glVertex2d(-1, 1);
			glEnd();
		}
	}
	
	/**
	 * A basic implementation of DisplayList which draws a textured rectangle which takes up the entire base OpenGL screen.
	 * The texture coordinates are such that the entire texture is mapped one time to the entire rectangle
	 */
	private static class TexRectangleList extends DisplayList{
		@Override
		public void listFunc(){
			glBegin(GL_QUADS);
			glTexCoord2d(0, 0);
			glVertex2d(-1, -1);
			glTexCoord2d(1, 0);
			glVertex2d(1, -1);
			glTexCoord2d(1, 1);
			glVertex2d(1, 1);
			glTexCoord2d(0, 1);
			glVertex2d(-1, 1);
			glEnd();
		}
	}
	
	/** Draw a rectangle which takes up the entire OpenGL screen. See {@link RectangleList} */
	public static void rect(){
		rect.draw();
	}
	
	/** Draw a rectangle which takes up the entire OpenGL screen and uses the current texture. See {@link TexRectangleList} */
	public static void texRect(){
		texRect.draw();
	}
	
}
