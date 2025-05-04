package tester;

import zgame.core.Game;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.RotRender3D;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.camera.GameCamera3D;
import zgame.core.graphics.image.ImageManager;
import zgame.core.state.PlayState;
import zgame.core.utils.ZMath;
import zgame.core.utils.ZRect2D;
import zgame.menu.Menu;
import zgame.physics.material.Materials;
import zgame.settings.BooleanTypeSetting;
import zgame.settings.DoubleTypeSetting;
import zgame.settings.IntTypeSetting;
import zgame.things.entity.EntityThing3D;
import zgame.things.entity.mobility.MobilityEntity3D;
import zgame.things.entity.mobility.MobilityType;
import zgame.things.still.tiles.BaseTiles3D;
import zgame.things.still.tiles.CubeTexTile;
import zgame.things.still.tiles.TileHitbox3D;
import zgame.things.type.bounds.CylinderHitbox;
import zgame.things.type.bounds.RectPrismHitbox;

import static zgame.world.Direction3D.*;

import zgame.world.Room3D;

import static org.lwjgl.glfw.GLFW.*;

public class GameDemo3D extends Game{
	
	private static GameDemo3D game;
	
	private static double xRot = 0;
	private static double yRot = 0;
	private static double zRot = 0;
	
	private static boolean autoRotate = false;
	private static double xRotSpeed = 0;
	private static double yRotSpeed = 0;
	private static double zRotSpeed = 0;
	
	private static double pillarAngle = 0;
	
	private static final double tiltSpeed = 3;
	
	private static Player3D player;
	private static boolean tinyPlayer = false;
	private static DummyRoom dummyRoom;
	
	// Custom tiles for this demo
	public static final String DEMO_GAME_TILE_ORIGIN = "demoGame";
	
	/** A tile with a solid hitbox with a generic brick texture */
	public static final CubeTexTile BRICK_GRAY = new CubeTexTile("brick", DEMO_GAME_TILE_ORIGIN, "brick", TileHitbox3D.FULL, Materials.DEFAULT);
	
	public GameDemo3D(){
		super();
	}
	
	@Override
	public void init(){
		super.init();
		this.make3D();
		
		var window = this.getWindow();
		window.setSizeUniform(1500, 900);
		window.center();
		
		ImageManager.instance().add("brick");
	}
	
