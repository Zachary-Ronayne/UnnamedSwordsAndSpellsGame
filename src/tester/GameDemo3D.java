package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera3D;
import zgame.core.state.PlayState;
import zgame.core.utils.ZRect;
import zgame.menu.Menu;
import zgame.settings.BooleanTypeSetting;
import zgame.settings.DoubleTypeSetting;
import zgame.settings.IntTypeSetting;
import zgame.things.entity.Movement3D;
import zgame.world.Room;

import static org.lwjgl.glfw.GLFW.*;

public class GameDemo3D extends Game{
	
	// TODO generally investigate issues with buffers, particularly when going to full screen
	
	private static GameDemo3D game;
	
	private static double xRot = 0;
	private static double yRot = 0;
	private static double zRot = 0;
	
	private static boolean autoRotate = false;
	private static double xRotSpeed = 0;
	private static double yRotSpeed = 0;
	private static double zRotSpeed = 0;
	
	private static double pillarAngle = 0;
	
	private static final double walkSpeed = 0.7;
	private static final double runSpeed = 2.5;
	private static double moveSpeed = walkSpeed;
	// TODO make flying based on the mouse angle possible, add that to the engine
	private static boolean flying = false;
	private static final double tiltSpeed = 3;
	private static final double gravity = 0.08;
	private static final double jumpVel = 2;
	private static double yVel = 0;
	private static final double minCamY = .3;
	private static final double minCamDist = 3.85;
	
	private static Player3D player;
	
	public GameDemo3D(){
		super();
		this.make3D();
	}
	
	public static void main(String[] args){
		game = new GameDemo3D();
		
		game.set(BooleanTypeSetting.V_SYNC, true, false);
		game.set(IntTypeSetting.FPS_LIMIT, 0, false);
		
		game.setPrintTps(false);
		game.setPrintFps(false);
		
		var window = game.getWindow();
		window.setWindowTitle("Cube Demo");
		window.setSizeUniform(1500, 900);
		window.center();
		game.getWindow().getRenderer().getCamera3D().setZ(2);
		game.getWindow().getRenderer().getCamera3D().setY(minCamY);
		
		var state = new DemoGameState();
		state.setCurrentRoom(new DummyRoom());
		game.setCurrentState(state);
		
		updatePaused(true);
		
		player = new Player3D(game);
		
		game.start();
	}
	
	private static class DemoGameState extends PlayState{
		
