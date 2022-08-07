package zgame.core.state;

import zgame.core.Game;
import zgame.core.graphics.Renderer;

/** An implementation of {@link GameState} which does nothing */
public class DefaultState<D> extends GameState<D>{
	
	/** Create an empty {@link DefaultState} */
	public DefaultState(){
		super();
	}
	
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
	}
	
	@Override
	public void tick(Game<D> game, double dt){
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