	public static void main(String[] args){
		game = new GameDemo3D();
		
		// Don't use any sound for this game
		game.setInitSoundOnStart(false);
		
		game.set(BooleanTypeSetting.V_SYNC, true, false);
		game.set(IntTypeSetting.FPS_LIMIT, 0, false);
		
		game.setPrintTps(false);
		game.setPrintFps(false);
		
		var window = game.getWindow();
		window.setWindowTitle("Cube Demo");
		
		dummyRoom = new DummyRoom();
		var state = new DemoGameState(dummyRoom);
		game.setCurrentState(state);
		
		updatePaused(true);
		
		player = new Player3D();
		player.setZ(2);
		dummyRoom.addThing(player);
		
		var movingRect = new Rect(2){
			@Override
			public void tick(double dt){
				super.tick(dt);
			}
		};
		movingRect.setY(2);
		movingRect.setX(-1.9);
		var movingCylinder = new Cylinder(2);
		dummyRoom.addThing(movingRect);
		movingCylinder.setY(3);
		movingCylinder.setX(1.9);
		dummyRoom.addThing(movingCylinder);
		
		var staticRect = new Rect(0);
		staticRect.setX(0.75);
		staticRect.setY(2.85);
		var staticCylinder = new Cylinder(0);
		staticCylinder.setX(-0.75);
		staticCylinder.setY(2.85);
		dummyRoom.addThing(staticRect);
		dummyRoom.addThing(staticCylinder);
		
		staticRect = new Rect(0);
		staticRect.setX(0.75);
		staticRect.setY(2.3);
		staticCylinder = new Cylinder(0);
		staticCylinder.setX(-0.75);
		staticCylinder.setY(2.3);
		dummyRoom.addThing(staticRect);
		dummyRoom.addThing(staticCylinder);
		
		staticRect = new Rect(0);
		staticRect.setX(-2);
		staticRect.setY(1);
		staticRect.setZ(-2);
		staticCylinder = new Cylinder(0);
		staticCylinder.setX(-2);
		staticCylinder.setY(1);
		staticCylinder.setZ(-3);
		dummyRoom.addThing(staticRect);
		dummyRoom.addThing(staticCylinder);
		
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
		public void render(Renderer r){
			super.render(r);
			
			// Draw the cube
			r.drawRectPrism(new RectRender3D(0, .2, 0, .6, .6, .6, RotRender3D.axis(xRot, yRot, zRot, 0, -.3, 0)),
					new ZColor(1, 0, 0), new ZColor(1, 1, 0),
					new ZColor(0, 1, 0), new ZColor(0, 1, 1),
					new ZColor(0, 0, 1), new ZColor(1, 0, 1));
			
			// Draw a checkerboard floor
			for(int x = 0; x < 40; x++){
				for(int z = 0; z < 48; z++){
					r.setColor(new ZColor(((x % 2 == 0) != (z % 2 == 0)) ? .2 : .6));
					r.drawFlatPlane(x * .25 - 4.875, 0, z * .25 - 5.875, .25, .25);
				}
			}
			
			// Draw a rectangular prism that rotates only on the y axis
			r.drawRectPrism(new RectRender3D(2, 0, 3, .1, .8, .1, pillarAngle),
					new ZColor(.5, .5, .8), new ZColor(.5, .5, .4),
					new ZColor(.5, .5, .6), new ZColor(.5, .5, .2),
					new ZColor(.5, .5, 1), new ZColor(0, 0));
			r.drawRectPrism(new RectRender3D(2.5, 0, 3, .1, .8, .1, pillarAngle),
					new ZColor(.5, .5, .8), new ZColor(.5, .5, .4),
					new ZColor(.5, .5, .6), new ZColor(.5, .5, .2, 0),
					new ZColor(.5, .5, 1), new ZColor(0, 0));
			
			// Draw some transparent cubes
			for(int i = 0; i < 3; i++){
				r.drawRectPrism(new RectRender3D(-2 + i * .2, .45 - i * .05, -3 + i * .2, .1, .1, .1, Math.PI * .25 * i),
						new ZColor(1, 0, 0, .5), new ZColor(1, 1, 0, .5),
						new ZColor(0, 1, 0, .5), new ZColor(0, 1, 1, .5),
						new ZColor(0, 0, 1, .5), new ZColor(1, 0, 1, .5));
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
			for(int x = 0; x < 40; x++){
				for(int y = 0; y < 4; y++){
					r.setColor(new ZColor(((x % 2 == 0) != (y % 2 == 0)) ? .2 : .6, 0, .5));
					r.drawSidePlaneX(x * .25 - 4.875, y * .25, 6, .25, .25);
				}
			}
			for(int z = 0; z < 48; z++){
				for(int y = 0; y < 4; y++){
					r.setColor(new ZColor(.5, 0, ((z % 2 == 0) != (y % 2 == 0)) ? .2 : .6));
					r.drawSidePlaneZ(5, y * .25, z * .25 - 5.875, .25, .25);
				}
			}
		}
		
		@Override
		public void renderHud(Renderer r){
			super.renderHud(r);
			r.setFontSize(30);
			var s = "FPS: " + game.getFps();
			
			r.setColor(new ZColor(0));
			r.drawRectangle(20, 6, 10 + r.getFont().stringWidth(s), 40);
			
			r.setColor(new ZColor(1));
			r.drawText(24, 34, s);
		}
		
		@Override
		public void playKeyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.playKeyAction(button, press, shift, alt, ctrl);
			
			if(press) return;
			
			// Toggle paused
			if(button == GLFW_KEY_ESCAPE) updatePaused(!game.getPlayState().isPaused());
			
			// Init sound separately from the main startup
			if(button == GLFW_KEY_P){
				if(ctrl) game.initSound();
				else if(shift) game.playMusic("song.ogg");
				else game.getSounds().addMusic("song.ogg");
			}
			
			// Modify FOV
			if(button == GLFW_KEY_LEFT_BRACKET) game.set(DoubleTypeSetting.FOV, game.get(DoubleTypeSetting.FOV) - .1, false);
			else if(button == GLFW_KEY_RIGHT_BRACKET) game.set(DoubleTypeSetting.FOV, game.get(DoubleTypeSetting.FOV) + .1, false);
				
				// Toggle no clip for the player
			else if(button == GLFW_KEY_N) player.setNoClip(!player.isNoClip());
				
				// Toggle tiny mode
			else if(button == GLFW_KEY_T) tinyPlayer = !tinyPlayer;
			
			// Numerical button related controls
			
			// Toggle the boundaries
			if(shift){
				var r = getCurrentRoom();
				if(button == GLFW_KEY_1) r.toggleBoundary(WEST);
				else if(button == GLFW_KEY_2) r.toggleBoundary(EAST);
				else if(button == GLFW_KEY_3) r.toggleBoundary(NORTH);
				else if(button == GLFW_KEY_4) r.toggleBoundary(SOUTH);
				else if(button == GLFW_KEY_5) r.toggleBoundary(UP);
				else if(button == GLFW_KEY_6) r.toggleBoundary(DOWN);
				else if(button == GLFW_KEY_7) dummyRoom.toggleUsingTileBoundary();
			}
			else{
				
				// Toggling the cube rotating on its own
				if(button == GLFW_KEY_1) autoRotate = !autoRotate;
				
				// Toggling full screen
				if(button == GLFW_KEY_2) game.toggleFullscreen();
				/*
				   issue#38 fix OpenGL warning when exiting full screen
					[LWJGL] OpenGL debug message
					ID: 0x20092
					Source: API
					Type: PERFORMANCE
					Severity: MEDIUM
					Message: Program/shader state performance warning: Vertex shader in program 12 is being recompiled based on GL state.
				 */
			}
			
			// Toggle fly
			if(button == GLFW_KEY_F){
				var mobilityData = player.getMobilityData();
				mobilityData.setType(mobilityData.getType() == MobilityType.WALKING ? (shift ? MobilityType.FLYING_AXIS : MobilityType.FLYING) : MobilityType.WALKING);
			}
			
			// Toggle vsync
			if(button == GLFW_KEY_V) game.toggle(BooleanTypeSetting.V_SYNC, false);
			
			// issue#39 allow for a third-person perspective and build it into the engine
		}
	}
	