		@Override
		public void render(Game game, Renderer r){
			super.render(game, r);
			
			// Draw the cube
			r.drawRectPrism(
					0, .2, 0,
					.6, .6, .6,
					xRot, yRot, zRot,
					0, -.3, 0,
					new ZColor(1, 0, 0),
					new ZColor(1, 1, 0),
					new ZColor(0, 1, 0),
					new ZColor(0, 1, 1),
					new ZColor(0, 0, 1),
					new ZColor(1, 0, 1)
			);
			
			// Draw a checkerboard floor
			for(int x = 0; x < 32; x++){
				for(int z = 0; z < 32; z++){
					r.setColor(new ZColor(((x % 2 == 0) != (z % 2 == 0)) ? .2 : .6));
					r.drawFlatPlane(x * .25 - 3.875, 0, z * .25 - 3.875, .25, .25);
				}
			}
			
			// Draw a rectangular prism that rotates only on the y axis
			r.drawRectPrism(
					2, 0, 3,
					.1, .8, .1,
					pillarAngle,
					new ZColor(.5, .5, .8),
					new ZColor(.5, .5, .4),
					new ZColor(.5, .5, .6),
					new ZColor(.5, .5, .2),
					new ZColor(.5, .5, 1),
					new ZColor(0, 0)
			);
			r.drawRectPrism(
					2.5, 0, 3,
					.1, .8, .1,
					pillarAngle,
					new ZColor(.5, .5, .8),
					new ZColor(.5, .5, .4),
					new ZColor(.5, .5, .6),
					new ZColor(.5, .5, .2, 0),
					new ZColor(.5, .5, 1),
					new ZColor(0, 0)
			);
			
			// Draw some transparent cubes
			for(int i = 0; i < 3; i++){
				r.drawRectPrism(
						-2 + i * .2, .45 - i * .05, -3 + i * .2,
						.1, .1, .1,
						Math.PI * .25 * i,
						new ZColor(1, 0, 0, .5),
						new ZColor(1, 1, 0, .5),
						new ZColor(0, 1, 0, .5),
						new ZColor(0, 1, 1, .5),
						new ZColor(0, 0, 1, .5),
						new ZColor(1, 0, 1, .5)
				);
			}
			// Draw a transparent plane
			r.setColor(new ZColor(1, 0, 0, .5));
			r.drawFlatPlane(-2, .6, -3, .25, .25);
			
			// Draw some planes of each type
			r.setColor(new ZColor(1, 0, 0));
			r.drawFlatPlane(2, .1, -3, .25, .4);
			r.setColor(new ZColor(0, 1, 0));
			r.drawSidePlaneX(2.3, .1, -3, .25, .4);
			r.setColor(new ZColor(0, 0, 1));
			r.drawSidePlaneZ(1.7, .1, -3, .4, .25);
			
			r.setColor(new ZColor(1, 1, 0));
			r.drawFlatPlane(2, 1, -3, .25, .4, pillarAngle);
			r.setColor(new ZColor(0, 1, 1));
			r.drawSidePlaneX(2.3, 1, -3, .25, .4, pillarAngle);
			r.setColor(new ZColor(1, 0, 1));
			r.drawSidePlaneZ(1.7, 1, -3, .4, .25, pillarAngle);
			
			// Draw some checkerboard walls
			for(int x = 0; x < 32; x++){
				for(int y = 0; y < 4; y++){
					r.setColor(new ZColor(((x % 2 == 0) != (y % 2 == 0)) ? .2 : .6, 0, .5));
					r.drawSidePlaneX(x * .25 - 3.875, y * .25, 4, .25, .25);
				}
			}
			for(int z = 0; z < 32; z++){
				for(int y = 0; y < 4; y++){
					r.setColor(new ZColor(.5, 0, ((z % 2 == 0) != (y % 2 == 0)) ? .2 : .6));
					r.drawSidePlaneZ(4, y * .25, z * .25 - 3.875, .25, .25);
				}
			}
		}
		
		@Override
		public void renderHud(Game game, Renderer r){
			super.renderHud(game, r);
			r.setFontSize(30);
			var s = "FPS: " + game.getRenderLooper().getLastFuncCalls();
			
			r.setColor(new ZColor(0));
			r.drawRectangle(20, 6, 10 + r.getFont().stringWidth(s), 40);
			
			r.setColor(new ZColor(1));
			r.drawText(24, 34, s);
		}
		
		@Override
		public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.playKeyAction(game, button, press, shift, alt, ctrl);
			
			if(shift) moveSpeed = runSpeed;
			else moveSpeed = walkSpeed;
			
			if(press) return;
			
			// Toggle paused
			if(button == GLFW_KEY_ESCAPE) updatePaused(!game.getPlayState().isPaused());
			
			// Modify FOV
			if(button == GLFW_KEY_LEFT_BRACKET) game.set(DoubleTypeSetting.FOV, game.get(DoubleTypeSetting.FOV) - .1, false);
			else if(button == GLFW_KEY_RIGHT_BRACKET) game.set(DoubleTypeSetting.FOV, game.get(DoubleTypeSetting.FOV) + .1, false);
			
			// Toggling the cube rotating on its own
			if(button == GLFW_KEY_1) autoRotate = !autoRotate;
			
			// Toggling full screen
			if(button == GLFW_KEY_2) game.toggleFullscreen();
			/*
			   TODO fix OpenGL warning when exiting full screen
				[LWJGL] OpenGL debug message
				ID: 0x20092
				Source: API
				Type: PERFORMANCE
				Severity: MEDIUM
				Message: Program/shader state performance warning: Vertex shader in program 12 is being recompiled based on GL state.
			 */
			
			// Toggle fly
			if(button == GLFW_KEY_F) flying = !flying;
			
			// Toggle vsync
			if(button == GLFW_KEY_V) game.toggle(BooleanTypeSetting.V_SYNC, false);
			
