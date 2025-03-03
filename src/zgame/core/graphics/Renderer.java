package zgame.core.graphics;

import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.joml.Vector4d;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;

import zgame.core.graphics.buffer.*;
import zgame.core.graphics.camera.GameCamera3D;
import zgame.core.graphics.camera.GameCamera;
import zgame.core.graphics.font.GameFont;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.graphics.image.GameImage;
import zgame.core.graphics.shader.ShaderProgram;
import zgame.core.utils.LimitedStack;
import zgame.core.utils.ZMath;
import zgame.core.utils.ZRect2D;
import zgame.core.utils.ZStringUtils;
import zgame.core.window.GameWindow;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that handles OpenGL operations related to drawing objects.
 * Create an instance of this class and call draw methods to draw to this Renderer,
 * then call drawToWindow to display the contents of this Renderer.
 * DO NOT directly call any OpenGL methods when using this class, otherwise unexpected results could happen. <p>
 * Coordinate explanation:<p>
 * OpenGL space: the coordinate system used by OpenGL, i.e. the upper left hand corner is (-1, 1) and the lower right hand corner is (1, -1)<p>
 * Window coordinates: The pixels on the GLFW window itself<p>
 * Screen coordinates: The in game coordinates, relative to what is displayed on the screen.
 * i.e. the upper left hand corner is always (0, 0),
 * and the lower right hand corner is always (Renderer.screen.width, Renderer.screen.height)<p>
 * Game coordinates: The actual position of something in the game, regardless of where it would be rendered
 */
public class Renderer implements Destroyable{
	
	// issue#5 abstract out the values being sent to the GPU, and make their updating handled by a separate class
	
	/** The color to use for rendering by default */
	public static final ZColor DEFAULT_COLOR = new ZColor(0);
	/** The default font to use for rendering. Null means rendering cannot happen unless a font is set */
	public static final GameFont DEFAULT_FONT = null;
	/** Default value for {@link #positioningEnabledStack} */
	public static final Boolean DEFAULT_POSITIONING_ENABLED = true;
	/** Default value for {@link #renderOnlyInsideStack} */
	public static final Boolean DEFAULT_RENDER_ONLY_INSIDE = true;
	/** Default value for {@link #limitedBoundsStack}. null means no limit */
	public static final ZRect2D DEFAULT_LIMITED_BOUNDS = null;
	
	/** true if the bounds should never be limited, false otherwise. Should always be false, unless debugging graphics */
	public static final boolean DISABLE_LIMITING_BOUNDS = false;
	
	/** The vertex buffer index for positional coordinates */
	public static final int VERTEX_POS_INDEX = 0;
	/** The vertex buffer index for texture coordinates */
	public static final int VERTEX_TEX_INDEX = 1;
	/** The vertex buffer index for color coordinates */
	public static final int VERTEX_COLOR_INDEX = 2;
	
	/** The buffer for the x coordinate when rendering text */
	private final FloatBuffer xTextBuff;
	/** The buffer for the y coordinate when rendering text */
	private final FloatBuffer yTextBuff;
	/** The STBTTAlignedQuad buffer for the quad for rendering */
	private final STBTTAlignedQuad textQuad;
	
	/** The shader used to draw basic shapes, i.e. solid colors */
	private final ShaderProgram shapeShader;
	/** The shader used to draw textures, i.e. images */
	private final ShaderProgram textureShader;
	/** The shader used to draw font, i.e. text */
	private final ShaderProgram fontShader;
	/** The shader used to draw the frame buffer to the screen, as a texture */
	private final ShaderProgram framebufferShader;
	/** The shader used to draw 3D rectangles with colors */
	private final ShaderProgram rect3DShader;
	/** The shader which is currently used */
	private ShaderProgram shader;
	
	/** The {@link VertexArray} for drawing plain rectangles */
	private VertexArray rectVertArr;
	/** A {@link VertexBuffer} which represents positional values that fill the entire OpenGL screen from (-1, -1) to (1, 1) */
	private VertexBuffer fillScreenPosBuff;
	/** The index buffer that tracks the indexes for drawing a rectangle */
	private IndexBuffer rectIndexBuff;
	
	/** The number of points used to draw an ellipse */
	public static final int NUM_ELLIPSE_POINTS = 36;
	/** The {@link VertexArray} for drawing plain ellipses */
	private VertexArray ellipseVertArr;
	/** The {@link VertexBuffer} which represents positional values that generate an ellipse */
	private VertexBuffer ellipsePosBuff;
	/** The index buffer that tracks the indexes for drawing an ellipse */
	private IndexBuffer ellipseIndexBuff;
	
	/** A {@link VertexArray} for drawing text */
	private VertexArray textVertArr;
	/** A {@link VertexBuffer} which represents positional values for a texture whose positional values will regularly change */
	private VertexBuffer posBuff2D;
	/** A {@link VertexBuffer} which represents texture coordinates for a texture whose texture coordinates will regularly change */
	private VertexBuffer changeTexCoordBuff;
	
	/** The {@link VertexArray} for drawing images */
	private VertexArray imgVertArr;
	/** The {@link VertexBuffer} used to track the texture coordinates for drawing the entirety of a texture, i.e. from (0, 0) to (1, 1) */
	private VertexBuffer texCoordBuff;
	
	/** The {@link IndexBuffer} that tracks indexes for drawing a 3D rectangular prism */
	private IndexBuffer rect3DIndexBuff;
	/** The {@link VertexBuffer} that tracks the coordinates for drawing a 3D rectangular prism */
	private VertexBuffer rect3DCoordBuff;
	/** The {@link VertexBuffer} that tracks the colors for drawing a 3D rectangular prism */
	private VertexBuffer rect3DColorBuff;
	/** The {@link VertexArray} for drawing a 3D rectangular prism */
	private VertexArray rect3DVertArr;
	/** The {@link VertexArray} for drawing an ellipse in 3D on the xz plane */
	private VertexArray ellipse3DVertArr;
	/** The {@link VertexBuffer} which represents positional values that generate a 3D ellipse */
	private VertexBuffer ellipse3DPosBuff;
	/** The index buffer that tracks the indexes for drawing a 3D ellipse */
	private IndexBuffer ellipse3DIndexBuff;
	
	/** The {@link IndexBuffer} that tracks indexes for drawing a finite plane from a quad */
	private IndexBuffer planeIndexBuff;
	/** The {@link VertexBuffer} that tracks the coordinates for drawing a finite plane from a quad */
	private VertexBuffer planeCoordBuff;
	/** The {@link VertexArray} for drawing a finite plane from a quad */
	private VertexArray planeVertArr;
	
	/** The {@link VertexBuffer} that tracks the texture coordinates for drawing a rectangular prism with a texture */
	private VertexBuffer rect3DTexCoordBuff;
	/** The {@link VertexArray} for drawing a rectangular prism with a texture */
	private VertexArray rect3DTexVertArr;
	
	/** The number of iterations for breaking up a sphere when drawing */
	public static final int NUM_SPHERE_ITERATIONS = 16;
	/** The {@link IndexBuffer} that tracks indexes for drawing a sphere */
	private IndexIntBuffer sphereIndexBuff;
	/** The {@link VertexBuffer} that tracks the coordinates for drawing a sphere */
	private VertexBuffer sphereCoordBuff;
	/** The {@link VertexArray} for drawing a sphere */
	private VertexArray sphereVertArr;
	
	/** The currently bound vertex array for rendering */
	private VertexArray boundVertexArray;
	
	/** The list of all the stacks of this {@link Renderer} keeping track of the state of this {@link Renderer} */
	private final ArrayList<LimitedStack<?>> stacks;
	
	/** The list of all the attribute related stacks of this {@link Renderer} keeping track of the state of this {@link Renderer} */
	private final ArrayList<LimitedStack<?>> attributeStacks;
	
	/** The stack used to keep track of transformations. The last element is always the current model view matrix */
	private final LimitedStack<Matrix4f> modelViewStack;
	/** The buffer used to track {@link #modelView} */
	private final FloatBuffer modelViewBuff;
	/** The current bounds of the renderer, transformed based on {@link #modelView()}, or null if it needs to be recalculated */
	private ZRect2D transformedRenderBounds;
	/** true if {@link #modelView()} has changed since last being sent to the current shader, and must be resent before any rendering operations take place, false otherwise */
	private boolean sendModelView;
	/** true if {@link #getColor()} has changed since last being sent to the current shader */
	private boolean sendColor;
	
	/** The last used mode for rendering alpha values */
	private AlphaMode alphaMode;
	
	/** The stack keeping track of the current color used by this {@link Renderer} */
	private final LimitedStack<ZColor> colorStack;
	
	/** The last color sent to the GPU, or null if it needs to be resent */
	private ZColor lastColor;
	
	/** The stack keeping track of the current font of this {@link Renderer}. If the top of the stack is null, no text can be drawn. No font is set by default */
	private final LimitedStack<GameFont> fontStack;
	
	/**
	 * The stack of buffers which this Renderer draws to, which later can be drawn to a window.
	 * All drawing operations happen to the top of the stack.
	 * Note that the stack will initially contain one buffer for drawing, based on the given size when initializing this {@link Renderer}
	 * Any buffers added to the stack must be externally managed, i.e., this class will not attempt to destroy them.
	 * If {@link #resize(int, int)} is called, it will destroy the initial buffer created by this object
	 */
	private final LimitedStack<GameBuffer> bufferStack;
	
	/**
	 * The stack keeping track of the {@link GameCamera} which determines the relative location and scale of objects drawn in this renderer.
	 * If the top of the stack is null, no transformations will be applied
	 */
	private final LimitedStack<GameCamera> cameraStack;
	
	/**
	 * A stack keeping track of the attribute of if positioning should be used.
	 * true if all render methods should automatically apply transformations to move from game coordinates to OpenGL coordinates, false otherwise.
	 * Essentially, if this is true, the render methods take game coordinates, if it is false, the render methods take OpenGL coordinates
	 */
	private final LimitedStack<Boolean> positioningEnabledStack;
	
	/**
	 * A stack keeping track of the attribute of if things will only attempt to render if they are inside this {@link Renderer}'s bounds
	 * true if objects which would be rendered outside of the bounds of {@link #bufferStack} should not be drawn, false otherwise.
	 * If this value is false, then all objects will be rendered, even if they should not be visible, which could cause performance issues
	 */
	private final LimitedStack<Boolean> renderOnlyInsideStack;
	
	/** The stack keeping track of the current bounds which rendering is limited to, in game coordinates, or null if no bounds is limited */
	private final LimitedStack<ZRect2D> limitedBoundsStack;
	
