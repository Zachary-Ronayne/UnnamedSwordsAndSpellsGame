package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.world.Room;

/**
 * A {@link GameState} which is designed for game play, i.e. controlling a character in a world, not a menu
 */
public class PlayState extends GameState{
	
	/** The {@link Room} which is currently used by this {@link PlayState} */
	private Room currentRoom;
	
	/** true if this {@link PlayState} is paused and should not perform tick updates, false otherwise */
	private boolean paused;
	/** true if this {@link PlayState} should not receive input, false otherwise */
	private boolean inputPaused;
	
	/**
	 * Create a basic empty play state with an empty default room
	 */
	public PlayState(){
		this(true);
	}
	
	/**
	 * Create a basic empty play state
	 *
	 * @param createRoom true to give the {@link PlayState} an empty default room, otherwise use false and call {@link #setCurrentRoom(Room)}
	 */
	public PlayState(boolean createRoom){
		super(true);
		if(createRoom) this.currentRoom = new Room();
		this.paused = false;
		this.inputPaused = false;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.getCurrentRoom().destroy();
	}
	
	/** @return See {@link #currentRoom} */
	public Room getCurrentRoom(){
		return this.currentRoom;
	}
	
	/**
	 * Set the room to use for this {@link PlayState} This method does nothing and returns false if r is null
	 *
	 * @param r See {@link #currentRoom}
	 * @return true if the room was set, false otherwise
	 */
	public boolean setCurrentRoom(Room r){
		if(r == null) return false;
		this.currentRoom = r;
		return true;
	}
	
	/** @return See {@link #paused} */
	public boolean isPaused(){
		return this.paused;
	}
	
	/** @param paused See {@link #paused} */
	public void setPaused(boolean paused){
		this.paused = paused;
	}
	
	/** @return See {@link #inputPaused} */
	public boolean isInputPaused(){
		return this.inputPaused;
	}
	
	/** @param inputPaused See {@link #inputPaused} */
	public void setInputPaused(boolean inputPaused){
		this.inputPaused = inputPaused;
	}
	
	/** Pause all tick updates and input for this {@link PlayState} */
	public void fullPause(){
		this.setPaused(true);
		this.setInputPaused(true);
	}
	
	/** Unpause all tick updates and input for this {@link PlayState} */
	public void fullUnpause(){
		this.setPaused(false);
		this.setInputPaused(false);
	}
	
	@Override
	public void onSet(Game game){
		super.onSet(game);
		// When going into the play state, consider all menus as closed
		game.getRenderStyle().onAllMenusClosed(game);
	}
	
	@Override
	public void onMenuChange(Game game, boolean added){
		super.onMenuChange(game, added);
		if(added) game.getRenderStyle().onMenuOpened(game);
		else if(!this.hasMenu()) game.getRenderStyle().onAllMenusClosed(game);
	}
	
	@Override
	public final void tick(Game game, double dt){
		super.tick(game, dt);
		if(this.isPaused()) return;
		
		this.currentRoom.tick(game, dt);
	}
	
	@Override
	public final void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean currentlyPaused = this.isInputPaused();
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(currentlyPaused) return;
		
		this.playKeyAction(game, button, press, shift, alt, ctrl);
	}
	
	/** See {@link #keyAction(Game, int, boolean, boolean, boolean, boolean)} Override this method instead to perform actions when the keyboard buttons are pressed */
	public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	@Override
	public final boolean mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean input = super.mouseAction(game, button, press, shift, alt, ctrl);
		if(this.isInputPaused()) return input;
		if(input) return true;
		
		return this.playMouseAction(game, button, press, shift, alt, ctrl);
	}
	
	/** See {@link #mouseAction(Game, int, boolean, boolean, boolean, boolean)} Override this method instead to perform actions when the mouse buttons are pressed */
	public boolean playMouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		return false;
	}
	
	@Override
	public final boolean mouseMove(Game game, double x, double y){
		boolean input = super.mouseMove(game, x, y);
		if(this.isInputPaused()) return input;
		
		return this.playMouseMove(game, x, y);
	}
	
	/** See {@link #mouseMove(Game, double, double)} Override this method instead to perform actions when the mouse moves */
	public boolean playMouseMove(Game game, double x, double y){
		return false;
	}
	
	@Override
	public final boolean mouseWheelMove(Game game, double amount){
		boolean input = super.mouseWheelMove(game, amount);
		if(this.isInputPaused()) return input;
		
		return this.playMouseWheelMove(game, amount);
	}
	
	/** See {@link #mouseWheelMove(Game, double)} Override this method instead to perform actions when a mouse wheel moves */
	public boolean playMouseWheelMove(Game game, double amount){
		return false;
	}
	
	@Override
	public void render(Game game, Renderer r){
		this.currentRoom.render(game, r);
		super.render(game, r);
	}
	
	@Override
	public PlayState asPlay(){
		return this;
	}
	
}
