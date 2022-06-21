package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.keyboard.ZKeyInput;
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
		super.tick(game, dt);
		
		// Move left and right
		ZKeyInput ki = game.getKeyInput();
		// TODO make this walking thing in the mob class or maybe the entity class, and make variables for walking speed / acceleration
		double speed = dt * 80000;
		double move = 0;
		if(ki.buttonDown(GLFW_KEY_LEFT)) move = -speed;
		else if(ki.buttonDown(GLFW_KEY_RIGHT)) move = speed;
		// this.setX(this.getX() + move);
		// If the mob is moving faster than the maximum walking speed, don't add any more speed
		double maxSpeed = 300;
		if(Math.abs(this.getVelocity().getX()) > maxSpeed) move = 0;
		// If the mob is on the ground, then slow the mob down
		/*
		 * TODO make this like a friction constant
		 * It should be based on the thing it is colliding with
		 * The first number is the mob trying to stop moving, the second is the friction of the ground
		 */
		if(this.isOnGround()) this.addVelocityX(-this.getVelocity().getX() * ((move == 0) ? 0.1 : 0.01));
		this.setWalkingForce(move);
		
		// Jump
		if(ki.buttonDown(GLFW_KEY_UP)) this.jump();
		
		// Center the camera to the player
		this.checkCenterCamera(game);
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