	private static class DummyRoom extends Room3D{
		
		private boolean usingTileBoundary;
		
		public DummyRoom(){
			super(5, 3, 5);
			this.setTile(1, 0, 0, BaseTiles3D.BOUNCY);
			this.setTile(1, 0, 2, BaseTiles3D.HIGH_FRICTION);
			this.setTile(1, 0, 4, GameDemo3D.BRICK_GRAY);
			this.setTile(2, 0, 4, BaseTiles3D.SOLID_LIGHT);
			this.setTile(2, 1, 3, BaseTiles3D.SOLID_DARK);
			
			for(int i = 0; i < 3; i++){
				this.setTile(4, 0, i, GameDemo3D.BRICK_GRAY);
			}
			
			this.usingTileBoundary = false;
			this.setGenericBoundaries();
		}
		
		public void toggleUsingTileBoundary(){
			this.usingTileBoundary = !this.usingTileBoundary;
			if(this.usingTileBoundary) this.setTileBoundaries();
			else this.setGenericBoundaries();
		}
		
		public void setGenericBoundaries(){
			this.setEqualWidth(10);
			this.setEqualLength(12);
			
			this.setBoundary(DOWN, 0);
			this.setBoundary(UP, 4);
			this.setBoundary(UP, false);
		}
		
		@Override
		public void tick(double dt){
			super.tick(dt);
			var ki = game.getKeyInput();
			
			var mobilityData = player.getMobilityData();
			
			// Tilting the camera to the side
			var tiltLeft = ki.pressed(GLFW_KEY_COMMA);
			var tiltRight = ki.pressed(GLFW_KEY_PERIOD);
			var tiltAmount = tiltSpeed * dt;
			
			// Move back to zero while not tilting
			var roll = mobilityData.getFacingRoll();
			if(roll != 0 && !tiltLeft && !tiltRight){
				if(Math.abs(roll) < tiltAmount) mobilityData.setFacingRoll(0);
				else if(roll < 0) mobilityData.addFacingRoll(tiltAmount);
				else mobilityData.addFacingRoll(-tiltAmount);
			}
			else{
				// Tilt left or right
				if(tiltLeft) if(mobilityData.getFacingRoll() > -Math.PI * 0.5) mobilityData.addFacingRoll(-tiltAmount);
				if(tiltRight) if(mobilityData.getFacingRoll() < Math.PI * 0.5) mobilityData.addFacingRoll(tiltAmount);
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
			
			if(ki.pressed(GLFW_KEY_R)){
				xRot = 0;
				yRot = 0;
				zRot = 0;
				
				player.setX(0);
				player.setY(0);
				player.setZ(2);
				
				player.clearMotion();
				var m = player.getMobilityData();
				m.setFacingYaw(0);
				m.setFacingPitch(0);
				m.setFacingRoll(0);
			}
			
			xRot += xRotSpeed * dt;
			yRot += yRotSpeed * dt;
			zRot += zRotSpeed * dt;
			
			// Rotate the pillar
			pillarAngle += 2 * dt;
		}
		
		@Override
		public void render(Renderer r){
			super.render(r);
			// Draw a marker at the origin
			var c = new ZColor(.5, 0, 0, 0.5);
			r.drawRectPrism(new RectRender3D(0, 0, 0, 0.1, 0.1, 0.1), c, c, c, c, c, c);
			
			// Draw markers at each axis direction
			// Left, west, negative x: red
			c = new ZColor(.5, 0, 0, 1);
			r.drawRectPrism(new RectRender3D(-.75, 0, 0, 0.1, 0.1, 0.1), c, c, c, c, c, c);
			// Right, east, positive x: green
			c = new ZColor(0, 0.5, 0, 1);
			r.drawRectPrism(new RectRender3D(.75, 0, 0, 0.1, 0.1, 0.1), c, c, c, c, c, c);
			// Backward, south, positive z: blue
			c = new ZColor(0, 0, 0.5, 1);
			r.drawRectPrism(new RectRender3D(0, 0, .75, 0.1, 0.1, 0.1), c, c, c, c, c, c);
			// Forward, north, negative z, cyan
			c = new ZColor(0, 0.5, 0.5, 1);
			r.drawRectPrism(new RectRender3D(0, 0, -.75, 0.1, 0.1, 0.1), c, c, c, c, c, c);
		}
	}
	
