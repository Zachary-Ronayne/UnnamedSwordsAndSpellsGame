package tester;

import static org.lwjgl.glfw.GLFW.*;
import zgame.core.Game;
import zgame.core.graphics.RectRender3D;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.PlayState;
import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint3D;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Materials;
import zgame.things.entity.mobility.MobilityEntity3D;
import zgame.things.entity.mobility.MobilityType;
import zgame.things.type.bounds.SphereHitBox;
import zgame.world.Room3D;

public class CollisionDemo3D extends Game{
	
	private static CollisionDemo3D game;
	
	private static final ZPoint3D sphere = new ZPoint3D(0, 1, 0);
	private static final double sRadius = 0.3;
	private static final double sSpeed = 0.05;
	private static final RectRender3D RECT = new RectRender3D(0, -0.5, 0, 1, 1, 1);
	
	public static void main(String[] args){
		
		game = new CollisionDemo3D();
		game.setInitSoundOnStart(false);
		
		game.getWindow().center();
		
		game.onNextLoop(() -> {
			game.setCurrentState(new Play());
			game.make3D();
		});
		
		game.start();
		
	}
	
	public static class Player extends MobilityEntity3D implements SphereHitBox{
		
		public Player(){
			super(1);
			this.getMobilityData().setType(MobilityType.FLYING_AXIS);
		}
		
		@Override
		public void tick(Game game, double dt){
			super.tick(game, dt);
			
			var ki = game.getKeyInput();
			var left = ki.buttonDown(GLFW_KEY_A);
			var right = ki.buttonDown(GLFW_KEY_D);
			var forward = ki.buttonDown(GLFW_KEY_W);
			var backward = ki.buttonDown(GLFW_KEY_S);
			var up = ki.buttonDown(GLFW_KEY_Q);
			var down = ki.buttonDown(GLFW_KEY_Z);
			var cam = game.getCamera3D();
			this.handleMobilityControls(dt, cam.getRotY(), cam.getRotX(), left, right, forward, backward, up, down);
			
			this.updateCameraPos(game.getCamera3D());
		}
		
		@Override
		public double getFrictionConstant(){
			return 0;
		}
		
		@Override
		public double getJumpPower(){
			return 1;
		}
		
		@Override
		public double getJumpStopPower(){
			return 1;
		}
		
		@Override
		public double getJumpBuildTime(){
			return 0;
		}
		
		@Override
		public boolean isJumpAfterBuildUp(){
			return false;
		}
		
		@Override
		public double getWalkPower(){
			return 3;
		}
		
		@Override
		public double getWalkSpeedMax(){
			return 1.2;
		}
		
		@Override
		public double getWalkAirControl(){
			return 1;
		}
		
		@Override
		public double getWalkFriction(){
			return 0;
		}
		
		@Override
		public double getWalkStopFriction(){
			return 1;
		}
		
		@Override
		public double getWalkingRatio(){
			return 1;
		}
		
		@Override
		public boolean isCanWallJump(){
			return false;
		}
		
		@Override
		public double getNormalJumpTime(){
			return 0;
		}
		
		@Override
		public double getWallJumpTime(){
			return 0;
		}
		
		@Override
		public boolean isWalking(){
			return false;
		}
		
		@Override
		protected void render(Game game, Renderer r){
//			var collision = ZCollision.rectToSphereBasic(
//					RECT.getX(), RECT.getY(), RECT.getZ(), RECT.getWidth(), RECT.getHeight(), RECT.getLength(),
//					this.getX(), this.getY(), this.getZ(), this.getRadius(), Materials.NONE
//			);
//
//			r.setColor(new ZColor(collision.hit() ? 1 : 0, 0, 0.5));
//			r.drawSphere(this.getX(), this.getY(), this.getZ(), this.getRadius());
		}
		
		@Override
		public double getRadius(){
			return 0.5;
		}
	}
	
	public static class Play extends PlayState{
		