	/** true if the OpenGL depth test is enabled, false otherwise */
	private boolean depthTestEnabled;
	
	
	/**
	 * Create a new empty renderer
	 *
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public Renderer(int width, int height){
		this.alphaMode = AlphaMode.NORMAL;
		
		// Initialize stack list
		this.stacks = new ArrayList<>();
		this.attributeStacks = new ArrayList<>();
		
		// Buffer stack
		this.bufferStack = new LimitedStack<>(new GameBuffer(width, height, true), false);
		this.stacks.add(this.bufferStack);
		
		// Camera stack
		this.cameraStack = new LimitedStack<>(null);
		this.stacks.add(this.cameraStack);
		
		// Model view initialization
		// The matrix is 4x4, so 16 floats
		this.modelViewBuff = BufferUtils.createFloatBuffer(16);
		// Model view stack
		this.modelViewStack = new LimitedStack<>(new Matrix4f());
		this.stacks.add(this.modelViewStack);
		this.transformedRenderBounds = null;
		this.sendModelView = true;
		
		// Font stack
		this.fontStack = new LimitedStack<>(DEFAULT_FONT);
		this.stacks.add(this.fontStack);
		this.attributeStacks.add(this.fontStack);
		
		// Color stack
		this.colorStack = new LimitedStack<>(DEFAULT_COLOR);
		this.stacks.add(this.colorStack);
		this.attributeStacks.add(this.colorStack);
		this.sendColor = true;
		this.lastColor = null;
		
		// Positioning enabled stack
		this.positioningEnabledStack = new LimitedStack<>(DEFAULT_POSITIONING_ENABLED);
		this.stacks.add(this.positioningEnabledStack);
		this.attributeStacks.add(this.positioningEnabledStack);
		
		// Render only inside stack
		this.renderOnlyInsideStack = new LimitedStack<>(DEFAULT_RENDER_ONLY_INSIDE);
		this.stacks.add(this.renderOnlyInsideStack);
		this.attributeStacks.add(this.renderOnlyInsideStack);
		
		// Limited bounds stack, rendering is unbounded by default
		this.limitedBoundsStack = new LimitedStack<>(DEFAULT_LIMITED_BOUNDS);
		this.stacks.add(this.limitedBoundsStack);
		this.updateLimitedBounds();
		
		// Text rendering buffers
		this.xTextBuff = BufferUtils.createFloatBuffer(1);
		this.yTextBuff = BufferUtils.createFloatBuffer(1);
		this.textQuad = STBTTAlignedQuad.create();
		
		// Load shaders
		this.shapeShader = new ShaderProgram("default");
		this.textureShader = new ShaderProgram("texture");
		this.fontShader = new ShaderProgram("font");
		this.framebufferShader = new ShaderProgram("framebuffer");
		this.rect3DShader = new ShaderProgram("default3D");
		this.renderModeImage();
		
		// Vertex arrays and vertex buffers
		this.initVertexes();
		
		// Init depth test, setting it to false, matching the OpenGL default
		this.depthTestEnabled = false;
		glDisable(GL_DEPTH_TEST);
		this.setDepthTestEnabled(false);
	}
	
	/** Initialize all resources used by index buffers, vertex arrays, and vertex buffers */
	public void initVertexes(){
		this.initIndexBuffers();
		this.initVertexBuffers();
		this.initVertexArrays();
	}
	
