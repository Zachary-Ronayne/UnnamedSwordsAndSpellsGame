package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.keyboard.ZKeyInput;

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
		double speed = dt * 300;
		double move = 0;
		if(ki.buttonDown(GLFW_KEY_LEFT)) move = -speed;
		else if(ki.buttonDown(GLFW_KEY_RIGHT)) move = speed;
		this.setX(this.getX() + move);
		
		// Jump
		if(ki.buttonDown(GLFW_KEY_UP)) this.jump();
		
		// Center the camera to the player
		if(this.isLockCamera()) game.centerCamera(this.getCenterX(), this.getCenterY());
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(1, 0, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	/** @return See {@link #lockCamera} */
	public boolean isLockCamera(){
		return this.lockCamera;
	}

	/** @param lockCamera See {@link #lockCamera} */
	public void setLockCamera(boolean lockCamera){
		this.lockCamera = lockCamera;
	}
	
}
