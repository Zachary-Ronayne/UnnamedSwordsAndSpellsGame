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
		if(createRoom) this.currentRoom = new Room();
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
	 * Set the room to use for this {@link PlayState}
	 * This method does nothing and returns false if r is null
	 * 
	 * @param r See {@link #currentRoom}
	 * @return true if the room was set, false otherwise
	 */
	public boolean setCurrentRoom(Room r){
		if(r == null) return false;
		this.currentRoom = r;
		return true;
	}
	
	@Override
	public void tick(Game game, double dt){
		this.currentRoom.tick(game, dt);
	}
	
	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
		this.currentRoom.render(game, r);
	}
}