		public Play(){
			super(new Room3D(0, 0, 0));
			
			var room = new Room3D(1, 1, 1);
			var player = new Player();
			player.setX(1.5);
			player.setY(0);
			player.setZ(1.5);
			room.addThing(player);
			room.setAllBoundaries(false);
			game.getCamera3D().setRotY(Math.PI * 1.75);
			setCurrentRoom(room);
		}
		
		@Override
		public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
			super.playKeyAction(game, button, press, shift, alt, ctrl);
			if(!press && button == GLFW_KEY_ESCAPE){
				var w = game.getWindow();
				w.setMouseNormally(!w.isMouseNormally());
			}
			if(!press && button == GLFW_KEY_SPACE){
				game.onNextLoop(() -> game.setCurrentState(new Play()));
			}
			if(!press){
				boolean left = button == GLFW_KEY_L;
				boolean right = button == GLFW_KEY_J;
				boolean back = button == GLFW_KEY_I;
				boolean forward = button == GLFW_KEY_K;
				double camA = game.getCamera3D().getRotY();
//				boolean invert = ZMath.angleNormalized(camA) < Math.PI;
				boolean invert = true;
				
				if(invert){
					if(left) sphere.setX(sphere.getX() - sSpeed);
					else if(right) sphere.setX(sphere.getX() + sSpeed);
					else if(forward) sphere.setZ(sphere.getZ() - sSpeed);
					else if(back) sphere.setZ(sphere.getZ() + sSpeed);
				}
				else{
					if(forward) sphere.setX(sphere.getX() + sSpeed);
					else if(back) sphere.setX(sphere.getX() - sSpeed);
					else if(left) sphere.setZ(sphere.getZ() + sSpeed);
					else if(right) sphere.setZ(sphere.getZ() - sSpeed);
				}
				
				if(button == GLFW_KEY_M) sphere.setY(sphere.getY() - sSpeed);
				else if(button == GLFW_KEY_U) sphere.setY(sphere.getY() + sSpeed);
			}
		}
		
		@Override
		public void render(Game game, Renderer r){
			super.render(game, r);
			
			var c = new ZColor(1, 0, 0, 0.5);
			r.drawRectPrism(RECT, c, c, c, c, c, c);
			
			var sphereB = new RectRender3D(sphere.getX(), sphere.getY(), sphere.getZ(), sRadius * 2, sRadius * 2, sRadius * 2);
			var collision = ZCollision.rectToSphereBasic(
					RECT.getX(), RECT.getY(), RECT.getZ(), RECT.getWidth(), RECT.getHeight(), RECT.getLength(),
					sphere.getX(), sphere.getY(), sphere.getZ(), sRadius, Materials.NONE
			);
			
			r.setColor(new ZColor(collision.hit() ? 1 : 0, 0, 0.5));
			r.drawSphere(sphereB.getX(), sphereB.getY(), sphereB.getZ(), sRadius);
			
			
			sphereB = new RectRender3D(sphereB);
			sphereB.setX(sphereB.getX() + collision.x());
			sphereB.setY(sphereB.getY() + collision.y());
			sphereB.setZ(sphereB.getZ() + collision.z());
			
			r.setColor(new ZColor(0, 1, 0, 0.5));
			r.drawSphere(sphereB.getX(), sphereB.getY(), sphereB.getZ(), sRadius);
			
			drawSampleCube(r, true, true, true);
			drawSampleCube(r, true, true, false);
			drawSampleCube(r, true, false, true);
			drawSampleCube(r, true, false, false);
			drawSampleCube(r, false, true, true);
			drawSampleCube(r, false, true, false);
			drawSampleCube(r, false, false, true);
			drawSampleCube(r, false, false, false);
		}
		
		private void drawSampleCube(Renderer r, boolean left, boolean up, boolean back){
			var c = new ZColor(left ? 0.2 : 1, up ? 0.2 : 1, back ? 0.2 : 1);
			r.drawRectPrism(new RectRender3D(left ? -2 : 2, up ? -2 : 2, back ? -2 : 2, 0.1, 0.1, 0.1), c, c, c, c, c, c);
		}
	}
}
