package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.settings.BooleanTypeSetting;
import zgame.settings.IntTypeSetting;

import static org.lwjgl.glfw.GLFW.*;

public class GameDemo3D extends Game{
	
	private static GameDemo3D game;
	
	private static double xRot = 0;
	private static double yRot = 0;
	private static double zRot = 0;
	
	private static double xRotSpeed = 0;//1;
	private static double yRotSpeed = 0;//.5;
	private static double zRotSpeed = 0;//.25;
	
	private static final double moveSpeed = 0.7;
	private static final double mouseSpeed = 0.0007;
	private static final double tiltSpeed = 3;
	private static final double gravity = 0.08;
	private static final double jumpVel = 2;
	private static double yVel = 0;
	
	public GameDemo3D(){
		super();
		this.make3D();
	}
	
	public static void main(String[] args){
		game = new GameDemo3D();
		
		game.set(BooleanTypeSetting.V_SYNC, true, false);
		game.set(IntTypeSetting.FPS_LIMIT, 0, false);
		game.setPrintTps(false);
		
		var window = game.getWindow();
		window.setWindowTitle("Cube Demo");
		window.setSizeUniform(800, 800);
		window.center();
		
		game.start();
	}
	
	@Override
	protected void render(Renderer r){
		super.render(r);
		
		// TODO fix weird flickering issues with the cube
		
		r.drawRect3D(
				0, 0, -2,
				.3, .3, .3,
				xRot, yRot, zRot,
				new ZColor(1, 0, 0),
				new ZColor(1, 1, 0),
				new ZColor(0, 1, 0),
				new ZColor(0, 1, 1),
				new ZColor(0, 0, 1),
				new ZColor(1, 0, 1)
		);
		
		// TODO add a floor as a single finite plane, not a cube
	}
	
	@Override
	protected void tick(double dt){
		super.tick(dt);
		var ki = game.getKeyInput();
		
		var camera = game.getWindow().getRenderer().getCamera3D();
		
		// TODO abstract out a bunch of this to be built into the engine
		double xSpeed = 0;
		double zSpeed = 0;
		
		// Determining movement direction
		var ang = camera.getRotY();
		var left = ki.buttonDown(GLFW_KEY_A);
		var right = ki.buttonDown(GLFW_KEY_D);
		var forward = ki.buttonDown(GLFW_KEY_W);
		var backward = ki.buttonDown(GLFW_KEY_S);
		if(left && forward || right && backward) ang -= Math.PI * 0.25;
		if(right && forward || left && backward) ang += Math.PI * 0.25;
		
		if(forward) {
			xSpeed = Math.sin(ang);
			zSpeed = -Math.cos(ang);
		}
		else if(backward) {
			xSpeed = -Math.sin(ang);
			zSpeed = Math.cos(ang);
		}
		else{
			if(left){
				ang = camera.getRotY() - Math.PI * 0.5;
				xSpeed = Math.sin(ang);
				zSpeed = -Math.cos(ang);
			}
			else if(right){
				ang = camera.getRotY() + Math.PI * 0.5;
				xSpeed = Math.sin(ang);
				zSpeed = -Math.cos(ang);
			}
		}
		
		camera.addX(dt * moveSpeed * xSpeed);
		camera.addZ(dt * moveSpeed * zSpeed);
		
		// Jumping
		if(camera.getY() < 0) {
			camera.setY(0);
			yVel = 0;
		}
		camera.addY(yVel);
		if(ki.pressed(GLFW_KEY_Q) && yVel == 0) yVel = jumpVel * dt;
		yVel -= gravity * dt;
		
		// Tilting the camera to the side
		var rotZ = camera.getRotZ();
		var tiltLeft = ki.pressed(GLFW_KEY_Z);
		var tiltRight = ki.pressed(GLFW_KEY_X);
		var tilt = tiltSpeed * dt;
		if(rotZ != 0 && !tiltLeft && !tiltRight){
			if(Math.abs(rotZ) < tilt) camera.setRotZ(0);
			else if(rotZ < 0) camera.addRotZ(tilt);
			else camera.addRotZ(-tilt);
		}
		if(tiltLeft) {
			if(camera.getRotZ() > -Math.PI * 0.5) camera.addRotZ(-tilt);
		}
		if(tiltRight) {
			if(camera.getRotZ() < Math.PI * 0.5) camera.addRotZ(tilt);
		}
		
		// Rotating the cube
		if(ki.pressed(GLFW_KEY_I)) xRotSpeed = -1;
		else if(ki.pressed(GLFW_KEY_K)) xRotSpeed = 1;
		else xRotSpeed = 0;
		
		if(ki.pressed(GLFW_KEY_J)) yRotSpeed = -1;
		else if(ki.pressed(GLFW_KEY_L)) yRotSpeed = 1;
		else yRotSpeed = 0;
		
		if(ki.pressed(GLFW_KEY_U)) zRotSpeed = -1;
		else if(ki.pressed(GLFW_KEY_O)) zRotSpeed = 1;
		else zRotSpeed = 0;
		
		if(ki.pressed(GLFW_KEY_R)) {
			xRot = 0;
			yRot = 0;
			zRot = 0;
		}
		
		xRot += xRotSpeed * dt;
		yRot += yRotSpeed * dt;
		zRot += zRotSpeed * dt;
	}
	
	@Override
	protected void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(button, press, shift, alt, ctrl);
		
		if(press) return;
		
		// TODO make this happen automatically depending on the game state and if menus are open, also make it an option to enable or disable it
		if(button == GLFW_KEY_SPACE) {
			var window = game.getWindow();
			window.setMouseNormally(!window.isMouseNormally());
		}
	}
	
	@Override
	protected void mouseMove(double x, double y){
		super.mouseMove(x, y);
		
		var window = game.getWindow();
		if(window.isMouseNormally()) return;
		
		// TODO add this as some kind of built in thing
		
		// TODO fix sudden camera jolts when switching between normal and not normal mouse modes
		// Axes swapped because of the way that it feels like it should be
		var diffX = y - game.getMouseInput().lastY();
		var diffY = x - game.getMouseInput().lastX();
		
		var camera = window.getRenderer().getCamera3D();
		camera.addRotX(diffX * mouseSpeed);
		camera.addRotY(diffY * mouseSpeed);
	}
}