			// TODO allow for a third person perspective and build it into the engine
		}
	}
	
	private static class DummyRoom extends Room{
		@Override
		public void tick(Game game, double dt){
			super.tick(game, dt);
			var ki = game.getKeyInput();
			
			player.move(dt,
					ki.buttonDown(GLFW_KEY_A), ki.buttonDown(GLFW_KEY_D),
					ki.buttonDown(GLFW_KEY_W), ki.buttonDown(GLFW_KEY_S),
					ki.buttonDown(GLFW_KEY_Q), ki.buttonDown(GLFW_KEY_Z),
					flying
			);
			var camera = game.getWindow().getRenderer().getCamera3D();
			
			// TODO move jumping to Movement3D
			if(!flying){
				// Jumping
				camera.addY(yVel);
				if(camera.getY() > minCamY){
					yVel -= gravity * dt;
				}
				if(camera.getY() < minCamY){
					camera.setY(minCamY);
					yVel = 0;
				}
				if(ki.pressed(GLFW_KEY_Q) && yVel == 0) yVel = jumpVel * dt;
			}
			
			// TODO add tiling to Movement3D
			// Tilting the camera to the side
			var rotZ = camera.getRotZ();
			var tiltLeft = ki.pressed(GLFW_KEY_COMMA);
			var tiltRight = ki.pressed(GLFW_KEY_PERIOD);
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
			
			// Misc logic for random objects
			
			// Rotating the cube
			if(autoRotate){
				xRotSpeed = 1;
				yRotSpeed = .5;
				zRotSpeed = .25;
			}
			else{
				if(ki.pressed(GLFW_KEY_I)) xRotSpeed = -1;
				else if(ki.pressed(GLFW_KEY_K)) xRotSpeed = 1;
				else xRotSpeed = 0;
				
				if(ki.pressed(GLFW_KEY_J)) yRotSpeed = -1;
				else if(ki.pressed(GLFW_KEY_L)) yRotSpeed = 1;
				else yRotSpeed = 0;
				
				if(ki.pressed(GLFW_KEY_U)) zRotSpeed = -1;
				else if(ki.pressed(GLFW_KEY_O)) zRotSpeed = 1;
				else zRotSpeed = 0;
			}
			
			if(ki.pressed(GLFW_KEY_R)) {
				xRot = 0;
				yRot = 0;
				zRot = 0;
			}
			
			xRot += xRotSpeed * dt;
			yRot += yRotSpeed * dt;
			zRot += zRotSpeed * dt;
			
			// Force the camera to stay in certain bounds
			if(camera.getX() < -minCamDist) camera.setX(-minCamDist);
			else if(camera.getX() > minCamDist) camera.setX(minCamDist);
			if(camera.getZ() < -minCamDist) camera.setZ(-minCamDist);
			else if(camera.getZ() > minCamDist) camera.setZ(minCamDist);
			
			// Rotate the pillar
			pillarAngle += 2 * dt;
		}
		
		@Override
		public void render(Game game, Renderer r){
		}
	}
	
	private static void updatePaused(boolean paused){
		var p = game.getPlayState();
		if(p == null) return;
		p.setPaused(paused);
		if(paused){
			game.getCurrentState().popupMenu(game, new Menu(600, 300, 500, 100, false){
				@Override
				public void render(Game game, Renderer r, ZRect bounds){
					super.render(game, r, bounds);
					// TODO fix proper transparency not working here, probably something with buffers
					r.setColor(new ZColor(0, .5));
					r.drawRectangle(bounds);
					
					r.setColor(new ZColor(1));
					r.setFontSize(40);
					r.drawText(bounds.getX() + 30, bounds.getY() + 50, "PAUSED");
				}
			});
		}
		else game.getCurrentState().removeTopMenu(game);
	}
	
	private static class Player3D implements Movement3D{
		private final Game game;
		
		public Player3D(Game game){
			this.game = game;
		}
		
		private GameCamera3D getCamera(){
			return this.game.getWindow().getRenderer().getCamera3D();
		}
		
		@Override
		public double getRotX(){
			return this.getCamera().getRotX();
		}
		
		@Override
		public double getRotY(){
			return this.getCamera().getRotY();
		}
		
		@Override
		public double getRotZ(){
			return this.getCamera().getRotZ();
		}
		
		@Override
		public void addX(double x){
			this.getCamera().addX(x);
		}
		
		@Override
		public void addY(double y){
			this.getCamera().addY(y);
		}
		
		@Override
		public void addZ(double z){
			this.getCamera().addZ(z);
		}
		
		@Override
		public double getMoveSpeed(){
			return moveSpeed;
		}
	}
	
}