	private static abstract class MovingThing extends EntityThing3D{
		
		private boolean movingLeft;
		private final double speed;
		private boolean intersecting;
		
		public MovingThing(double speed){
			super(1);
			this.speed = speed;
			this.movingLeft = false;
			this.setGravityLevel(0);
			this.intersecting = false;
		}
		
		/** @return See {@link #intersecting} */
		public boolean isIntersecting(){
			return this.intersecting;
		}
		
		@Override
		public double getGravityDragReferenceArea(){
			return 1;
		}
		
		@Override
		public double getFrictionConstant(){
			return 0;
		}
		
		@Override
		public double getWidth(){
			return 0.5;
		}
		
		@Override
		public double getHeight(){
			return 0.5;
		}
		
		@Override
		public double getLength(){
			return 0.5;
		}
		
		@Override
		public void tick(double dt){
			super.tick(dt);
			double dx = dt * speed;
			
			if(this.getX() < -1.5) movingLeft = false;
			else if(this.getX() > 1.5) movingLeft = true;
			
			if(movingLeft) this.addX(-dx);
			else this.addX(dx);
			
			boolean foundIntersection = false;
			var things = dummyRoom.getHitBoxThings();
			if(things != null){
				for(var t : things){
					if(t != this && t.intersects(this)){
						foundIntersection = true;
						break;
					}
				}
			}
			this.intersecting = foundIntersection;
		}
	}
	
	private static class Rect extends MovingThing implements RectPrismHitbox{
		public Rect(double speed){
			super(speed);
		}
		
		@Override
		protected void render(Renderer r){
			ZColor c;
			if(this.isIntersecting()) c = new ZColor(1, 1, 0);
			else c = new ZColor(1, 0, 0);
			r.drawRectPrism(new RectRender3D(this.getBounds()), c, c, c, c, c, c);
		}
	}
	
	private static class Cylinder extends MovingThing implements CylinderHitbox{
		public Cylinder(double speed){
			super(speed);
		}
		
