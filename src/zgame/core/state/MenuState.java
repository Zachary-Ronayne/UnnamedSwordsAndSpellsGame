package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** A generic class which handles a menu */
public abstract class MenuState extends GameState{

	/** Create a new empty menu */
	public MenuState(){
		super(false);
	}

	@Override
	public void tick(Game game, double dt){
	}

	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}

	@Override
	public void mouseAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}

	@Override
	public void mouseMove(Game game, double x, double y){
	}

	@Override
	public void mouseWheelMove(Game game, double amount){
	}

	@Override
	public void renderBackground(Game game, Renderer r){
	}

	@Override
	public void render(Game game, Renderer r){
	}

	@Override
	public void renderHud(Game game, Renderer r){
	}
	
}
