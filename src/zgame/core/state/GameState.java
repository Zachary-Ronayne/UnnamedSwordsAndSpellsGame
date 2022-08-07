package zgame.core.state;

import zgame.core.Game;
import zgame.core.GameInteractable;
import zgame.core.file.Saveable;
import zgame.core.graphics.Renderer;

/**
 * A class which handles a single state that a game can be in. A state could be things like playing the game, or being in the main menu.
 * 
 * A state is essentially a separate place where you can easily define what happens for input and rendering
 */
public abstract class GameState<D> implements GameInteractable<D>, Saveable{
	
	/** true if this state should use the camera for drawing the main graphics, false otherwise */
	private boolean useCamera;
	
	/** Create a new {@link GameState} which uses the camera for rendering */
	public GameState(){
		this(true);
	}
	
	/**
	 * Create a new {@link GameState} with the given parameters
	 * 
	 * @param useCamera See {@link #useCamera}
	 */
	public GameState(boolean useCamera){
		this.setUseCamera(useCamera);
	}

	/** @return See {@link #useCamera} */
	public boolean isUseCamera(){
		return this.useCamera;
	}
	
	/** @param useCamera See {@link #useCamera} */
	public void setUseCamera(boolean useCamera){
		this.useCamera = useCamera;
	}

	/**
	 * A method called when a {@link Game} sets its current state to this {@link GameState}. 
	 * Override this method to do something when it happens. Does nothing by default
	 * 
	 * @param game The {@link Game} which set its current state
	 */
	public void onSet(Game<D> game){

	}
	
	@Override
	public void tick(Game<D> game, double dt){
		
	}
	
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		
	}
	
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		
	}
	
	@Override
	public void mouseMove(Game<D> game, double x, double y){
		
	}
	
	@Override
	public void mouseWheelMove(Game<D> game, double amount){
		
	}
	
	@Override
	public void renderBackground(Game<D> game, Renderer r){
		
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		
	}
	
	@Override
	public void renderHud(Game<D> game, Renderer r){
	}
	
}
