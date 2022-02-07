package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** An implementation of {@link GameState} which does nothing */
public class DefaultState extends GameState{
	
	/** Create an empty {@link DefaultState} */
	public DefaultState(){
		super();
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	@Override
	public void tick(Game game, double dt){
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
