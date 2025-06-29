package zgame.core.graphics;

import zgame.core.graphics.shader.ShaderProgram;

/** A singleton class tracking all shared objects related to rendering across multiple contexts */
public final class RenderObjects{
	
	/** The single instance of {@link RenderObjects} which exists */
	private static RenderObjects instance;
	
	/** Perform all initialization needed, or do nothing if instance is already initialized */
	public static void init(){
		// Do nothing if there is already an instance
		if(instance != null) return;
		
		// Full init
		instance = new RenderObjects();
		instance.initShaders();
	}
	
	/** Destroy the static resources used */
	public static void destroyInstance(){
		// Do nothing if there is no instance
		if(instance == null) return;
		
		// Remove the instance
		instance = null;
	}
	
	/** @return See {@link #instance} */
	public static RenderObjects get(){
		return instance;
	}
	
	/** The shader used to draw basic shapes, i.e. solid colors */
	private ShaderProgram shapeShader;
	/** The shader used to draw textures, i.e. images */
	private ShaderProgram textureShader;
	/** The shader used to draw textures, i.e. images, with a tint for the color. Textures sent to this shader are expected to be grayscale */
	private ShaderProgram textureTintShader;
	/** The shader used to draw font, i.e. text */
	private ShaderProgram fontShader;
	/** The shader used to draw the frame buffer to the screen, as a texture */
	private ShaderProgram framebufferShader;
	/** The shader used to draw 3D rectangles with colors */
	private ShaderProgram rect3DShader;
	
	/** Cannot instantiate {@link RenderObjects} outside of this class */
	private RenderObjects(){}
	
	/** Initialize the state of all shaders, including loading them from a file */
	public void initShaders(){
		this.shapeShader = ShaderProgram.coreShader("default");
		this.textureShader = ShaderProgram.coreShader("texture");
		this.textureTintShader = ShaderProgram.coreShader("textureTint");
		this.fontShader = ShaderProgram.coreShader("font");
		this.framebufferShader = ShaderProgram.coreShader("framebuffer");
		this.rect3DShader = ShaderProgram.coreShader("default3D");
	}
	
	/** @return See {@link #shapeShader} */
	public ShaderProgram getShapeShader(){
		return this.shapeShader;
	}
	
	/** @return See {@link #textureShader} */
	public ShaderProgram getTextureShader(){
		return this.textureShader;
	}
	
	/** @return See {@link #textureTintShader} */
	public ShaderProgram getTextureTintShader(){
		return this.textureTintShader;
	}
	
	/** @return See {@link #fontShader} */
	public ShaderProgram getFontShader(){
		return this.fontShader;
	}
	
	/** @return See {@link #framebufferShader} */
	public ShaderProgram getFramebufferShader(){
		return this.framebufferShader;
	}
	
	/** @return See {@link #rect3DShader} */
	public ShaderProgram getRect3DShader(){
		return this.rect3DShader;
	}
	
}
