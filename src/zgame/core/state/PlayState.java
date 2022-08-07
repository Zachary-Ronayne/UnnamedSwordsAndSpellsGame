package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.world.Room;

/**
 * A {@link GameState} which is designed for game play, i.e. controlling a character in a world, not a menu
 * 
 * @param <D> The type of data that can be stored alongside the associated {@link Game}
 */
public class PlayState<D> extends GameState<D>{
	
	/** The {@link Room} which is currently used by this {@link PlayState} */
	private Room<D> currentRoom;
	
	/**
	 * Create a basic empty play state with an empty default room
	 */
	public PlayState(){
		this(true);
	}
	
	/**
	 * 
	 * Create a basic empty play state
	 * 
	 * @param createRoom true to give the {@link PlayState} an empty default room, otherwise use false and call {@link #setCurrentRoom(Room)}
	 */
	public PlayState(boolean createRoom){
		super(true);
		if(createRoom) this.currentRoom = new Room<D>();
	}
	
	/** @return See {@link #currentRoom} */
	public Room<D> getCurrentRoom(){
		return this.currentRoom;
	}
	
	/**
	 * Set the room to use for this {@link PlayState}
	 * This method does nothing and returns false if r is null
	 * 
	 * @param r See {@link #currentRoom}
	 * @return true if the room was set, false otherwise
	 */
	// Suppressing this method's cast because types are stupid sometimes
	@SuppressWarnings("unchecked")
	public boolean setCurrentRoom(Room<?> r){
		if(r == null) return false;
		this.currentRoom = (Room<D>)r;
		return true;
	}
	
	@Override
	public void tick(Game<D> game, double dt){
		this.currentRoom.tick(game, dt);
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
		this.currentRoom.render(game, r);
	}
	
	@Override
	public void renderHud(Game<D> game, Renderer r){
	}
}
