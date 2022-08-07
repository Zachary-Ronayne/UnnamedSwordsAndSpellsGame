package zgame.things.entity;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.input.keyboard.ZKeyInput;
import zgame.world.Room;

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
	public void tick(Game<?> game, double dt){
		// Move left and right
		ZKeyInput ki = game.getKeyInput();
		if(ki.buttonDown(GLFW_KEY_LEFT)) this.walkLeft();
		else if(ki.buttonDown(GLFW_KEY_RIGHT)) this.walkRight();
		else this.stopWalking();
		
		// Jump if holding the jump button
		if(ki.buttonDown(GLFW_KEY_UP)) this.jump(dt);
		// For not holding the button
		else{
			// if jumps should be instant, or no jump time is being built up, then stop the jump
			if(this.jumpsAreInstant() || this.getJumpTimeBuilt() == 0) this.stopJump();
			// Otherwise, perform the built up jump
			else this.jumpFromBuiltUp(dt);
			
		}
		// Lastly, perform the normal game tick on the player
		super.tick(game, dt);
		
		// Now the camera to the player after repositioning the player
		this.checkCenterCamera(game);
	}
	
	@Override
	public void render(Game<?> game, Renderer r){
		r.setColor(1, 0, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	/**
	 * If the camera should be locked to this {@link Player}, then lock the camera, otherwise do nothing
	 * 
	 * @param game The game to get the camera from
	 */
	public void checkCenterCamera(Game<?> game){
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
	public void enterRoom(Room<?> from, Room<?> to, Game<?> game){
		super.enterRoom(from, to, game);
		if(to != null) game.getPlayState().setCurrentRoom(to);
		
		// Center the camera to the player
		this.checkCenterCamera(game);
	}
	
}
