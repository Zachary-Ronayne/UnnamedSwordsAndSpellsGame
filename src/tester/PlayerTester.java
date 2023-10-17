package tester;

import zgame.core.Game;
import zgame.physics.material.Material;
import zgame.things.entity.EntityThing;
import zgame.things.entity.Walk;
import zgame.world.Room;

import static org.lwjgl.glfw.GLFW.*;

public abstract class PlayerTester extends EntityThing {
	
	/** The width of this mob */
	private double width;
	/** The height of this mob */
	private double height;
	
	/** true to lock the camera to the center of the player, false otherwise */
	private boolean lockCamera;
	
	/** The {@link Walk} object used by this player */
	private final Walk walk;
	
	/**
	 * Create a new {@link PlayerTester} of the given size
	 *
	 * @param x The x coordinate of the {@link PlayerTester}
	 * @param y The y coordinate of the {@link PlayerTester}
	 * @param width The width of the {@link PlayerTester} hit box
	 * @param height The height of the {@link PlayerTester} hit box
	 */
	public PlayerTester(double x, double y, double width, double height){
		super(x, y);
		this.walk = new Walk(this);
		
		this.lockCamera = false;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void tick(Game game, double dt){
		var walk = this.getWalk();
		
		walk.updatePosition(game, dt);
		walk.tick(game, dt);
		
		var ki = game.getKeyInput();
		walk.handleMovementControls(ki.pressed(GLFW_KEY_LEFT), ki.pressed(GLFW_KEY_RIGHT), ki.pressed(GLFW_KEY_UP), dt);
		
		// Lastly, perform the normal game tick on the player
		super.tick(game, dt);
		
		// Now the camera to the player after repositioning the player
		this.checkCenterCamera(game);
	}
	
	/** @return See {@link #walk} */
	public Walk getWalk(){
		return this.walk;
	}
	
	/**
	 * If the camera should be locked to this {@link PlayerTester}, then lock the camera, otherwise do nothing
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
	
	/** @return See {@link #width} */
	@Override
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	@Override
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
	
	@Override
	public double getFrictionConstant(){
		return this.getWalk().getFrictionConstant();
	}
	
	@Override
	public void leaveFloor(){
		super.leaveFloor();
		this.getWalk().leaveFloor();
	}
	
	@Override
	public void leaveWall(){
		super.leaveWall();
		this.getWalk().leaveWall();
	}
	
	@Override
	public void touchFloor(Material touched){
		super.touchFloor(touched);
		this.getWalk().touchFloor(touched);
	}
}
