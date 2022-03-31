package zusass.game;

import zgame.core.Game;
import zgame.core.state.PlayState;
import zusass.menu.mainmenu.MainMenuState;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the ZUSASS game
 * I initially called this ZUSASSPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	/** Initialize the main play state for the ZUSASS game */
	public MainPlay(){
		this.setCurrentRoom(new Hub());
	}

	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(button == GLFW_KEY_ESCAPE) game.setCurrentState(new MainMenuState(game));
	}

}
