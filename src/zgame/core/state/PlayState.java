package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.world.Room;

/**
 * A {@link GameState} which is designed for game play, i.e. controlling a character in a world, not a menu
 */
public class PlayState extends GameState{
	
	/** The {@link Room} which is currently used by this {@link PlayState}. The system assumes this will always be an appropriate type of room for the game played */
	private Room<?, ?, ?, ?, ?> currentRoom;
	
	/** true if this {@link PlayState} is paused and should not perform tick updates, false otherwise */
	private boolean paused;
	/** true if this {@link PlayState} should not receive input, false otherwise */
	private boolean inputPaused;
	
	/**
	 * Create a basic empty play state with the given room
	 *
	 * @param room The room to use for the play state
	 */
	public PlayState(Room<?, ?, ?, ?, ?> room){
		super(true);
		this.currentRoom = room;
		this.paused = false;
		this.inputPaused = false;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.getCurrentRoom().destroy();
	}
	
	/** @return See {@link #currentRoom} */
	public Room<?, ?, ?, ?, ?> getCurrentRoom(){
		return this.currentRoom;
	}
	
	/**
	 * Set the room to use for this {@link PlayState} This method does nothing and returns false if r is null
	 *
	 * @param r See {@link #currentRoom}
	 * @return true if the room was set, false otherwise
	 */
	public boolean setCurrentRoom(Room<?, ?, ?, ?, ?> r){
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
	public void onSet(){
		super.onSet();
		// When going into the play state, consider all menus as closed
		Game.get().getRenderStyle().onAllMenusClosed();
	}
	
	@Override
	public void onMenuChange(boolean added){
		super.onMenuChange(added);
		var game = Game.get();
		if(added) game.getRenderStyle().onMenuOpened();
		else if(!this.hasMenu()) game.getRenderStyle().onAllMenusClosed();
	}
	
	@Override
	public final void tick(double dt){
		super.tick(dt);
		if(this.isPaused()) return;
		
		this.currentRoom.tick(dt);
	}
	
	@Override
	public final void keyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean currentlyPaused = this.isInputPaused();
		super.keyAction(button, press, shift, alt, ctrl);
		if(currentlyPaused) return;
		
		this.playKeyAction(button, press, shift, alt, ctrl);
	}
	
	/** See {@link #keyAction(int, boolean, boolean, boolean, boolean)} Override this method instead to perform actions when the keyboard buttons are pressed */
	public void playKeyAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	@Override
	public final boolean mouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean input = super.mouseAction(button, press, shift, alt, ctrl);
		if(this.isInputPaused()) return input;
		if(input) return true;
		
		return this.playMouseAction(button, press, shift, alt, ctrl);
	}
	
	/** See {@link #mouseAction(int, boolean, boolean, boolean, boolean)} Override this method instead to perform actions when the mouse buttons are pressed */
	public boolean playMouseAction(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		return false;
	}
	
	@Override
	public final boolean mouseMove(double x, double y){
		boolean input = super.mouseMove(x, y);
		if(this.isInputPaused()) return input;
		
		Game.get().getRenderStyle().mouseMove(x, y);
		
		return this.playMouseMove(x, y);
	}
	
	/** See {@link #mouseMove(double, double)} Override this method instead to perform actions when the mouse moves */
	public boolean playMouseMove(double x, double y){
		return false;
	}
	
	@Override
	public final boolean mouseWheelMove(double amount){
		boolean input = super.mouseWheelMove(amount);
		if(this.isInputPaused()) return input;
		
		return this.playMouseWheelMove(amount);
	}
	
	/** See {@link #mouseWheelMove(double)} Override this method instead to perform actions when a mouse wheel moves */
	public boolean playMouseWheelMove(double amount){
		return false;
	}
	
	@Override
	public void render(Renderer r){
		this.currentRoom.render(r);
		super.render(r);
	}
	
	@Override
	public PlayState asPlay(){
		return this;
	}
	
}
