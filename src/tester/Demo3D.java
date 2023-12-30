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
import zgame.core.input.GLFWModUtils;

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
	public static final int VERTEX_COLOR_INDEX = 2;
	
	private static ShaderProgram shader;
	
	private static final float[][] cubeCorners = new float[][]{
			{-0.5f, -0.5f, -0.5f},
			{0.5f, -0.5f, -0.5f},
			{0.5f, 0.5f, -0.5f},
			{-0.5f, 0.5f, -0.5f},
			{-0.5f, -0.5f, 0.5f},
			{0.5f, -0.5f, 0.5f},
			{0.5f, 0.5f, 0.5f},
			{-0.5f, 0.5f, 0.5f},
	};
	
	private static final byte[][] cubeCorderIndices = new byte[][]{
			{0, 1, 2, 3},
			{1, 5, 6, 2},
			{4, 5, 6, 7},
			{0, 4, 7, 3},
			{3, 2, 6, 7},
			{0, 1, 5, 4},
	};
	
	private static final float[][] cubeColors = new float[][]{
			// Face 0
			{1.0f, 0.0f, 0.0f, 1.0f,},
			// Face 1
			{1.0f, 1.0f, 0.0f, 1.0f,},
			// Face 2,
			{0.0f, 1.0f, 0.0f, 1.0f,},
			// Face 3,
			{0.0f, 1.0f, 1.0f, 1.0f,},
			// Face 4,
			{0.0f, 0.0f, 1.0f, 1.0f,},
			// Face 5,
			{1.0f, 0.0f, 1.0f, 1.0f,},
	};
	
	private static IndexBuffer cubeIndexBuffer;
	private static VertexBuffer cubeVertexBuffer;
	private static VertexArray cubeVertexArray;
	
	private static VertexBuffer cubeColorVertexBuffer;
	
	private static Matrix4f modelView;
	private static FloatBuffer modelViewBuff;
	private static ZColor color;
	
	private static final float mouseSpeed = 0.001f;
	
	private static float rotateX = (float)(Math.toRadians(180));
	private static float rotateY = 0;
	
	private static float cubeRotateX = 0;
	private static float cubeRotateY = 0;
	
	private static final float[] cameraPos = new float[]{0.0f, 0.0f, 3.0f};
	private static final float cubeSpeed = 0.05f;
	
	private static boolean shiftDown = false;
	
	private static void render(){
		modelView.identity().perspective((float)Math.toRadians(45.0), 1.0f, 0.1f, 100f);
		modelView.scale(.5f, .5f, .5f);
		modelView.rotate(rotateX, 0, 1.0f, 0);
		modelView.rotate(rotateY, 1.0f, 0, 0);
		modelView.translate(cameraPos[0], cameraPos[1], cameraPos[2]);
		
		updateModelView();
		updateColor();
		
		glFrustum(-5.0, 1.0, -5.0, 1.0, 1.0, 100.0);
		glViewport(0, 0, 800, 800);
		
		modelView.translate(cameraPos[0], cameraPos[1], cameraPos[2]);
		modelView.rotate(cubeRotateX, 0, 1.0f, 0);
		modelView.rotate(cubeRotateY, 1.0f, 0, 0);
		modelView.translate(-cameraPos[0], -cameraPos[1], -cameraPos[2]);
		updateModelView();
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
		shiftDown = GLFWModUtils.isShift(mods);
		
		if(action != GLFW_RELEASE) {
			if(key == GLFW_KEY_ESCAPE) running = false;
			else if(key == GLFW_KEY_SPACE) setMouseNormal(!mouseNormal);
		}
		
		if(key == GLFW_KEY_A) cameraPos[0] -= cubeSpeed;
		else if(key == GLFW_KEY_D) cameraPos[0] += cubeSpeed;
		else if(key == GLFW_KEY_Z) cameraPos[1] -= cubeSpeed;
		else if(key == GLFW_KEY_Q) cameraPos[1] += cubeSpeed;
		else if(key == GLFW_KEY_S) cameraPos[2] -= cubeSpeed;
		else if(key == GLFW_KEY_W) cameraPos[2] += cubeSpeed;
		else if(key == GLFW_KEY_R) {
			cameraPos[0] = 0.0f;
			cameraPos[1] = 0.0f;
			cameraPos[2] = 3.0f;
			
			rotateX = (float)(Math.toRadians(180));
			rotateY = 0;
			
			cubeRotateX = 0;
			cubeRotateY = 0;
		}
	}
	
	private static void mouseMove(long window, double x, double y){
		if(lastX == null) lastX = x;
		if(lastY == null) lastY = y;
		
		var diffX = x - lastX;
		var diffY = y - lastY;
		
		if(!mouseNormal){
			if(shiftDown){
				cubeRotateX += diffX * mouseSpeed;
				cubeRotateY += diffY * mouseSpeed;
			}
			else {
				rotateX += diffX * mouseSpeed;
				rotateY += diffY * mouseSpeed;
			}
		}
		
		lastX = x;
		lastY = y;
	}
	
	private static void setMouseNormal(boolean normal){
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
		
		initDepthTest();
	}
	
	private static void initCube(){
		var cubeIndices = new byte[24];
		for(var i = 0; i < cubeIndices.length; i++) cubeIndices[i] = (byte)i;
		cubeIndexBuffer = new IndexBuffer(cubeIndices);
		cubeIndexBuffer.bind();
		
		cubeVertexArray = new VertexArray();
		cubeVertexArray.bind();
		
		// 6 faces, 4 verticies per face, 3 coordinates per position
		var cubePositions = new float[6 * 4 * 3];
		var i = 0;
		for(int f = 0; f < 6; f++){
			for(int v = 0; v < 4; v++){
				for(int p = 0; p < 3; p++){
					cubePositions[i++] = cubeCorners[cubeCorderIndices[f][v]][p];
				}
			}
		}
		
		cubeVertexBuffer = new VertexBuffer(VERTEX_POS_INDEX, 3, cubePositions);
		cubeVertexBuffer.applyToVertexArray();
		
		// 6 faces, 4 verticies per face, 4 color channels per color
		var colorVerticies = new float[6 * 4 * 4];
		i = 0;
		for(int f = 0; f < 6; f++){
			for(int v = 0; v < 4; v++){
				for(int c = 0; c < 4; c++){
					colorVerticies[i++] = cubeColors[f][c];
				}
			}
		}
		
		cubeColorVertexBuffer = new VertexBuffer(VERTEX_COLOR_INDEX, 4, colorVerticies);
		cubeColorVertexBuffer.applyToVertexArray();
		
		glBindVertexArray(0);
	}
	
	private static void initDepthTest(){
		glEnable(GL_DEPTH_TEST);
	}
	
	
	// Basically everything after this line is just the boilerplate given from the lwjgl website
	public static void main(String[] args){
		init();
		customInit();
		loop();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		var callback = glfwSetErrorCallback(null);
		if(callback == null) throw new IllegalStateException("Failed to free the callback");
		callback.free();
		
		cubeVertexBuffer.destroy();
		cubeVertexArray.destroy();
		cubeColorVertexBuffer.destroy();
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
			if (vidmode == null) throw new RuntimeException("Failed to create the video mode");
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		}
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	private static void loop() {
		while(running && !glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearDepth(1);
			render();
			glfwSwapBuffers(window);
			
			glfwPollEvents();
		}
	}
	
}
