package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.settings.BooleanTypeSetting;

public class GameDemo3D extends Game{
	
	private static double xRot = Math.PI * 0.25;
	private static double yRot = Math.PI * 0.25;
	private static double zRot = Math.PI * 0.25;
	
	private static double xRotSpeed = 1;
	private static double yRotSpeed = .5;
	private static double zRotSpeed = .25;
	
	public static void main(String[] args){
		var game = new GameDemo3D();
		game.set(BooleanTypeSetting.V_SYNC, true, false);
		
		var window = game.getWindow();
		window.center();
		window.setSize(800, 800);
		
		game.start();
	}
	
	@Override
	public String getGlobalSettingsLocation(){
		return "./testGame/settings";
	}
	
	@Override
	protected void render(Renderer r){
		super.render(r);
		r.identityMatrix();
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
		xRot += xRotSpeed * dt;
		yRot += yRotSpeed * dt;
		zRot += zRotSpeed * dt;
	}
}
