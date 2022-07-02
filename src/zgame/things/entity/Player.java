package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.core.utils.ZMathUtils;
import zgame.things.Room;

import static org.lwjgl.glfw.GLFW.*;

/** A {@link MobRectangle} which represents the character which can be controlled by the human player of the game */
public class Player extends MobRectangle{
	
	/** true to lock the camera to the center of the player, false otherwise */
	private boolean lockCamera;
	
	/**
	 * Create a new {@link Player} of the given size
	 * 
	 * @param x The x coordinate of the {@link Player}
	 * @param y The y coordinate of the {@link Player}
	 * @param width The width of the {@link Player} hit box
	 * @param height The height of the {@link Player} hit box
	 */
	public Player(double x, double y, double width, double height){
		super(x, y, width, height);
		this.lockCamera = false;
	}
	
	@Override
	public void tick(Game game, double dt){
		// TODO move this to the mob class, abstract out moving left and right
		// First handle mob movement
		double mass = this.getMass();
		double acceleration = this.getWalkAcceleration();
		double walkForce = acceleration * mass;
		
		// Move left and right
		ZKeyInput ki = game.getKeyInput();
		if(ki.buttonDown(GLFW_KEY_LEFT)) walkForce = -acceleration;
		else if(ki.buttonDown(GLFW_KEY_RIGHT)) walkForce = acceleration;
		else walkForce = 0;
		boolean walking = walkForce != 0;

		/*
		 * TODO make this like a friction constant
		 * It should be based on the thing it is colliding with
		 * The first number is the mob trying to stop moving, the second is the friction of the ground
		 */
		// If the mob is on the ground, then slow the mob down
		double frictionMove = 0;
		if(this.isOnGround()) frictionMove = -this.getVX() * (walking ? 0.05 : 0.1);
		// Otherwise, modify the movement based on the amount of control in the air
		else walkForce *= this.getWalkAirControl();
		this.addVX(frictionMove);
		
		// If the mob is not trying to move and is moving so slowly that the friction would stop it, then set the x velocity to zero
		// TODO make this a friction value in a material
		if(!walking && Math.abs(this.getVX()) < 0.00000001) this.setVX(0);
		// Otherwise, the mob should walk
		else{
			// If walking speed is already at max, and walking would increase the x axis speed, don't move at all
			double vx = this.getVX();
			if(Math.abs(vx) > this.getWalkSpeedMax() && ZMathUtils.sameSign(vx, walkForce)) walkForce = 0;
			
			// Set the amount the mob is walking
			this.setWalkingForce(walkForce);
		}
		
		// Jump
		if(ki.buttonDown(GLFW_KEY_UP)) this.jump();
		
		// Center the camera to the player
		this.checkCenterCamera(game);
		
		// Lastly, perform the normal game tick on the player
		super.tick(game, dt);
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(1, 0, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	/**
	 * If the camera should be locked to this {@link Player}, then lock the camera, otherwise do nothing
	 * 
	 * @param game The game to get the camera from
	 */
	public void checkCenterCamera(Game game){
		if(this.isLockCamera()) this.centerCamera(game);
	}
	
	/** @return See {@link #lockCamera} */
	public boolean isLockCamera(){
		return this.lockCamera;
	}
	
	/** @param lockCamera See {@link #lockCamera} */
	public void setLockCamera(boolean lockCamera){
		this.lockCamera = lockCamera;
	}
	
	@Override
	public void enterRoom(Room from, Room to, Game game){
		super.enterRoom(from, to, game);
		if(to != null) game.getPlayState().setCurrentRoom(to);
		
		// Center the camera to the player
		this.checkCenterCamera(game);
	}
	
}