		@Override
		public double getRadius(){
			return 0.25;
		}
		
		@Override
		protected void render(Renderer r){
			ZColor c;
			if(this.isIntersecting()) c = new ZColor(0, 1, 1);
			else c = new ZColor(0, 0, 1);
			r.setColor(c);
			r.drawEllipse3D(this.getX(), this.getY(), this.getZ(), this.getWidth(), this.getHeight());
			r.drawEllipse3D(this.getX(), this.getY() + this.getHeight(), this.getZ(), this.getWidth(), this.getLength());
			var renderBounds = new RectRender3D(this.getBounds());
			renderBounds.setWidth(0.1);
			renderBounds.setLength(0.1);
			r.drawRectPrism(renderBounds, c, c, c, c, c, c);
		}
	}
	
	private static void updatePaused(boolean paused){
		var p = game.getPlayState();
		if(p == null) return;
		p.setPaused(paused);
		if(paused){
			game.getCurrentState().popupMenu(new Menu(600, 300, 500, 100, false){
				@Override
				public void render(Renderer r, ZRect2D bounds){
					super.render(r, bounds);
					// issue#28 fix proper transparency not working here, probably something with buffers
					r.setColor(new ZColor(0, .5));
					r.drawRectangle(bounds);
					
					r.setColor(new ZColor(1));
					r.setFontSize(40);
					r.drawText(bounds.getX() + 30, bounds.getY() + 50, "PAUSED");
				}
			});
		}
		else game.getCurrentState().removeTopMenu();
	}
	
	public static class Player3D extends MobilityEntity3D implements CylinderHitbox{
		
		public Player3D(){
			super(100);
		}
		
		@Override
		public void tick(double dt){
			super.tick(dt);
			
			var ki = game.getKeyInput();
			var left = ki.buttonDown(GLFW_KEY_A);
			var right = ki.buttonDown(GLFW_KEY_D);
			var forward = ki.buttonDown(GLFW_KEY_W);
			var backward = ki.buttonDown(GLFW_KEY_S);
			var up = ki.buttonDown(GLFW_KEY_Q);
			var down = ki.buttonDown(GLFW_KEY_Z);
			var cam = this.getCamera();
			this.handleMobilityControls(dt, cam.getYaw(), cam.getPitch(), left, right, forward, backward, up, down);
			
			// Move the camera to the player
			this.updateCameraPos(cam);
		}
		
		@Override
		public double getGravityDragReferenceArea(){
			return CylinderHitbox.super.getGravityDragReferenceArea();
		}
		
		private GameCamera3D getCamera(){
			return Game.get().getCamera3D();
		}
		
		@Override
		public double getFrictionConstant(){
			return this.getWalkFrictionConstant();
		}
		
		@Override
		public double getJumpPower(){
			return 500.0;
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
		public double getWalkPower(){
			return 70000.0;
		}
		
		@Override
		public double getWalkSpeedMax(){
			return 1.8;
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
			return 10.0;
		}
		
		@Override
		public double getSprintingRatio(){
			return 1.5;
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
		public double getFlyStopPower(){
			return 30.0;
		}
		
		@Override
		public double getFlySpeedMax(){
			return 3.4;
		}
		
		@Override
		public double getFlyPower(){
			return 100000.0;
		}
		
		@Override
		public boolean isSprinting(){
			return Game.get().getKeyInput().shift();
		}
		
		@Override
		protected void render(Renderer r){
			// issue#41 make some way of rendering this differently when it's the selected thing to control?
			r.setColor(new ZColor(.5, 0, 0));
			double diameter = this.getRadius() * 2.0;
			r.drawSidePlaneZ(this.getX(), this.getY(), this.getZ(), diameter, this.getHeight(), ZMath.PI_BY_2 - this.getCamera().getYaw());
			
			// Pseudo shadow
			r.setColor(new ZColor(0, 0, 0, 0.5));
			r.drawEllipse3D(this.getX(), 0.001, this.getZ(), diameter, diameter);
		}
		
		@Override
		public double getHeight(){
			return tinyPlayer ? 0.1 : 0.95;
		}
		
		@Override
		public double getRadius(){
			return tinyPlayer ? 0.01 : 0.25;
		}
	}
	
}
