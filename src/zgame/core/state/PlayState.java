package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.Room;

/** A {@link GameState} which is designed for game play, i.e. controlling a character in a world, not a menu */
public class PlayState extends GameState{
	
	/** The {@link Room} which is currently used by this {@link PlayState} */
	private Room currentRoom;
	
	public PlayState(){
		super(true);
		this.currentRoom = new Room();
	}
	
	/** @return See {@link #currentRoom} */
	public Room getCurrentRoom(){
		return this.currentRoom;
	}
	
	@Override
	public final void tick(Game game, double dt){
		this.tickO(game, dt);
		this.currentRoom.tick(game, dt);
	}
	
	@Override
	public final void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.keyActionO(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public final void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		this.mouseActionO(game, button, press, shift, alt, ctrl);
	}
	
	@Override
	public final void mouseMove(Game game, double x, double y){
		this.mouseMoveO(game, x, y);
	}
	
	@Override
	public final void mouseWheelMove(Game game, double amount){
		this.mouseWheelMoveO(game, amount);
	}
	
	@Override
	public final void renderBackground(Game game, Renderer r){
		this.renderBackgroundO(game, r);
	}
	
	@Override
	public final void render(Game game, Renderer r){
		this.renderO(game, r);
		this.currentRoom.render(game, r);
	}
	
	@Override
	public final void renderHud(Game game, Renderer r){
		this.renderHudO(game, r);
	}
	
	/** A version of {@link #tick(Game, double)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call directly */
	public void tickO(Game game, double dt){
	}
	
	/**
	 * A version of {@link #keyAction(Game, int, boolean, boolean, boolean, boolean)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call
	 * directly
	 */
	public void keyActionO(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	/**
	 * A version of {@link #mouseAction(Game, int, boolean, boolean, boolean, boolean)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call
	 * directly
	 */
	public void mouseActionO(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	/** A version of {@link #mouseMove(Game, double, double)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call directly */
	public void mouseMoveO(Game game, double x, double y){
	}
	
	/** A version of {@link #mouseWheelMove(Game, double)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call directly */
	public void mouseWheelMoveO(Game game, double amount){
	}
	
	/** A version of {@link #renderBackground(Game, Renderer)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call directly */
	public void renderBackgroundO(Game game, Renderer r){
	}
	
	/** A version of {@link #render(Game, Renderer)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call directly */
	public void renderO(Game game, Renderer r){
	}
	
	/** A version of {@link #renderHud(Game, Renderer)} which can be overwritten to make this {@link PlayState} perform extra actions. Do not call directly */
	public void renderHudO(Game game, Renderer r){
	}
}