	/** Initialize the constant values for all index buffers */
	private void initIndexBuffers(){
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 2D index buffers
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate an index buffer for drawing rectangles
		this.rectIndexBuff = new IndexBuffer(new byte[]{
				0, 1, 2,
				0, 3, 2
		});
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate an index buffer for drawing a 2D ellipse
		
		// Make indexes for the number of triangles minus 1
		var ellipseIndexes = new byte[(NUM_ELLIPSE_POINTS - 1) * 3];
		
		// Go through each point
		// Don't add the last index, that would cause the last triangle to overlap
		for(int i = 0; i < NUM_ELLIPSE_POINTS - 1; i++){
			ellipseIndexes[i * 3] = (byte)(0);
			ellipseIndexes[i * 3 + 1] = (byte)(i);
			ellipseIndexes[i * 3 + 2] = (byte)(i + 1);
		}
		this.ellipseIndexBuff = new IndexBuffer(ellipseIndexes);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 3D index buffers
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate the indexes for the 3D rect
		var cubeIndices = new byte[24];
		for(int i = 0; i < cubeIndices.length; i++) cubeIndices[i] = (byte)i;
		this.rect3DIndexBuff = new IndexBuffer(cubeIndices);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate the indexes for the finite 3D plane
		this.planeIndexBuff = new IndexBuffer(new byte[]{0, 1, 2, 3});
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// TODO explain why this makes sense as indexes
		// Generate the indexes for the sphere
		var sphereIndices = new int[NUM_SPHERE_ITERATIONS * NUM_SPHERE_ITERATIONS * 6];
		int sphereIndex = 0;
		for (int i = 0; i < NUM_SPHERE_ITERATIONS; i++) {
			for (int j = 0; j < NUM_SPHERE_ITERATIONS; j++) {
				int first = i * (NUM_SPHERE_ITERATIONS + 1) + j;
				int second = first + NUM_SPHERE_ITERATIONS + 1;
				sphereIndices[sphereIndex++] = first;
				sphereIndices[sphereIndex++] = second;
				sphereIndices[sphereIndex++] = first + 1;
				sphereIndices[sphereIndex++] = second;
				sphereIndices[sphereIndex++] = second + 1;
				sphereIndices[sphereIndex++] = first + 1;
			}
		}
		this.sphereIndexBuff = new IndexIntBuffer(sphereIndices);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate in index buffer for rendering 3D ellipses
		
		// Make indexes for the number of triangles minus 1
		ellipseIndexes = new byte[(NUM_ELLIPSE_POINTS - 1) * 3];
		// Go through each point
		// Don't add the last index, that would cause the last triangle to overlap
		for(int i = 0; i < NUM_ELLIPSE_POINTS - 1; i++){
			ellipseIndexes[i * 3] = (byte)(0);
			ellipseIndexes[i * 3 + 1] = (byte)(i);
			ellipseIndexes[i * 3 + 2] = (byte)(i + 1);
		}
		this.ellipse3DIndexBuff = new IndexBuffer(ellipseIndexes);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	/** Initialize the constant values for all vertex buffers */
	private void initVertexBuffers(){
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 2D vertex buffers
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for drawing rectangles that fill the entire screen and can be scaled
		this.fillScreenPosBuff = new VertexBuffer(VERTEX_POS_INDEX, 2, GL_STATIC_DRAW, new float[]{
				// Low Left Corner
				-1, -1,
				// Low Right Corner
				1, -1,
				// Up Right Corner
				1, 1,
				// Up Left Corner
				-1, 1});
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for texture coordinates for rendering images that renders the entire image
		this.texCoordBuff = new VertexBuffer(VERTEX_TEX_INDEX, 2, GL_STATIC_DRAW, new float[]{
				// Low Left Corner
				0, 0,
				// Low Right Corner
				1, 0,
				// Up Right Corner
				1, 1,
				// Up Left Corner
				0, 1});
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for texture coordinates that regularly change
		this.changeTexCoordBuff = new VertexBuffer(VERTEX_TEX_INDEX, 2, GL_DYNAMIC_DRAW, 4);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for positional coordinates that regularly change
		this.posBuff2D = new VertexBuffer(VERTEX_POS_INDEX, 2, GL_DYNAMIC_DRAW, 4);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for drawing an ellipse
		
		// Make one point for each of the points on the circle
		var ellipsePoints = new float[NUM_ELLIPSE_POINTS * 2];
		// Go through each point
		for(int i = 0; i < NUM_ELLIPSE_POINTS; i++){
			// Find the current angle based on the index
			var a = ZMath.TAU * ((float)i / NUM_ELLIPSE_POINTS);
			var x = Math.cos(a);
			var y = Math.sin(a);
			// Apply the angle
			ellipsePoints[i * 2] = (float)x;
			ellipsePoints[i * 2 + 1] = (float)y;
		}
		this.ellipsePosBuff = new VertexBuffer(VERTEX_POS_INDEX, 2, GL_STATIC_DRAW, ellipsePoints);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 3D vertex buffers
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Create the vertex buffer for the coordinates and colors of a cube with 6 different colors per face
		
		// Values for defining the cube
		// 6 faces, 4 vertices per face, 3 coordinates per position
		var cubeCorners = new float[][]{
				// left, bottom, back
				{-0.5f, 0.0f, -0.5f},
				// right, bottom, back
				{0.5f, 0.0f, -0.5f},
				// right, top, back
				{0.5f, 1.0f, -0.5f},
				// left, top, back
				{-0.5f, 1.0f, -0.5f},
				// left, bottom, front
				{-0.5f, 0.0f, 0.5f},
				// right, bottom, front
				{0.5f, 0.0f, 0.5f},
				// right, top, front
				{0.5f, 1.0f, 0.5f},
				// left, top, front
				{-0.5f, 1.0f, 0.5f},
		};
		var cubeCornerIndices = new byte[][]{
				// Front
				{4, 5, 6, 7},
				// Back
				{0, 1, 2, 3},
				// Left
				{0, 4, 7, 3},
				// Right
				{1, 5, 6, 2},
				// Top
				{3, 2, 6, 7},
				// Bottom
				{0, 1, 5, 4},
		};
		
		var cubePositions = new float[6 * 4 * 3];
		int cubeIndex = 0;
		for(int f = 0; f < 6; f++){
			for(int v = 0; v < 4; v++){
				for(int p = 0; p < 3; p++){
					cubePositions[cubeIndex++] = cubeCorners[cubeCornerIndices[f][v]][p];
				}
			}
		}
		
		// Create the vertex buffer for the colors, default to all white
		// 6 faces, 4 vertices per face, 4 color channels per color
		var colorVertices = new float[6 * 4 * 4];
		for(int i = 0; i < colorVertices.length; i++){
			colorVertices[i] = 1;
		}
		
		// Buffer for the position vertices
		this.rect3DCoordBuff = new VertexBuffer(VERTEX_POS_INDEX, 3, cubePositions);
		
		// Buffer for the color vertices
		this.rect3DColorBuff = new VertexBuffer(VERTEX_COLOR_INDEX, 4, GL_DYNAMIC_DRAW, colorVertices);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Vertex buffer for a plane
		this.planeCoordBuff = new VertexBuffer(VERTEX_POS_INDEX, 3, new float[]{
				0.5f, 0.0f, -0.5f,
				0.5f, 0.0f, 0.5f,
				-0.5f, 0.0f, 0.5f,
				-0.5f, 0.0f, -0.5f
		});
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for a sphere
		// TODO explain why this makes sense
		var sphereVertices = new float[(NUM_SPHERE_ITERATIONS + 1) * (NUM_SPHERE_ITERATIONS + 1) * 3];
		var sphereIndex = 0;
		for (int i = 0; i <= NUM_SPHERE_ITERATIONS; i++) {
			float phi = (float) (Math.PI * i / NUM_SPHERE_ITERATIONS);
			for (int j = 0; j <= NUM_SPHERE_ITERATIONS; j++) {
				float theta = (float) (2 * Math.PI * j / NUM_SPHERE_ITERATIONS);
				float x = (float) (Math.sin(phi) * Math.cos(theta));
				float y = (float) (Math.cos(phi));
				float z = (float) (Math.sin(phi) * Math.sin(theta));
				sphereVertices[sphereIndex++] = x;
				sphereVertices[sphereIndex++] = y;
				sphereVertices[sphereIndex++] = z;
			}
		}
		this.sphereCoordBuff = new VertexBuffer(VERTEX_POS_INDEX, 3, sphereVertices);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Vertex buffer for an ellipse in 3D, aligned to the xz plane
		var ellipsePoints3D = new float[NUM_ELLIPSE_POINTS * 3];
		// Go through each point
		for(int i = 0; i < NUM_ELLIPSE_POINTS; i++){
			// Find the current angle based on the index
			var a = ZMath.TAU * ((float)i / NUM_ELLIPSE_POINTS);
			var x = Math.cos(a) * 0.5;
			var z = Math.sin(a) * 0.5;
			// Apply the angle
			ellipsePoints3D[i * 3] = (float)x;
			ellipsePoints3D[i * 3 + 1] = 0f;
			ellipsePoints3D[i * 3 + 2] = (float)z;
		}
		this.ellipse3DPosBuff = new VertexBuffer(VERTEX_POS_INDEX, 3, GL_STATIC_DRAW, ellipsePoints3D);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Generate a vertex buffer for texture coordinates for rendering images
		// 6 faces, 4 vertexes per face, 2 coordinates per vertex
		var rect3DTexCoords = new float[6 * 4 * 2];
		int rectIndex = 0;
		for(int face = 0; face < 6; face++){
			// Low Left Corner
			rect3DTexCoords[rectIndex++] = 0;
			rect3DTexCoords[rectIndex++] = 0;
			// Low Right Corner
			rect3DTexCoords[rectIndex++] = 1;
			rect3DTexCoords[rectIndex++] = 0;
			// Up Right Corner
			rect3DTexCoords[rectIndex++] = 1;
			rect3DTexCoords[rectIndex++] = 1;
			// Up Left Corner
			rect3DTexCoords[rectIndex++] = 0;
			rect3DTexCoords[rectIndex++] = 1;
		}
		this.rect3DTexCoordBuff = new VertexBuffer(VERTEX_TEX_INDEX, 2, GL_STATIC_DRAW, rect3DTexCoords);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	/** Initialize each type of vertex array core to the renderer */
	private void initVertexArrays(){
		
		// 2D vertex arrays
		
		// Generate a vertex array for drawing solid colored rectangles
		this.rectVertArr = new VertexArray(this.fillScreenPosBuff);
		
		// Generate a vertex array for rendering images
		this.imgVertArr = new VertexArray(this.fillScreenPosBuff, this.texCoordBuff);
		
		// Generate a vertex array for rendering text
		this.textVertArr = new VertexArray(this.posBuff2D, this.changeTexCoordBuff);
		
		// Generate a vertex array for rendering ellipses
		this.ellipseVertArr = new VertexArray(this.ellipsePosBuff);
		
		// 3D vertex arrays
		
		// Create and bind the vertex array for the 3D rect
		this.rect3DVertArr = new VertexArray(this.rect3DCoordBuff, this.rect3DColorBuff);
		
		// Create and bind the vertex array for the finite 3D plane
		this.planeVertArr = new VertexArray(this.planeCoordBuff);
		
		// Create and bind the vertex array for the sphere
		this.sphereVertArr = new VertexArray(this.sphereCoordBuff);
		
		// Generate a vertex array for rendering 3D ellipses
		this.ellipse3DVertArr = new VertexArray(this.ellipse3DPosBuff);
		
		// Create a vertex array for drawing a textured cube
		this.rect3DTexVertArr = new VertexArray(this.rect3DCoordBuff, this.rect3DTexCoordBuff);
		
		// By default, no bound array
		this.boundVertexArray = null;
	}
	
	/** Free all resources used by the vertex arrays and vertex buffers */
	public void destroyVertexes(){
		this.fillScreenPosBuff.destroy();
		this.texCoordBuff.destroy();
		this.posBuff2D.destroy();
		this.changeTexCoordBuff.destroy();
		this.rect3DColorBuff.destroy();
		
		this.rectVertArr.destroy();
		this.imgVertArr.destroy();
		this.textVertArr.destroy();
		this.rect3DVertArr.destroy();
		
		this.ellipse3DVertArr.destroy();
		
		this.rect3DTexVertArr.destroy();
		this.rect3DTexCoordBuff.destroy();
	}
	
	/**
	 * Bind the given vertex array as the current one for rendering. Does nothing if it is already the one bounded
	 *
	 * @param vertexArray The array to bind
	 */
	public void bindVertexArray(VertexArray vertexArray){
		if(this.boundVertexArray == vertexArray) return;
		this.boundVertexArray = vertexArray;
		this.boundVertexArray.bind();
	}
	
	/** Delete any resources used by this Renderer */
	@Override
	public void destroy(){
		this.getBuffer().destroy();
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		this.destroyVertexes();
	}
	
	/** Push the entire state of this renderer into its stacks */
	public void pushAll(){
		for(LimitedStack<?> s : this.stacks) s.push();
	}
	
	/** Pop the entire state of this renderer off its stacks */
	public void popAll(){
		GameBuffer oldBuffer = this.getBuffer();
		for(LimitedStack<?> s : this.stacks) s.pop();
		if(this.getBuffer() != oldBuffer) this.updateBuffer();
	}
	
	/**
	 * Push the values of the simple attributes of this renderer
	 * See {@link #colorStack}, {@link #fontStack}, {@link #positioningEnabledStack}, {@link #renderOnlyInsideStack}
	 */
	public void pushAttributes(){
		for(LimitedStack<?> s : this.attributeStacks) s.push();
	}
	
	/** Pop the values of the simple attributes of this renderer */
	public void popAttributes(){
		for(LimitedStack<?> s : this.attributeStacks) s.pop();
	}
	
	/**
	 * Modify the default size of this Renderer. This is a costly operation and should not regularly be run
	 * This will not modify the current top of the buffer stack, but the default buffer, unless the default buffer is at the top of the stack.
	 * This method will also destroy the buffer at the bottom of the stack
	 *
	 * @param width The width, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 * @param height The height, in pixels, of the size of this Renderer, i.e. the size of the internal buffer
	 */
	public void resize(int width, int height){
		this.bufferStack.peek().regenerateBuffer(width, height);
	}
	
	/**
	 * Clear all rendered contents of this renderer. Calling this method will leave this Renderer's GameBuffer's Framebuffer as the bound framebuffer
	 */
	public void clear(){
		glBindFramebuffer(GL_FRAMEBUFFER, this.getBuffer().getFrameID());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		if(this.isDepthTestEnabled()) glClearDepth(1);
	}
	
	/** @return The {@link Matrix4f} of the model view, i.e. the current transformation status of the renderer */
	public Matrix4f modelView(){
		return this.modelViewStack.peek();
	}
	
	public ZRect2D getTransformedRenderBounds(){
		if(this.transformedRenderBounds == null) this.recalculateRenderBounds();
		return this.transformedRenderBounds;
	}
	
	/** Update the data of the model view matrix in OpenGL */
	private void updateGpuModelView(){
		// Do nothing if the matrix does not need to be updated
		if(!this.sendModelView) return;
		this.sendModelView = false;
		
		// Send the model view to the GPU
		this.modelView().get(this.modelViewBuff);
		int loc = glGetUniformLocation(this.shader.getId(), "modelView");
		glUniformMatrix4fv(loc, false, this.modelViewBuff);
	}
	
	/** Update the uniform variable used to track the color, with the current value */
	private void updateGpuColor(){
		// Do nothing if the color does not need to be updated
		if(!this.sendColor) return;
		this.sendColor = false;
		
		var col = this.getColor();
		if(col.equals(this.lastColor)) return;
		this.lastColor = col;
		
		float[] c = col.toFloat();
		int loc = glGetUniformLocation(this.shader.getId(), "mainColor");
		if(loc != -1) glUniform4fv(loc, c);
	}
	
	/** Recalculate the value of {@link #transformedRenderBounds} based on the current value of {@link #modelView()} */
	private void recalculateRenderBounds(){
		// Don't recalculate the bounds if it doesn't need to be recalculated
		if(this.transformedRenderBounds != null) return;
		
		// This assumes only scaling and translation transformations have occurred
		
		ZRect2D renderBounds = this.getBounds();
		
		// issue#6 Transform the render bounds by the current model view matrix so that it aligns with the given draw bounds, figure out where this math is going wrong
		
		// Form the matrices that represent the upper left hand and lower right hand corners of the draw bounds
		Vector4d upper = new Vector4d(renderBounds.getX(), renderBounds.getY(), 1, 1);
		Vector4d lower = new Vector4d(renderBounds.getX() + renderBounds.getWidth(), renderBounds.getY() + renderBounds.getHeight(), 1, 1);
		
		// Grab the current modelView matrix
		Matrix4f transform = this.modelView();
		
		// Transform the corners by the current matrix
		upper.mul(transform);
		lower.mul(transform);
		
		// Set the current transformed bounds based on the calculated corners
		this.transformedRenderBounds = new ZRect2D(upper.x, upper.y, lower.x - upper.x, lower.y - upper.y);
	}
	
	// issue#6 remove, this is a testing method. If working correctly, it should always draw a transparent rectangle on top of the entire canvas, regardless of any kind of
	// transformations
	// public void renderWeird(){
	// this.recalculateRenderBounds();
	// this.setColor(0, 1, 1, .5);
	// this.drawRectangle(transformedRenderBounds);
	// }
	
	/** Tell all of the values in this {@link Renderer} that they need to be resent to the gpu */
	public void markGpuSend(){
		this.sendModelView = true;
		this.sendColor = true;
		this.lastColor = null;
	}
	
	/**
	 * Set the transformation matrix used for rendering
	 *
	 * @param matrix The matrix to use
	 */
	public void setMatrix(Matrix4f matrix){
		this.modelViewStack.replaceTop(matrix);
		this.markModelViewChanged();
	}
	
	/** Tell this {@link Renderer} that {@link #modelView()} has changed, and its dependent values will need to be recalculated before they can be used */
	private void markModelViewChanged(){
		this.sendModelView = true;
		this.transformedRenderBounds = null;
	}
	
	/** Set the modelView matrix to the identity matrix */
	public void identityMatrix(){
		this.setMatrix(new Matrix4f());
	}
	
	/** Push the current state of the transformation matrix onto the matrix stack, i.e. save the current state of the transformations */
	public void pushMatrix(){
		this.modelViewStack.push(new Matrix4f(this.modelView()));
	}
	
	/** @return The stack keeping track of the model view matrix */
	public LimitedStack<Matrix4f> getMatrixStack(){
		return this.modelViewStack;
	}
	
	/**
	 * Pop the current state of the transformation matrix, i.e. load the previous state of the transformations and discard the current state.
	 * This method does nothing if the stack is empty
	 *
	 * @return true if the stack was popped, false if no element could be popped, i.e. the stack was empty
	 */
	public boolean popMatrix(){
		boolean success = this.modelViewStack.pop() != null;
		if(success) this.markModelViewChanged();
		return success;
	}
	
	/**
	 * Translate the transformation matrix by the given amount. The coordinates are based on OpenGL positions
	 *
	 * @param x The amount on the x axis
	 * @param y The amount on the y axis
	 */
	public void translate(double x, double y){
		this.translate(x, y, 0);
	}
	
	/**
	 * Translate the transformation matrix by the given amount. The coordinates are based on OpenGL positions
	 *
	 * @param x The amount on the x axis
	 * @param y The amount on the y axis
	 * @param z The amount on the z axis
	 */
	public void translate(double x, double y, double z){
		this.modelView().translate((float)x, (float)y, (float)z);
		this.markModelViewChanged();
	}
	
	/**
	 * Scale the transformation matrix by the given amount
	 *
	 * @param x The amount on the x axis
	 * @param y The amount on the y axis
	 */
	public void scale(double x, double y){
		this.scale(x, y, 1);
	}
	
	/**
	 * Scale the transformation matrix by the given amount
	 *
	 * @param x The amount on the x axis
	 * @param y The amount on the y axis
	 * @param z The amount on the z axis
	 */
	public void scale(double x, double y, double z){
		this.modelView().scale((float)x, (float)y, (float)z);
		this.markModelViewChanged();
	}
	
	/**
	 * Rotate the transformation matrix by the given amount
	 *
	 * @param ang The angle to rotate by, in radians
	 * @param x The amount on the x axis, usually 1 or 0
	 * @param y The amount on the x axis, usually 1 or 0
	 * @param z The amount on the x axis, usually 1 or 0
	 */
	public void rotate(double ang, double x, double y, double z){
		this.modelView().rotate((float)ang, (float)x, (float)y, (float)z);
		this.markModelViewChanged();
	}
	
	/**
	 * Set the model view to be the base matrix for a perspective projection using the given camera's perspective
	 * @param camera The camera to use
	 */
	public void camera3DPerspective(GameCamera3D camera){
		this.setMatrix(new Matrix4f().perspective((float)camera.getFov(), (float)this.getBuffer().getRatioWH(), (float)camera.getNearZ(), (float)camera.getFarZ()));
		this.rotate(camera.getRotX(), 1, 0, 0);
		this.rotate(camera.getRotY(), 0, 1, 0);
		this.rotate(camera.getRotZ(), 0, 0, 1);
		this.translate(-camera.getX(), -camera.getY(), -camera.getZ());
	}
	
	/** @return The top of {@link #positioningEnabledStack} */
	public boolean isPositioningEnabled(){
		return this.positioningEnabledStack.peek();
	}
	
	/** @param positioningEnabled Set the top of {@link #positioningEnabledStack} */
	public void setPositioningEnabled(boolean positioningEnabled){
		this.positioningEnabledStack.replaceTop(positioningEnabled);
	}
	
	/** @return See {@link #positioningEnabledStack} */
	public LimitedStack<Boolean> getPositioningEnabledStack(){
		return this.positioningEnabledStack;
	}
	
	/** Call this method before rendering normal shapes, i.e. solid rectangles */
	public void renderModeShapes(){
		this.setShader(this.shapeShader);
	}
	
	/** Call this method before rendering images, i.e. textures */
	public void renderModeImage(){
		this.setShader(this.textureShader);
	}
	
	/** Call this method before rendering font, i.e text */
	public void renderModeFont(){
		this.setShader(this.fontShader);
	}
	
	/** Call this method before rendering a frame buffer in place of a texture */
	public void renderModeBuffer(){
		this.setShader(this.framebufferShader);
	}
	
	/** Call this method before rendering a rect 3D object */
	public void renderModeRect3D(){
		this.setShader(this.rect3DShader);
	}
	
	/**
	 * Set the currently used shader
	 *
	 * @param shader The shader to use.
	 */
	private void setShader(ShaderProgram shader){
		if(this.shader == shader) return;
		this.markGpuSend();
		shader.use();
		this.shader = shader;
	}
	
	/**
	 * Make all rendering operations draw to this Renderer and set up this {@link Renderer} to be ready for drawing
	 */
	public void initToDraw(){
		// Bind the screen as the frame buffer
		this.getBuffer().drawToBuffer();
		this.getBuffer().setViewport();
		// Load the identity matrix before setting a default shader
		this.identityMatrix();
		// Bind a default shader
		this.shader = null;
		this.setShader(this.shapeShader);
	}
	
	/**
	 * Draw the contents of {@link #bufferStack} to the given {@link GameWindow}.
	 * This method will leave this {@link Renderer} in the state for drawing buffers, i.e. {@link #renderModeBuffer()} is called.
	 * Additionally, this method will make all further drawing operations occur directly on the given {@link GameWindow}
	 *
	 * @param window The window to draw to
	 */
	public void drawToWindow(GameWindow window){
		// Set the current shader for drawing a frame buffer
		this.renderModeBuffer();
		AlphaMode.NORMAL.apply();
		this.pushColor(this.getColor().solid());
		this.pushMatrix();
		this.identityMatrix();
		this.updateGpuColor();
		this.updateGpuModelView();
		this.popColor();
		// Bind the vertex array for drawing an image that fills the entire OpenGL space
		this.bindVertexArray(imgVertArr);
		
		// Position the image and the frame buffer to draw to the window
		glViewport(window.viewportX(), window.viewportY(), window.viewportW(), window.viewportH());
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		// Use the frame buffer texture
		glBindTexture(GL_TEXTURE_2D, this.getBuffer().getTextureID());
		
		// Draw the image
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		this.popMatrix();
	}
	
	/** @return The top of {@link #limitedBoundsStack} */
	public ZRect2D getLimitedBounds(){
		return this.limitedBoundsStack.peek();
	}
	
	/**
	 * Make this {@link Renderer} only draw things in the given bounds. Call {@link #unlimitBounds()} to turn this off.
	 * This is off by default
	 * All values are in game coordinates
	 *
	 * @param x The upper left hand x coordinate of the bounds
	 * @param y The upper left hand y coordinate of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 */
	public void limitBounds(double x, double y, double w, double h){
		this.limitBounds(new ZRect2D(x, y, w, h));
	}
	
	/**
	 * Make this {@link Renderer} only draw things in the given bounds. Call {@link #unlimitBounds()} to turn this off.
	 * This is off by default
	 * Turns off the limit if bounds is null
	 *
	 * @param bounds The bounds to limit to, in game coordinates
	 * @return true if the bounds were changed, false otherwise
	 */
	public boolean limitBounds(ZRect2D bounds){
		ZRect2D limited = this.getLimitedBounds();
		this.limitedBoundsStack.replaceTop(bounds);
		
		// If the new and old bounds are the same, don't change anything
		if(bounds == null && limited == null || bounds != null && bounds.equals(limited)) return false;
		this.updateLimitedBounds();
		
		return true;
	}
	
	/** Allow this {@link Renderer} to render anywhere on the screen, i.e. disable {@link #limitBounds(ZRect2D)}. */
	public void unlimitBounds(){
		this.limitBounds(null);
	}
	
	/**
	 * Push the current value of {@link #getLimitedBounds()} onto the stack, and limit the bounds to the given bounds
	 *
	 * @param bounds The bounds to limit to, in game coordinates
	 */
	public void pushLimitedBounds(ZRect2D bounds){
		this.limitedBoundsStack.push();
		this.limitBounds(bounds);
	}
	
	/**
	 * Push the current value of {@link #getLimitedBounds()} onto the stack, and limit the bounds to the union of the given bounds and the current limited bounds.
	 * If the bounds are not currently limited, this call is equivalent to {@link #pushLimitedBounds(ZRect2D)}
	 *
	 * @param bounds The bounds to limit to, in game coordinates
	 */
	public void pushLimitedBoundsUnion(ZRect2D bounds){
		var newBounds = this.getLimitedBounds();
		if(newBounds == null) newBounds = bounds;
		else newBounds = new ZRect2D(newBounds.createUnion(bounds));
		
		this.pushLimitedBounds(newBounds);
	}
	
	/**
	 * Push the current value of {@link #getLimitedBounds()} onto the stack, and limit the bounds to the intersection of the given bounds and the current limited bounds.
	 * If the bounds are not currently limited, this call is equivalent to {@link #pushLimitedBounds(ZRect2D)}
	 *
	 * @param bounds The bounds to limit to, in game coordinates
	 */
	public void pushLimitedBoundsIntersection(ZRect2D bounds){
		var newBounds = this.getLimitedBounds();
		if(newBounds == null) newBounds = bounds;
		else newBounds = new ZRect2D(newBounds.createIntersection(bounds));
		if(newBounds.getWidth() <= 0 || newBounds.getHeight() <= 0) newBounds = new ZRect2D();
		
		this.pushLimitedBounds(newBounds);
	}
	
	/**
	 * Push the current value of {@link #getLimitedBounds()} onto the stack, and unlimit the bounds
	 */
	public void pushUnlimitedBounds(){
		this.pushLimitedBounds(null);
	}
	
	/** Remove the current limited bounds and revert it to the previous limited bounds */
	public void popLimitedBounds(){
		this.limitedBoundsStack.pop();
		this.updateLimitedBounds();
	}
	
	/** Update the current state of the limited bounds via calls to glScissor */
	private void updateLimitedBounds(){
		if(DISABLE_LIMITING_BOUNDS){
			glDisable(GL_SCISSOR_TEST);
			return;
		}
		
		ZRect2D b = this.getLimitedBounds();
		if(b == null){
			glDisable(GL_SCISSOR_TEST);
			return;
		}
		double x = b.getX();
		double y = b.getY();
		double w = b.getWidth();
		double h = b.getHeight();
		y = y + h;
		GameCamera c = this.getCamera();
		if(c != null){
			x = c.gameToScreenX(x);
			y = c.gameToScreenY(y);
			w = c.sizeGameToScreenX(w);
			h = c.sizeGameToScreenY(h);
		}
		glEnable(GL_SCISSOR_TEST);
		glScissor((int)Math.round(x), (int)Math.round(this.getHeight() - y), (int)Math.round(w), (int)Math.round(h));
	}
	
	/** @param depthTestEnabled See {@link #depthTestEnabled} */
	public void setDepthTestEnabled(boolean depthTestEnabled){
		if(this.depthTestEnabled == depthTestEnabled) return;
		this.depthTestEnabled = depthTestEnabled;
		if(depthTestEnabled) glEnable(GL_DEPTH_TEST);
		else glDisable(GL_DEPTH_TEST);
	}
	
	/** @return See {@link #depthTestEnabled} */
	public boolean isDepthTestEnabled(){
		return this.depthTestEnabled;
	}
	
	/**
	 * Call OpenGL operations that transform to draw to a location in game coordinates.
	 * This method assumes the coordinates to translate are centered in the given rectangular bounding box in game coordinates
	 * This method does not push or pop the matrix stack
	 *
	 * @param r The bounds
	 */
	public void positionObject(ZRect2D r){
		this.positionObject(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Call OpenGL operations that transform to draw to a location in game coordinates.
	 * This method assumes the coordinates to translate are centered in the given rectangular bounding box in game coordinates
	 * This method does not push or pop the matrix stack
	 *
	 * @param x The x coordinate of the upper left hand corner
	 * @param y The y coordinate of the upper left hand corner
	 * @param w The width
	 * @param h The height
	 */
	public void positionObject(double x, double y, double w, double h){
		if(!this.isPositioningEnabled()) return;
		
		GameBuffer b = this.getBuffer();
		double rw = b.getInverseWidth();
		double rh = b.getInverseHeight();
		double hw = b.getInverseHalfWidth();
		double hh = b.getInverseHalfHeight();
		
		// OpenGL transformations happen in reverse order
		// Need to account for OpenGL, where the buffer is in the range [-1, 1] on both axis
		
		// Second, translate from the center to the upper left hand corner, -1 on the x axis, +1 on the y axis
		// That will translate the object so the object is centered on the upper left hand corner of the buffer
		// Then, translate by half of the percentage of the buffer that the object takes up.
		// This is not multiplied, because the OpenGL space is 2x2 because of the range [-1, 1], so half of 2 is 1, so multiply by nothing
		// That will make the upper left hand corner of the object align with the upper left hand corner of the buffer
		// Then, translate by the percentage of the buffer that the given position takes up.
		// This is multiplied by 2, for the same reason as before, but now it's the full amount, not half
		// That will put the object at the final location
		// The below line is just a mathematically simplified version of the commented out line
		// this.translate(-1 + w * rw + 2 * x * rw, 1 - h * rh - 2 * y * rh);
		this.translate(-1 + (x + w * .5) * hw, 1 - (y + h * .5) * hh);
		// First scale by the ratio of objectSize / bufferSize, i.e. the percentage of the buffer that object takes up
		// After this scaling, the object will be in the center of the buffer, and will be the correct size relative to the buffer
		this.scale(w * rw, h * rh);
	}
	
	/**
	 * Call OpenGL operations that transform to draw to a location in game coordinates.
	 * This method does not push or pop the matrix stack
	 *
	 * @param x The bottom middle x coordinate of the rect
	 * @param y The bottom middle y coordinate of the rect
	 * @param z The bottom middle z coordinate of the rect
	 * @param w The width, x axis, of the rect
	 * @param h The height, y axis, of the rect
	 * @param l The length, z axis, of the rect
	 * @param xRot The rotation on the x axis
	 * @param yRot The rotation on the y axis
	 * @param zRot The rotation on the z axis
	 * @param xA The point, relative to the point to position this object, to rotate on the x axis
	 * @param yA The point, relative to the point to position this object, to rotate on the y axis
	 * @param zA The point, relative to the point to position this object, to rotate on the z axis
	 */
	public void positionObject(double x, double y, double z,
							   double w, double h, double l,
							   double xRot, double yRot, double zRot,
							   double xA, double yA, double zA){
		// Transformations happen in reverse order
		
		// Move the object to its final position
		this.translate(x - xA, y - yA, z - zA);
		
		// Rotate around the center for each axis
		if(xRot != 0) this.rotate(xRot, 1.0f, 0.0f, 0.0f);
		if(yRot != 0) this.rotate(yRot, 0.0f, 1.0f, 0.0f);
		if(zRot != 0) this.rotate(zRot, 0.0f, 0.0f, 1.0f);
		
		// Translate to the axis of rotation
		this.translate(xA, yA, zA);
		
		// Start by scaling appropriately
		this.scale(w, h, l);
	}
	
	/**
	 * Draw a rectangle, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param r The bounds
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawRectangle(ZRect2D r){
		return this.drawRectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Draw a rectangle, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param x The x coordinate of the upper left hand corner of the rectangle
	 * @param y The y coordinate of the upper left hand corner of the rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawRectangle(double x, double y, double w, double h){
		if(!this.shouldDraw(x, y, w, h)) return false;
		
		// Use the shape shader and the rectangle vertex array
		this.renderModeShapes();
		this.bindVertexArray(rectVertArr);
		
		this.pushMatrix();
		this.positionObject(x, y, w, h);
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	/**
	 * Draw an ellipse, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param r The bounds
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawEllipse(ZRect2D r){
		return this.drawEllipse(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Draw a circle, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param x The x coordinate of the center of the circle
	 * @param y The y coordinate of the center of the circle
	 * @param r The radius of the circle
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawCircle(double x, double y, double r){
		return this.drawEllipse(x - r, y - r, r * 2, r * 2);
	}
	
	/**
	 * Draw an ellipse, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param x The x coordinate of the upper left hand corner of the ellipse
	 * @param y The y coordinate of the upper left hand corner of the ellipse
	 * @param w The width of the ellipse
	 * @param h The height of the ellipse
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawEllipse(double x, double y, double w, double h){
		if(!this.shouldDraw(x, y, w, h)) return false;
		
		// Use the shape shader and the rectangle vertex array
		this.renderModeShapes();
		this.bindVertexArray(ellipseVertArr);
		
		this.pushMatrix();
		this.positionObject(x, y, w, h);
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		glDrawElements(GL_TRIANGLE_FAN, this.ellipseIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	/** @param mode The new mode to use */
	private void updateAlphaMode(AlphaMode mode){
		if(mode == null) mode = AlphaMode.NORMAL;
		if(mode == this.alphaMode) return;
		this.alphaMode = mode;
		this.alphaMode.apply();
	}
	
	/**
	 * Draw a rectangular buffer at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given buffer, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param r The bounds
	 * @param b The {@link GameBuffer} to draw
	 * @param mode The way to draw the texture for transparency, or null to default to {@link AlphaMode#NORMAL}
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawBuffer(ZRect2D r, GameBuffer b, AlphaMode mode){
		return this.drawBuffer(r.getX(), r.getY(), r.getWidth(), r.getHeight(), b, mode);
	}
	
	/**
	 * Draw a rectangular buffer at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given buffer, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param x The x coordinate of the upper left hand corner of the buffer
	 * @param y The y coordinate of the upper left hand corner of the buffer
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param b The {@link GameBuffer} to draw
	 * @param mode The way to draw the texture for transparency, or null to default to {@link AlphaMode#NORMAL}
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawBuffer(double x, double y, double w, double h, GameBuffer b, AlphaMode mode){
		if(!this.shouldDraw(x, y, w, h)) return false;
		this.renderModeBuffer();
		return this.drawTexture(x, y, w, h, b.getTextureID(), mode);
	}
	
	/**
	 * Draw a rectangular image at the specified location.
	 * Draw a rectangular image at the specified location on the given buffer
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param r The bounds of the image
	 * @param img The OpenGL id of the image to draw
	 * @param mode The way to draw the texture for transparency, or null to default to {@link AlphaMode#NORMAL}
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(ZRect2D r, GameImage img, AlphaMode mode){
		return this.drawImage(r.getX(), r.getY(), r.getWidth(), r.getHeight(), img, mode);
	}
	
	/**
	 * Draw a rectangular image at the specified location.
	 * If the given dimensions have a different aspect ratio that those of the given image, then the image will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 *
	 * @param x The x coordinate of the upper left hand corner of the image
	 * @param y The y coordinate of the upper left hand corner of the image
	 * @param w The width of the image
	 * @param h The height of the image
	 * @param img The {@link GameImage} to draw
	 * @param mode The way to draw the texture for transparency, or null to default to {@link AlphaMode#NORMAL}
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawImage(double x, double y, double w, double h, GameImage img, AlphaMode mode){
		if(!this.shouldDraw(x, y, w, h)) return false;
		this.renderModeImage();
		return this.drawTexture(x, y, w, h, img.getId(), mode);
	}
	
	/**
	 * Draw a rectangular texture at the specified location.
	 * Draw a rectangular texture at the specified location on the given buffer
	 * If the given dimensions have a different aspect ratio that those of the given texture, then the texture will stretch to fit the given dimensions
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * This method does not set the shader to use, and it does not check if the bounds should be rendered
	 *
	 * @param x The x coordinate of the upper left hand corner of the texture
	 * @param y The y coordinate of the upper left hand corner of the texture
	 * @param w The width of the texture
	 * @param h The height of the texture
	 * @param img The OpenGL id of the texture to draw
	 * @param mode The way to draw the texture for transparency, or null to default to {@link AlphaMode#NORMAL}
	 * @return true if the object was drawn, false otherwise
	 */
	private boolean drawTexture(double x, double y, double w, double h, int img, AlphaMode mode){
		this.bindVertexArray(imgVertArr);
		glBindTexture(GL_TEXTURE_2D, img);
		updateAlphaMode(mode);
		
		// Perform the drawing operation
		this.pushMatrix();
		this.positionObject(x, y, w, h);
		
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		
		this.popMatrix();
		
		return true;
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 *
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param options The options for how to draw the text
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, List<TextOption> options){
		return drawText(x, y, this.getFont(), options);
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 *
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param text The text to draw
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, String text){
		return drawText(x, y, this.getFont(), List.of(new TextOption(text)));
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 *
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param text The text to draw
	 * @param mode The way to draw the texture for transparency, or null to default to {@link AlphaMode#NORMAL}
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, String text, AlphaMode mode){
		return drawText(x, y, this.getFont(), List.of(new TextOption(text, null, mode)));
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 *
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param text The text to draw
	 * @param f The font to use for drawing
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, String text, GameFont f){
		return this.drawText(x, y, f, List.of(new TextOption(text)));
	}
	
	/**
	 * @param text The text to turn into a text block
	 * @param maxWidth Max width the text can take up
	 * @return The text with newlines, the width, and height the text takes up
	 */
	public TextBoundsResult createTextBounds(String text, double maxWidth){
		var font = this.getFont();
		if(font == null) return new TextBoundsResult("", 0, 0.0);
		
		// Largest width of a word
		double largestWidth = 0;
		
		// Find the width of each word
		var words = text.split("\\s+");
		var sizes = new double[words.length];
		for(int i = 0; i < words.length; i++){
			var word = words[i];
			var strW = font.stringWidth(word);
			if(strW > maxWidth) maxWidth = strW;
			if(strW > largestWidth) largestWidth = strW;
			sizes[i] = strW;
		}
		var lines = new ArrayList<String>();
		
		// Find how many lines to use based on the max width
		var currentWidth = 0;
		var sb = new StringBuilder();
		var spaceWidth = font.charWidth(' ');
		var largestLine = 0;
		
		// Go through each word
		var first = true;
		for(int i = 0; i < words.length; i++){
			var word = words[i];
			var wordWidth = sizes[i];
			// If adding the next word would exceed the current line width, add it to a new line
			var newLine = currentWidth + wordWidth >= maxWidth;
			if(newLine){
				lines.add(sb.toString());
				if(currentWidth > largestLine) largestLine = currentWidth;
				currentWidth = 0;
				sb = new StringBuilder();
			}
			// Otherwise, if this is not the first line, add a space after the word
			else{
				if(!first){
					sb.append(" ");
					currentWidth += spaceWidth;
				}
				else first = false;
			}
			// Add the word to the current line
			sb.append(word);
			currentWidth += wordWidth;
		}
		// If anything is left in the string builder, add another line
		var remaining = sb.toString();
		if(!remaining.isEmpty()){
			lines.add(remaining);
			if(currentWidth > largestLine) largestLine = currentWidth;
		}
		
		double lineCount = lines.size();
		
		// Width will be based on the maximum of the line widths
		// Height is based on the line height and the total number of lines
		return new TextBoundsResult(ZStringUtils.concatSep("\n", lines.toArray()), largestLine, lineCount * font.getMaxHeight() + this.getFontCharSpace() * (lineCount - 1));
	}
	
	/**
	 * Draw the given text to the given position
	 * The text will be positioned such that it is written on a line, and the given position is the leftmost part of that line.
	 * i.e. the text starts at the given coordinates and is draw left to right
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * It is unwise to call this method directly. Usually it's better to use a {@link TextBuffer} and draw to that, then draw the text buffer
	 *
	 * @param x The x position of the text
	 * @param y The y position of the text
	 * @param f The font to use for drawing
	 * @param options How to draw the text, must not be null
	 * @return true if the text was drawn, false otherwise
	 */
	public boolean drawText(double x, double y, GameFont f, List<TextOption> options){
		// Make sure options are given
		if(options == null || options.isEmpty()) return false;
		
		// Make sure a font exists
		if(f == null) return false;
		
		var fa = f.getAsset();
		
		var sb = new StringBuilder();
		for(var op : options) sb.append(op.getText());
		var fullText = sb.toString();
		
		// Bounds check for if the text should be drawn
		var rects = f.stringBounds(x, y, fullText, 1, true);
		if(!this.shouldDraw(rects[fullText.length()])) return false;
		
		// Mark the drawing bounds
		// Use the font shaders
		this.renderModeFont();
		// Use the font vertex array
		this.bindVertexArray(textVertArr);
		
		// Use the font's bitmap
		glBindTexture(GL_TEXTURE_2D, fa.getBitmapID());
		
		// Set up for text position and size
		this.xTextBuff.put(0, 0.0f);
		this.yTextBuff.put(0, 0.0f);
		
		// Find the size for positioning the object
		double posSize = f.fontScalar();
		
		// Position and scale the text
		this.pushMatrix();
		// Need to scale because text is upside down
		this.scale(1, -1);
		// Need to position with height - y to account for the negative scaling
		// Need to use posSize for the width and height to keep it scaled appropriately to OpenGL coordinates
		this.positionObject(x, this.getHeight() - y, posSize, posSize);
		
		// Draw every character of the text
		for(var op : options){
			var text = op.getText();
			if(text == null || text.isEmpty()) continue;
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				
				// Find the vertices and texture coordinates of the character to draw
				// Must do this regardless of if the character will render to ensure the text moves to the next location
				f.bounds(c, this.xTextBuff, this.yTextBuff, this.textQuad);
				
				// Only draw the character if it will be in the bounds of the buffer
				if(!this.shouldDraw(rects[i])) continue;
				
				// Buffer the new data
				this.posBuff2D.updateData(new float[]{
						//////////////////////////////////////
						this.textQuad.x0(), this.textQuad.y0(),
						//////////////////////////////////////
						this.textQuad.x1(), this.textQuad.y0(),
						//////////////////////////////////////
						this.textQuad.x1(), this.textQuad.y1(),
						//////////////////////////////////////
						this.textQuad.x0(), this.textQuad.y1()});
				
				this.changeTexCoordBuff.updateData(new float[]{
						//////////////////////////////////////
						this.textQuad.s0(), this.textQuad.t0(),
						//////////////////////////////////////
						this.textQuad.s1(), this.textQuad.t0(),
						//////////////////////////////////////
						this.textQuad.s1(), this.textQuad.t1(),
						//////////////////////////////////////
						this.textQuad.s0(), this.textQuad.t1()});
				
				// Ensure the gpu has the current modelView and color
				this.updateAlphaMode(op.getAlpha());
				var opC = op.getColor();
				if(opC != null) this.setColor(opC);
				this.updateGpuColor();
				this.updateGpuModelView();
				
				// Draw the square
				glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
			}
		}
		this.popMatrix();
		
		return true;
	}
	
	/**
	 * Determine if the given bounds are contained within the bounds of the given buffer
	 * i.e. find out if something drawn within the given bounds would appear on the buffer
	 * This method accounts for the camera repositioning elements, i.e., if the camera will make something off the screen, this method accounts for that
	 *
	 * @param x The upper left hand corner x coordinate of the object, in game coordinates
	 * @param y The upper left hand corner y coordinate of the object, in game coordinates
	 * @param w The width of the object, in game coordinates
	 * @param h The height of the object, in game coordinates
	 * @return true if the bounds should be drawn, false otherwise
	 */
	public boolean shouldDraw(double x, double y, double w, double h){
		return shouldDraw(new ZRect2D(x, y, w, h));
	}
	
	/**
	 * Determine if the given bounds are contained within the current state of this {@link Renderer}
	 * i.e. find out if something drawn within the given bounds would appear on the screen
	 * This method accounts for the camera repositioning elements, i.e., if the camera will make something off the screen, this method accounts for that
	 *
	 * @param drawBounds The bounds
	 * @return true if the bounds should be drawn, false otherwise
	 */
	public boolean shouldDraw(ZRect2D drawBounds){
		// If rendering only inside is not enabled, immediately return true
		
		// issue#6 put this method back. Render checking is currently broken. This method is here to improve performance, i.e., only render things that will appear on the screen
		// By always returning true, the render check is just skipped, and everything attempts to render no matter what
		return true;
		
		// issue#6 may also need to account for how this interacts with using a buffer. Probably need to recalculate transformedRenderBounds when the buffer changes
		
		// if(!this.isRenderOnlyInside()) return true;
		// ZRect renderBounds = this.getTransformedRenderBounds();
		// ZRect limited = this.getLimitedBounds();
		
		// // Determine if the bounds for the rendering intersects the bounds to draw
		// boolean yes = renderBounds.intersects(drawBounds);
		// // If there is a limited space for the rendering, make sure that also intersects the bounds to draw
		// if(limited != null) yes &= limited.intersects(drawBounds);
		// return yes;
	}
	
	/** Fill the screen with the current color, regardless of camera position */
	public void fill(){
		this.renderModeShapes();
		this.bindVertexArray(rectVertArr);
		
		this.pushMatrix();
		this.identityMatrix();
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		glDrawElements(GL_TRIANGLES, this.rectIndexBuff.getBuff());
		this.popMatrix();
	}
	
	
	/**
	 * Draw a rectangular prism based on the given values
	 *
	 * @param r The position, scaling, and rotation information for rendering
	 * @param front The color of the front of the rect
	 * @param back The color of the back of the rect
	 * @param left The color of the left of the rect
	 * @param right The color of the right of the rect
	 * @param top The color of the top of the rect
	 * @param bot The color of the bottom of the rect
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawRectPrism(RectRender3D r, ZColor front, ZColor back, ZColor left, ZColor right, ZColor top, ZColor bot){
		// Use the 3D color shader and the 3D rect vertex array
		this.renderModeRect3D();
		this.bindVertexArray(rect3DVertArr);
		
		// Position the 3D rect
		this.pushMatrix();
		this.positionObject(r.getX(), r.getY(), r.getZ(), r.getWidth(), r.getHeight(), r.getLength(), r.xRot(), r.yRot(), r.zRot(), r.xA(), r.yA(), r.zA());
		
		// Update the color on the cube
		// 6 faces, 4 vertices per face, 4 color channels per color
		var colorVertices = new float[6 * 4 * 4];
		// issue#35 Make transparent colors work with multiple transparent faces happening at once, all transparent shapes must be added to a list that gets sorted by distance to the camera, and those are rendered last
		var cubeColors = new ZColor[]{front, back, left, right, top, bot};
		var i = 0;
		for(int f = 0; f < 6; f++){
			for(int v = 0; v < 4; v++){
				colorVertices[i++] = (float)cubeColors[f].red();
				colorVertices[i++] = (float)cubeColors[f].green();
				colorVertices[i++] = (float)cubeColors[f].blue();
				colorVertices[i++] = (float)cubeColors[f].alpha();
			}
		}
		rect3DColorBuff.updateData(colorVertices);
		
		// Ensure the gpu has the current modelView
		this.updateGpuModelView();
		
		// Draw the rect
		glDrawElements(GL_QUADS, rect3DIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	/**
	 * Draw a rectangular prism based on the given values
	 *
	 * @param r The position, scaling, and rotation information for rendering
	 * @param texture The image to use for the texture
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawRectPrismTex(RectRender3D r, GameImage texture){
		// Use the 3D texture shader and the 3D rect vertex array
		this.renderModeImage();
		this.bindVertexArray(this.rect3DTexVertArr);
		glBindTexture(GL_TEXTURE_2D, texture.getId());
		updateAlphaMode(AlphaMode.NORMAL);
		
		// Position the 3D rect
		this.pushMatrix();
		this.positionObject(r.getX(), r.getY(), r.getZ(), r.getWidth(), r.getHeight(), r.getLength(), r.xRot(), r.yRot(), r.zRot(), r.xA(), r.yA(), r.zA());
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuModelView();
		this.updateGpuColor();
		
		// Draw the rect
		glDrawElements(GL_QUADS, this.rect3DIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	/**
	 * Draw a sphere of the current color
	 * @param x The center x coordinate of the sphere
	 * @param y The center y coordinate of the sphere
	 * @param z The center z coordinate of the sphere
	 * @param radius The radius of the sphere
	 * @return true if anything was drawn, false otherwise
	 */
	public boolean drawSphere(double x, double y, double z, double radius){
		// Use the 3D color shader
		this.renderModeShapes();
		this.bindVertexArray(sphereVertArr);
		
		// Position the plane
		this.pushMatrix();
		this.positionObject(x, y, z, radius, radius, radius, 0, 0, 0, 0, 0, 0);
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		// Draw the rect
		glDrawElements(GL_TRIANGLES, sphereIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	/**
	 * Draw a plane aligned to the y axis, i.e. a flat plane like the ground
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param w The width of the plane on the x axis
	 * @param l The length of the plane on the z axis
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawFlatPlane(double x, double y, double z, double w, double l){
		return this.drawPlane(x, y, z, w, l, 0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Draw a plane aligned to the y axis, i.e. a flat plane like the ground
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param w The width of the plane on the x axis
	 * @param l The length of the plane on the z axis
	 * @param angle The rotation on the y axis
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawFlatPlane(double x, double y, double z, double w, double l, double angle){
		return this.drawPlane(x, y, z, w, l, 0, angle, 0, 0, 0, 0);
	}
	
	/**
	 * Draw a plane aligned to the x axis, i.e. the side of something like a wall
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param s The size of the plane side to side
	 * @param h The height of the plane
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawSidePlaneX(double x, double y, double z, double s, double h){
		return this.drawSidePlaneX(x, y, z, s, h, 0);
	}
	
	/**
	 * Draw a plane aligned to the x axis, i.e. the side of something like a wall
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param s The size of the plane side to side
	 * @param h The height of the plane
	 * @param angle The rotation on the y axis
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawSidePlaneX(double x, double y, double z, double s, double h, double angle){
		return this.drawSidePlane(x, y, z, h, s, h, Math.PI * 0.5, 0, angle);
	}
	
	/**
	 * Draw a plane aligned to the z axis, i.e. the side of something like a wall
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param s The size of the plane side to side
	 * @param h The height of the plane
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawSidePlaneZ(double x, double y, double z, double s, double h){
		return this.drawSidePlaneZ(x, y, z, h, s, 0);
	}
	
	/**
	 * Draw a plane aligned to the z axis, i.e. the side of something like a wall
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param s The size of the plane side to side
	 * @param h The height of the plane
	 * @param angle The rotation on the y axis
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawSidePlaneZ(double x, double y, double z, double s, double h, double angle){
		return this.drawSidePlane(x, y, z, h, s, s, 0, angle, Math.PI * 0.5);
	}
	
	/**
	 * Draw a plane, default aligned to the x axis, i.e. the side of something like a wall
	 *
	 * @param x The x coordinate of the center bottom of the plane
	 * @param y The y coordinate of the center bottom of the plane
	 * @param z The z coordinate of the center bottom of the plane
	 * @param s The size of the plane side to side
	 * @param h The height of the plane
	 * @param hh The height to use for adjusting the plane so that the y coordinate is the bottom
	 * @param xRot The rotation on the x axis
	 * @param yRot The rotation on the y axis
	 * @param zRot The rotation on the z axis
	 * @return true if the object was drawn, false otherwise
	 */
	private boolean drawSidePlane(double x, double y, double z, double s, double h, double hh, double xRot, double yRot, double zRot){
		return this.drawPlane(x, y + hh * 0.5, z, h, s, xRot, yRot, zRot, 0, 0, 0);
	}
	
	/**
	 * Draw a plane based on the given values
	 *
	 * @param x The x coordinate center of the initially horizontal plane
	 * @param y The y coordinate of the initially horizontal plane
	 * @param z The z coordinate center of the initially horizontal plane
	 * @param w The width of the plane
	 * @param l The length of the plane
	 * @param xRot The rotation on the x axis
	 * @param yRot The rotation on the y axis
	 * @param zRot The rotation on the z axis
	 * @param xA The point, relative to the point to position this object, to rotate on the x axis
	 * @param yA The point, relative to the point to position this object, to rotate on the y axis
	 * @param zA The point, relative to the point to position this object, to rotate on the z axis
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawPlane(double x, double y, double z, double w, double l, double xRot, double yRot, double zRot, double xA, double yA, double zA){
		// Use the 3D color shader and the 3D rect vertex array
		this.renderModeShapes();
		this.bindVertexArray(planeVertArr);
		
		// Position the plane
		this.pushMatrix();
		this.positionObject(x, y, z, w, 1, l, xRot, yRot, zRot, xA, yA, zA);
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		// Draw the rect
		glDrawElements(GL_QUADS, planeIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	
	/**
	 * Draw an ellipse in 3D, of the current color of this Renderer, at the specified location. All values are in game coordinates
	 * Coordinate types depend on {@link #positioningEnabledStack}
	 * <p>
	 * This ellipse is flat on the xz plane
	 *
	 * @param x The x coordinate of the center of the ellipse
	 * @param y The y coordinate of the center of the plane's location
	 * @param z The z coordinate of the center of the ellipse
	 * @param w The width of the ellipse
	 * @param l The length of the ellipse
	 * @return true if the object was drawn, false otherwise
	 */
	public boolean drawEllipse3D(double x, double y, double z, double w, double l){
		// Use the shape shader and the rectangle vertex array
		this.renderModeShapes();
		this.bindVertexArray(ellipse3DVertArr);
		
		this.pushMatrix();
		this.positionObject(x, y, z, w, 1, l, 0, 0, 0, 0, 0, 0);
		
		// Ensure the gpu has the current modelView and color
		this.updateGpuColor();
		this.updateGpuModelView();
		
		glDrawElements(GL_TRIANGLE_FAN, this.ellipse3DIndexBuff.getBuff());
		this.popMatrix();
		
		return true;
	}
	
	// issue#44 implement interpolation
	
	/**
	 * Update the OpenGL frustum to the given camera
	 * @param camera The camera to use
	 */
	public void updateFrustum(GameCamera3D camera){
		glFrustum(camera.getLeftClip(), camera.getRightClip(), camera.getBottomClip(), camera.getTopClip(), camera.getNearClip(), camera.getFarClip());
	}
	
	/** @return The top of {@link #colorStack} */
	public ZColor getColor(){
		return this.colorStack.peek();
	}
	
	/** @param c The new color to push to the top of the color stack */
	public void pushColor(ZColor c){
		this.colorStack.push(c);
		this.sendColor = true;
	}
	
	/** Push the current color onto the top of {@link #colorStack} */
	public void pushColor(){
		this.colorStack.push();
		this.sendColor = true;
	}
	
	/** Pop off the top of {@link #colorStack} */
	public boolean popColor(){
		boolean success = this.colorStack.pop() != null;
		if(success) this.sendColor = true;
		return success;
	}
	
	/**
	 * Set the color, fully opaque, currently used to draw basic shapes
	 *
	 * @param r The red amount, should be in the range [0-1]
	 * @param g The green amount, should be in the range [0-1]
	 * @param b The blue amount, should be in the range [0-1]
	 */
	public void setColor(double r, double g, double b){
		this.setColor(r, g, b, 1);
	}
	
	/**
	 * Set the color currently used to draw basic shapes
	 *
	 * @param r The red amount, should be in the range [0-1]
	 * @param g The green amount, should be in the range [0-1]
	 * @param b The blue amount, should be in the range [0-1]
	 * @param a The alpha amount (transparency), should be in the range [0-1]
	 */
	public void setColor(double r, double g, double b, double a){
		this.setColor(new ZColor(r, g, b, a));
	}
	
	/**
	 * Set the color currently used to draw basic shapes. The alpha channel of this color also determines the transparency of images, text, buffers, etc.
	 *
	 * @param color the new color
	 */
	public void setColor(ZColor color){
		this.colorStack.replaceTop(color);
		this.sendColor = true;
	}
	
	/** @param a The alpha, i.e. opacity, to use for all drawing operations */
	public void setAlpha(double a){
		this.setColor(this.getColor().alpha(a));
	}
	
	/** Make the current color have no transparency, i.e. an alpha channel of 1 */
	public void makeOpaque(){
		this.setColor(this.getColor().solid());
	}
	
	/** @return The top of {@link #fontStack} */
	public GameFont getFont(){
		return this.fontStack.peek();
	}
	
	/** @param font Set the top of {@link #fontStack} */
	public void setFont(GameFont font){
		this.fontStack.replaceTop(font);
	}
	
	/** @return See {@link #fontStack} */
	public LimitedStack<GameFont> getFontStack(){
		return this.fontStack;
	}
	
	/** @return See {@link #getFont()} and {@link GameFont#getSize()} */
	public double getFontSize(){
		return this.getFont().getSize();
	}
	
	/** @param size Change the current size of the font. See {@link GameFont#getSize()} */
	public void setFontSize(double size){
		this.setFont(this.getFont().size(size));
	}
	
	/** @return See {@link #getFont()} and {@link GameFont#getLineSpace()} */
	public double getFontLineSpace(){
		return this.getFont().getLineSpace();
	}
	
	/** @param lineSpace Change the current line space of the font. See {@link GameFont#getLineSpace()} */
	public void setFontLineSpace(double lineSpace){
		this.setFont(this.getFont().lineSpace(lineSpace));
	}
	
	/** @return See {@link #getFont()} and {@link GameFont#getCharSpace()} */
	public double getFontCharSpace(){
		return this.getFont().getCharSpace();
	}
	
	/** @param charSpace Change the current line space of the font. See {@link GameFont#getCharSpace()} */
	public void setFontCharSpace(double charSpace){
		this.setFont(this.getFont().charSpace(charSpace));
	}
	
	/** @return The top of {@link #cameraStack} */
	public GameCamera getCamera(){
		return this.cameraStack.peek();
	}
	
	/** @param camera Set the top of {@link #cameraStack}. Can also use null to not use a camera for rendering */
	public void setCamera(GameCamera camera){
		this.cameraStack.replaceTop(camera);
	}
	
	/** @return The top of {@link #renderOnlyInsideStack} */
	public boolean isRenderOnlyInside(){
		return this.renderOnlyInsideStack.peek();
	}
	
	/** @param renderOnlyInside The top of {@link #renderOnlyInsideStack} */
	public void setRenderOnlyInside(boolean renderOnlyInside){
		this.renderOnlyInsideStack.replaceTop(renderOnlyInside);
	}
	
	/** @return See {@link #renderOnlyInsideStack} */
	public LimitedStack<Boolean> getRenderOnlyInsideStack(){
		return this.renderOnlyInsideStack;
	}
	
	/** @return The width, in pixels, of the underlying buffer of this Renderer */
	public int getWidth(){
		return this.getBuffer().getWidth();
	}
	
	/** @return The base width of the window used by this buffer, regardless of what is on top of the stack */
	public int getBaseWidth(){
		return this.bufferStack.getDefaultItem().getWidth();
	}
	
	/** @return The height, in pixels, of the underlying buffer of this Renderer */
	public int getHeight(){
		return this.getBuffer().getHeight();
	}
	
	/** @return The base height of the window used by this buffer, regardless of what is on top of the stack */
	public int getBaseHeight(){
		return this.bufferStack.getDefaultItem().getHeight();
	}
	
	/** @return A rectangle of the bounds of this {@link Renderer}, i.e. the position will be (0, 0), width will be {@link #getWidth()} and height will be {@link #getHeight()} */
	public ZRect2D getBounds(){
		return this.getBuffer().getBounds();
	}
	
	/** @return The ratio of the size of the internal buffer, i.e. the width divided by the height */
	public double getRatioWH(){
		return this.getBuffer().getRatioWH();
	}
	
	/** @return The ratio of the size of the internal buffer, i.e. the height divided by the width */
	public double getRatioHW(){
		return this.getBuffer().getRatioHW();
	}
	
	/** @return The OpenGL id used by the top of this {@link Renderer}s {@link #bufferStack} */
	public int getBufferId(){
		return this.getBuffer().getTextureID();
	}
	
	/** @return The top of see {@link #bufferStack} */
	public GameBuffer getBuffer(){
		return this.bufferStack.peek();
	}
	
	/**
	 * Set the buffer that this Renderer should draw to by pushing the given buffer onto {@link #bufferStack}
	 *
	 * @param buffer The top of {@link #bufferStack}
	 * @return The buffer that was being used
	 */
	public GameBuffer pushBuffer(GameBuffer buffer){
		GameBuffer oldBuff = this.getBuffer();
		this.bufferStack.push(buffer);
		this.updateBuffer();
		return oldBuff;
	}
	
	/**
	 * Pop the top buffer off of {@link #bufferStack} and return it
	 *
	 * @return The buffer, or null if no buffer could be popped
	 */
	public GameBuffer popBuffer(){
		GameBuffer b = this.bufferStack.pop();
		this.updateBuffer();
		return b;
	}
	
	/**
	 * Set the current buffer to draw to
	 * Must be very careful about using this method. Cannot set the buffer if there is only one buffer in the stack
	 *
	 * @param buffer The new buffer
	 * @return The old buffer, or null if it could not be replaced
	 */
	public GameBuffer setBuffer(GameBuffer buffer){
		GameBuffer old = this.bufferStack.replaceTop(buffer);
		if(old != buffer) this.updateBuffer();
		return old;
	}
	
	/** Update the current state of OpenGL to use the buffer at the top of {@link #bufferStack} for rendering */
	private void updateBuffer(){
		GameBuffer b = this.getBuffer();
		b.drawToBuffer();
		b.setViewport();
		// Changing the buffer and or viewport does something weird with OpenGL, so update the limited bounds after changing the buffer
		this.updateLimitedBounds();
	}
	
	/**
	 * Determine if the given bounds are in the bounds of this {@link Renderer}
	 *
	 * @param bounds The bounds to check, in game coordinates
	 * @return true if they intersect, i.e. return true if any part of the given bounds is in this {@link Renderer}'s bounds, false otherwise
	 */
	public boolean gameBoundsInScreen(ZRect2D bounds){
		ZRect2D rBounds = this.getBounds();
		GameCamera c = this.getCamera();
		ZRect2D gBounds;
		if(c == null) gBounds = rBounds;
		else gBounds = c.boundsScreenToGame(rBounds.getX(), rBounds.getBounds().getY(), rBounds.getBounds().getWidth(), rBounds.getBounds().getHeight());
		return gBounds.intersects(bounds);
	}
	
	/**
	 * Convert an x coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param window the {@link GameWindow} to use for reference for converting coordinates
	 * @param x The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenX(GameWindow window, double x){
		return windowToScreen(x, window.viewportX(), window.viewportWInverse(), this.getWidth());
	}
	
	/**
	 * Convert a y coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param window the {@link GameWindow} to use for reference for converting coordinates
	 * @param y The value to convert
	 * @return The value in screen coordinates
	 */
	public double windowToScreenY(GameWindow window, double y){
		return windowToScreen(y, window.viewportY(), window.viewportHInverse(), this.getHeight());
	}
	
	/**
	 * Convert a coordinate value in window space, to a coordinate in screen space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @return The value in screen coordinates
	 */
	public static double windowToScreen(double p, double viewportPos, double windowInverseSize, double screenSize){
		return (p - viewportPos) * windowInverseSize * screenSize;
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param window the {@link GameWindow} to use for reference for converting coordinates
	 * @param x The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowX(GameWindow window, double x){
		return screenToWindow(x, window.viewportX(), window.viewportW(), this.getBuffer().getInverseWidth());
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param window the {@link GameWindow} to use for reference for converting coordinates
	 * @param y The value to convert
	 * @return The value in window coordinates
	 */
	public double screenToWindowY(GameWindow window, double y){
		return screenToWindow(y, window.viewportY(), window.viewportH(), this.getBuffer().getInverseHeight());
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in window space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen
	 * @return The value in window coordinates
	 */
	public static double screenToWindow(double p, double viewportPos, double windowSize, double screenInverseSize){
		return p * screenInverseSize * windowSize + viewportPos;
	}
	
	/**
	 * Convert an x coordinate value in screen space, to a coordinate in OpenGL coordinates
	 *
	 * @param x The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlX(GameWindow window, double x){
		return screenToGl(x, window.viewportX(), window.getWidth(), this.getBuffer().getInverseWidth(), window.getInverseWidth());
	}
	
	/**
	 * Convert a y coordinate value in screen space, to a coordinate in OpenGL coordinates
	 *
	 * @param y The value to convert
	 * @return The value in OpenGL coordinates
	 */
	public double screenToGlY(GameWindow window, double y){
		return screenToGl(y, window.viewportY(), window.getHeight(), this.getBuffer().getInverseHeight(), window.getInverseHeight());
	}
	
	/**
	 * Convert a coordinate value in screen space, to a coordinate in OpenGL space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen to convert from
	 * @param windowInverseSize The inverse of the size of the window to convert from
	 * @return The value in OpenGL coordinates
	 */
	public static double screenToGl(double p, double viewportPos, double windowSize, double screenInverseSize, double windowInverseSize){
		return screenToWindow(p, viewportPos, windowSize, screenInverseSize) * windowInverseSize * 2 - 1;
	}
	
	/**
	 * Convert an x coordinate value in OpenGL space, to a coordinate in screen coordinates
	 *
	 * @param x The value to convert
	 * @param window The window to use to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenX(GameWindow window, double x){
		return glToScreen(x, window.viewportX(), window.getInverseWidth(), this.getWidth(), window.getWidth());
	}
	
	/**
	 * Convert a y coordinate value in OpenGL space, to a coordinate in screen coordinates
	 *
	 * @param y The value to convert
	 * @param window The window to use to convert
	 * @return The value in screen coordinates
	 */
	public double glToScreenY(GameWindow window, double y){
		return glToScreen(y, window.viewportY(), window.getInverseHeight(), this.getHeight(), window.getHeight());
	}
	
	/**
	 * Convert a coordinate value in OpenGL space, to a coordinate in screen space coordinates
	 *
	 * @param p The value to convert
	 * @param viewportPos The position of the screen when placed on the window
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @param windowSize The size of the window to convert to
	 * @return The value in OpenGL coordinates
	 */
	public static double glToScreen(double p, double viewportPos, double windowInverseSize, double screenSize, double windowSize){
		return windowToScreen(((p + 1) * 0.5) * windowSize, viewportPos, windowInverseSize, screenSize);
	}
	
	/**
	 * Convert a size on the x axis in window space, to a size in screen space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenX(GameWindow window, double x){
		return sizeWindowToScreen(x, window.viewportWInverse(), this.getWidth());
	}
	
	/**
	 * Convert a size on the y axis in window space, to a size in screen space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeWindowToScreenY(GameWindow window, double y){
		return sizeWindowToScreen(y, window.viewportHInverse(), this.getHeight());
	}
	
	/**
	 * Convert a size in window space, to a size in screen space
	 *
	 * @param p The value to convert
	 * @param windowInverseSize The size of the window
	 * @param screenSize The size of the screen to convert to
	 * @return The converted size
	 */
	public static double sizeWindowToScreen(double p, double windowInverseSize, double screenSize){
		return p * windowInverseSize * screenSize;
	}
	
	/**
	 * Convert a size on the x axis in screen space, to a size in window space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowX(GameWindow window, double x){
		return sizeScreenToWindow(x, window.viewportW(), this.getBuffer().getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in window space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToWindowY(GameWindow window, double y){
		return sizeScreenToWindow(y, window.viewportH(), this.getBuffer().getInverseHeight());
	}
	
	/**
	 * Convert a size in screen space, to a size in window space
	 *
	 * @param p The value to convert
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen to convert from
	 * @return The converted size
	 */
	public static double sizeScreenToWindow(double p, double windowSize, double screenInverseSize){
		return p * screenInverseSize * windowSize;
	}
	
	/**
	 * Convert a size on the x axis in screen space, to a size in OpenGL space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlX(GameWindow window, double x){
		return sizeScreenToGl(x, window.getWidth(), this.getBuffer().getInverseWidth(), window.getInverseWidth());
	}
	
	/**
	 * Convert a size on the y axis in screen space, to a size in OpenGL space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeScreenToGlY(GameWindow window, double y){
		return sizeScreenToGl(y, window.getHeight(), this.getBuffer().getInverseHeight(), window.getInverseHeight());
	}
	
	/**
	 * Convert a size in screen space, to a size in OpenGL space
	 *
	 * @param p The value to convert
	 * @param windowSize The size of the window
	 * @param screenInverseSize The inverse of the size of the screen to convert from
	 * @param windowInverseSize The inverse of the size of the window to convert from
	 * @return The converted size
	 */
	public static double sizeScreenToGl(double p, double windowSize, double screenInverseSize, double windowInverseSize){
		return sizeScreenToWindow(p, windowSize, screenInverseSize) * windowInverseSize * 2;
	}
	
	/**
	 * Convert a size on the x axis in OpenGL space, to a size in screen space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes the {@link GameWindow} to use for reference for converting sizes
	 * @param x The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenX(GameWindow window, double x){
		return sizeGlToScreen(x, window.getInverseWidth(), this.getWidth(), window.getWidth());
	}
	
	/**
	 * Convert a size on the y axis in OpenGL space, to a size in screen space
	 *
	 * @param window the {@link GameWindow} to use for reference for converting sizes the {@link GameWindow} to use for reference for converting sizes
	 * @param y The value to convert
	 * @return The converted size
	 */
	public double sizeGlToScreenY(GameWindow window, double y){
		return sizeGlToScreen(y, window.getInverseHeight(), this.getHeight(), window.getHeight());
	}
	
	/**
	 * Convert a size in OpenGL space, to a size in screen space
	 *
	 * @param p The value to convert
	 * @param windowInverseSize The inverse of the size of the window
	 * @param screenSize The size of the screen to convert to
	 * @param windowSize The size of the window to convert to
	 * @return The converted size
	 */
	public static double sizeGlToScreen(double p, double windowInverseSize, double screenSize, double windowSize){
		return sizeWindowToScreen(p * 0.5 * windowSize, windowInverseSize, screenSize);
	}
	
}
