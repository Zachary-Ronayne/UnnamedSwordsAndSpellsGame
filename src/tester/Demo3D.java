package tester;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.IndexBuffer;
import zgame.core.graphics.buffer.VertexArray;
import zgame.core.graphics.buffer.VertexBuffer;
import zgame.core.graphics.shader.ShaderProgram;
import zgame.core.utils.ZStringUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL30.*;

public class Demo3D{
	
	private static long window;
	private static boolean running = true;
	private static boolean mouseNormal = true;
	private static Double lastX = null;
	private static Double lastY = null;
	public static final int VERTEX_POS_INDEX = 0;
	public static final int COLOR_POS_INDEX = 1;
	
	private static ShaderProgram shader;
	
	private static final byte[] cubeIndices = new byte[]{
			// Face 0
			0, 1, 2, 3,
			// Face 1
			1, 5, 6, 2,
			// Face 2
			4, 5, 6, 7,
			// Face 3
			0, 4, 7, 3,
			// Face 4
			3, 2, 6, 7,
			// Face 5
			0, 1, 5, 4
	};
	
	private static final float[] cubeVertices = new float[]{
			// Corner 0
			-0.5f, -0.5f, -0.5f,
			// Corner 1
			0.5f, -0.5f, -0.5f,
			// Corner 2
			0.5f, 0.5f, -0.5f,
			// Corner 3
			-0.5f, 0.5f, -0.5f,
			// Corner 4
			-0.5f, -0.5f, 0.5f,
			// Corner 5
			0.5f, -0.5f, 0.5f,
			// Corner 6
			0.5f, 0.5f, 0.5f,
			// Corner 7
			-0.5f, 0.5f, 0.5f
	};
	
	private static final float[] cubeColors = new float[]{
			// Corner 0
			1.0f, 0.0f, 0.0f, 1.0f,
			// Corner 1
			1.0f, 1.0f, 0.0f, 1.0f,
			// Corner 2
			0.0f, 1.0f, 0.0f, 1.0f,
			// Corner 3
			0.0f, 1.0f, 1.0f, 1.0f,
			// Corner 4
			0.0f, 0.0f, 1.0f, 1.0f,
			// Corner 5
			1.0f, 0.0f, 1.0f, 1.0f,
			// Corner 6
			1.0f, 0.0f, 0.5f, 1.0f,
			// Corner 7
			1.0f, 0.5f, 0.0f, 1.0f,
	};
	
	private static IndexBuffer cubeIndexBuffer;
	private static VertexBuffer cubeVertexBuffer;
	private static VertexArray cubeVertexArray;
	
	private static VertexBuffer cubeColorVertexBuffer;
	
	private static Matrix4f modelView;
	private static FloatBuffer modelViewBuff;
	private static ZColor color;
	
	private static float rotateX = 0;
	private static float rotateY = 0;
	private static float rotateZ = 0;
	
	private static void render(){
		modelView.identity();
		modelView.rotate((float)(Math.PI * 0.25), rotateX, rotateY, rotateZ);
		modelView.scale(.5f, .5f, .5f);
		
		updateModelView();
		updateColor();
		
		drawCube();
	}
	
	private static void updateModelView(){
		modelView.get(modelViewBuff);
		int loc = glGetUniformLocation(shader.getId(), "modelView");
		glUniformMatrix4fv(loc, false, modelViewBuff);
	}
	
	private static void updateColor(){
		float[] c = color.toFloat();
		int loc = glGetUniformLocation(shader.getId(), "mainColor");
		if(loc != -1) glUniform4fv(loc, c);
	}
	
	private static void drawCube(){
		cubeVertexArray.bind();
		glDrawElements(GL_QUADS, cubeIndexBuffer.getBuff());
		glBindVertexArray(0);
	}
	
	private static void keyPress(long window, int key, int scancode, int action, int mods){
		if(action != GLFW_RELEASE) return;
		
		if(key == GLFW_KEY_ESCAPE) running = false;
		else if(key == GLFW_KEY_SPACE) setMouseNormal(!mouseNormal);
	}
	
	private static void mouseMove(long window, double x, double y){
		if(lastX == null) lastX = x;
		if(lastY == null) lastY = y;
		
		var diffX = lastX - x;
		var diffY = lastY - y;
//		ZStringUtils.print("Mouse diff (", diffX, ", ", diffY, ")");
		
		if(!mouseNormal){
			rotateX += diffX * 0.01;
			rotateY += diffY * 0.01;
		}
		
		lastX = x;
		lastY = y;
	}
	
	private static void setMouseNormal(boolean normal){
		ZStringUtils.prints("Setting mouse to", normal ? "normal" : "held");
		if(normal) glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		else glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		mouseNormal = normal;
	}
	
	private static void customInit(){
		shader = new ShaderProgram("default3D");
		shader.use();
		
		modelView = new Matrix4f();
		modelViewBuff = BufferUtils.createFloatBuffer(16);
		
		color = new ZColor(1, 0, 0);
		
		initCube();
	}
	
	private static void initCube(){
		cubeIndexBuffer = new IndexBuffer(cubeIndices);
		cubeIndexBuffer.bind();
		
		cubeVertexArray = new VertexArray();
		cubeVertexArray.bind();
		
		cubeVertexBuffer = new VertexBuffer(VERTEX_POS_INDEX, 3, cubeVertices);
		cubeVertexBuffer.applyToVertexArray();
		
		cubeColorVertexBuffer = new VertexBuffer(COLOR_POS_INDEX, 4, cubeColors);
		cubeColorVertexBuffer.applyToVertexArray();
		
		glBindVertexArray(0);
	}
	
	
	// Basically everything before this line is just the boilerplate given from the lwjgl website
	public static void main(String[] args){
		init();
		customInit();
		loop();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	private static void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		window = glfwCreateWindow(800, 800, "Cube Test", NULL, NULL);
		if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(window, Demo3D::keyPress);
		glfwSetCursorPosCallback(window, Demo3D::mouseMove);
		
		try (var stack = stackPush()){
			var pWidth = stack.mallocInt(1); // int*
			var pHeight = stack.mallocInt(1); // int*
			glfwGetWindowSize(window, pWidth, pHeight);
			
			var vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	private static void loop() {
		while(running && !glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			render();
			glfwSwapBuffers(window);
			
			glfwPollEvents();
		}
	}
	
}
