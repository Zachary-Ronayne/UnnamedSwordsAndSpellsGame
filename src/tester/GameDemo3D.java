package tester;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera3D;
import zgame.core.state.PlayState;
import zgame.core.utils.ZRect2D;
import zgame.menu.Menu;
import zgame.settings.BooleanTypeSetting;
import zgame.settings.DoubleTypeSetting;
import zgame.settings.IntTypeSetting;
import zgame.things.entity.movement.MovementEntityThing3D;
import zgame.world.Directions3D;
import zgame.world.Room;
import zgame.world.Room3D;

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
	
	private static boolean flying = false;
	private static final double tiltSpeed = 3;
	private static double currentTilt = 0;
	
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
		
		var room = new DummyRoom();
		var state = new DemoGameState(room);
		game.setCurrentState(state);
		
		updatePaused(true);
		
		player = new Player3D(game);
		player.setZ(2);
		room.addThing(player);
		
		game.start();
	}
	
	private static class DemoGameState extends PlayState{
		
		/**
		 * Create a basic empty play state with the given room
		 *
		 * @param room The room to use for the play state
		 */
		public DemoGameState(Room3D room){
			super(room);
		}
		
		@Override
		public Room3D getCurrentRoom(){
			return (Room3D)super.getCurrentRoom();
		}
		
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
				for(int z = 0; z < 40; z++){
					r.setColor(new ZColor(((x % 2 == 0) != (z % 2 == 0)) ? .2 : .6));
					r.drawFlatPlane(x * .25 - 3.875, 0, z * .25 - 4.875, .25, .25);
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
					r.drawSidePlaneX(x * .25 - 3.875, y * .25, 5, .25, .25);
				}
			}
			for(int z = 0; z < 40; z++){
				for(int y = 0; y < 4; y++){
					r.setColor(new ZColor(.5, 0, ((z % 2 == 0) != (y % 2 == 0)) ? .2 : .6));
					r.drawSidePlaneZ(4, y * .25, z * .25 - 4.875, .25, .25);
				}
			}
		}
		
		@Override
		public void renderHud(Game game, Renderer r){
			super.renderHud(game, r);
			r.setFontSize(30);
			// TODO abstract this to a getFps call
			var s = "FPS: " + game.getRenderLooper().getLastFuncCalls();
			
			r.setColor(new ZColor(0));
			r.drawRectangle(20, 6, 10 + r.getFont().stringWidth(s), 40);
			
			r.setColor(new ZColor(1));
			r.drawText(24, 34, s);
		}
		
		@Override
		public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.playKeyAction(game, button, press, shift, alt, ctrl);
			
			if(press) return;
			
			// Toggle paused
			if(button == GLFW_KEY_ESCAPE) updatePaused(!game.getPlayState().isPaused());
			
			// Modify FOV
			if(button == GLFW_KEY_LEFT_BRACKET) game.set(DoubleTypeSetting.FOV, game.get(DoubleTypeSetting.FOV) - .1, false);
			else if(button == GLFW_KEY_RIGHT_BRACKET) game.set(DoubleTypeSetting.FOV, game.get(DoubleTypeSetting.FOV) + .1, false);
			
			// Numerical button related controls
			
			// Toggle the boundaries
			if(shift){
				var r = getCurrentRoom();
				if(button == GLFW_KEY_1) r.toggleBoundary(Directions3D.EAST);
				else if(button == GLFW_KEY_2) r.toggleBoundary(Directions3D.WEST);
				else if(button == GLFW_KEY_3) r.toggleBoundary(Directions3D.NORTH);
				else if(button == GLFW_KEY_4) r.toggleBoundary(Directions3D.SOUTH);
				else if(button == GLFW_KEY_5) r.toggleBoundary(Directions3D.UP);
				else if(button == GLFW_KEY_6) r.toggleBoundary(Directions3D.DOWN);
			}
			else{
				
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
			}
			
			// Toggle fly
			if(button == GLFW_KEY_F) {
				flying = !flying;
				player.setGravityLevel(flying ? 0 : 1);
			}
			
			// Toggle vsync
			if(button == GLFW_KEY_V) game.toggle(BooleanTypeSetting.V_SYNC, false);
			
			// TODO allow for a third person perspective and build it into the engine
		}
	}
	
	private static class DummyRoom extends Room3D{
		
		public DummyRoom(){
			super();
			this.setEqualWidth(8);
			this.setEqualLength(10);
			this.setBoundary(Directions3D.DOWN, 0);
			this.setBoundary(Directions3D.UP, 0.6);
			this.setBoundary(Directions3D.UP, false);
		}
		
		@Override
		public void tick(Game game, double dt){
			super.tick(game, dt);
			var ki = game.getKeyInput();
			
			var camera = game.getWindow().getRenderer().getCamera3D();
			
			// TODO add tiling to Movement3D and make it relative to the position looked at
			// Tilting the camera to the side
			var tiltLeft = ki.pressed(GLFW_KEY_COMMA);
			var tiltRight = ki.pressed(GLFW_KEY_PERIOD);
			var tiltAmount = tiltSpeed * dt;
			
			double changedTilt = 0;
			// Move back to zero while not tilting
			var rotZ = camera.getRotZ();
			if(rotZ != 0 && !tiltLeft && !tiltRight){
				// TODO make untilting also move the x rotation axis
				if(Math.abs(rotZ) < tiltAmount) {
					camera.setRotZ(0);
					currentTilt = 0;
				}
				else if(rotZ < 0) {
					camera.addRotZ(tiltAmount);
					currentTilt += tiltAmount;
				}
				else {
					camera.addRotZ(-tiltAmount);
					currentTilt -= tiltAmount;
				}
			}
			else{
				// TODO to do this properly, need to offset angles by 90 degrees?
				// Tilt left or right
				if(tiltLeft){
					if(currentTilt > -Math.PI * 0.5) changedTilt -= tiltAmount;
				}
				if(tiltRight){
					if(currentTilt < Math.PI * 0.5) changedTilt += tiltAmount;
				}
				currentTilt += changedTilt;
				camera.addRotX(changedTilt * -Math.sin(camera.getRotY()));
				camera.addRotZ(changedTilt * Math.cos(camera.getRotY()));
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
				
				player.setX(0);
				player.setY(0);
				player.setZ(2);
				
				player.clearMotion();
			}
			
			xRot += xRotSpeed * dt;
			yRot += yRotSpeed * dt;
			zRot += zRotSpeed * dt;
			
			// Rotate the pillar
			pillarAngle += 2 * dt;
		}
	}
	
	private static void updatePaused(boolean paused){
		var p = game.getPlayState();
		if(p == null) return;
		p.setPaused(paused);
		if(paused){
			game.getCurrentState().popupMenu(game, new Menu(600, 300, 500, 100, false){
				@Override
				public void render(Game game, Renderer r, ZRect2D bounds){
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
	
	private static class Player3D extends MovementEntityThing3D{
		private final Game game;
		
		public Player3D(Game game){
			super(100);
			this.game = game;
		}
		
		@Override
		public void tick(Game game, double dt){
			super.tick(game, dt);
			
			var ki = game.getKeyInput();
			// TODO make a better way of getting these angles?
			var cam = this.getCamera();
			
			var left = ki.buttonDown(GLFW_KEY_A);
			var right = ki.buttonDown(GLFW_KEY_D);
			var forward = ki.buttonDown(GLFW_KEY_W);
			var backward = ki.buttonDown(GLFW_KEY_S);
			var up = ki.buttonDown(GLFW_KEY_Q);
			var down = ki.buttonDown(GLFW_KEY_Z);
			this.handleMovementControls(dt, cam.getRotY(), cam.getRotX(), left, right, forward, backward, up, down, flying);
			if(!left && !right && !forward && !backward && this.getWalk().getWalkingForce().getMagnitude() != 0) this.stopWalking();
			
			// TODO abstract this out?
			// Move the camera to the player
			cam.setX(this.getX());
			cam.setY(this.getY() + this.getHeight());
			cam.setZ(this.getZ());
		}
		
		private GameCamera3D getCamera(){
			return this.game.getWindow().getRenderer().getCamera3D();
		}
		
		// TODO should this just be the camera?
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
		public double getSurfaceArea(){
			return 0;
		}
		
		@Override
		public double getFrictionConstant(){
			return this.getWalkFrictionConstant();
		}
		
		@Override
		public double getCurrentWalkingSpeed(){
			return this.getHorizontalVel();
		}
		
		@Override
		public void stopWalking(){
			this.getWalk().setWalkingForce(zeroVector());
		}
		
		@Override
		public double getJumpPower(){
			return 300.0;
		}
		
		@Override
		public double getJumpStopPower(){
			return 50.0;
		}
		
		@Override
		public double getJumpBuildTime(){
			return 0;
		}
		
		@Override
		public boolean isJumpAfterBuildUp(){
			return true;
		}
		
		@Override
		public double getWalkAcceleration(){
			return 4.0;
		}
		
		@Override
		public double getWalkSpeedMax(){
			return 2.6;
		}
		
		@Override
		public double getWalkAirControl(){
			return 0.5;
		}
		
		@Override
		public double getWalkFriction(){
			return 1.0;
		}
		
		@Override
		public double getWalkStopFriction(){
			return 1.0;
		}
		
		@Override
		public double getWalkingRatio(){
			return 0.5;
		}
		
		@Override
		public boolean isCanWallJump(){
			return true;
		}
		
		@Override
		public double getNormalJumpTime(){
			return 0.2;
		}
		
		@Override
		public double getWallJumpTime(){
			return 0.2;
		}
		
		@Override
		public boolean isWalking(){
			return !this.game.getKeyInput().shift();
		}
		
		@Override
		protected void render(Game game, Renderer r){
			r.setColor(new ZColor(.5, 0, 0));
			r.drawSidePlaneZ(this.getX(), this.getY(), this.getZ(), 0.2, this.getHeight(), this.getCamera().getRotY());
			
			// TODO make some way of rendering this differently when it's the selected thing to control?
		}
		
		@Override
		public double getWidth(){
			// TODO implement in an abstracted class
			return 0.3;
		}
		
		@Override
		public double getHeight(){
			// TODO implement in an abstracted class
			return 0.3;
		}
		
		@Override
		public double getLength(){
			// TODO implement in an abstracted class
			return 0.3;
		}
	}
	
}
