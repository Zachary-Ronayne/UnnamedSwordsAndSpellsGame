package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.settings.BooleanTypeSetting;
import zgame.settings.IntTypeSetting;

import static org.lwjgl.glfw.GLFW.*;

public class GameDemo3D extends Game{
	
	private static GameDemo3D game;
	
	private static double xRot = Math.PI * 0.25;
	private static double yRot = Math.PI * 0.25;
	private static double zRot = Math.PI * 0.25;
	
	private static double xRotSpeed = 0;//1;
	private static double yRotSpeed = 0;//.5;
	private static double zRotSpeed = 0;//.25;
	
	public GameDemo3D(){
		super();
		this.make3D();
	}
	
	public static void main(String[] args){
		game = new GameDemo3D();
		
		game.set(BooleanTypeSetting.V_SYNC, false, false);
		game.set(IntTypeSetting.FPS_LIMIT, 0, false);
		game.setPrintTps(false);
		
		var window = game.getWindow();
		window.center();
		window.setSize(800, 800);
		
		game.start();
	}
	
	@Override
	protected void render(Renderer r){
		super.render(r);
		
		r.drawRect3D(
				0, 0, 0,
				.3, .3, .3,
				xRot, yRot, zRot,
				new ZColor(1, 0, 0),
				new ZColor(1, 1, 0),
				new ZColor(0, 1, 0),
				new ZColor(0, 1, 1),
				new ZColor(0, 0, 1),
				new ZColor(1, 0, 1)
		);
	}
	
	@Override
	protected void tick(double dt){
		super.tick(dt);
		var ki = game.getKeyInput();
		
		if(ki.pressed(GLFW_KEY_W)) xRotSpeed = -1;
		else if(ki.pressed(GLFW_KEY_S)) xRotSpeed = 1;
		else xRotSpeed = 0;
		
		if(ki.pressed(GLFW_KEY_A)) yRotSpeed = -1;
		else if(ki.pressed(GLFW_KEY_D)) yRotSpeed = 1;
		else yRotSpeed = 0;
		
		if(ki.pressed(GLFW_KEY_Q)) zRotSpeed = -1;
		else if(ki.pressed(GLFW_KEY_Z)) zRotSpeed = 1;
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
}
