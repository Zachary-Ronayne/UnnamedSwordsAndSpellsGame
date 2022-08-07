package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.PlayState;
import zusass.ZUSASSData;
import zusass.menu.mainmenu.MainMenuState;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the ZUSASS game
 * I initially called this ZUSASSPlay, but I um... changed it
 */
public class MainPlay extends PlayState<ZUSASSData>{
	
	/**
	 * Initialize the main play state for the ZUSASS game
	 * 
	 * @param game The {@link Game} using this state
	 */
	public MainPlay(Game<ZUSASSData> game){
		this.enterHub(game);
	}
	
	/**
	 * Set the current room of the game to the main hub
	 * 
	 * @param game The {@link Game} using this state
	 */
	public void enterHub(Game<ZUSASSData> game){
		this.setCurrentRoom(new Hub(game));
	}
	
	/**
	 * Set the current state of the game to the main menu
	 * 
	 * @param game The {@link Game} using this state
	 */
	public void enterMainMenu(Game<ZUSASSData> game){
		game.setCurrentState(new MainMenuState(game));
	}
	
	@Override
	public void keyAction(Game<ZUSASSData> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE){
			if(shift) this.enterMainMenu(game);
			else this.enterHub(game);
		}
		else if(button == GLFW_KEY_S && ctrl) game.saveGame("./saves/zusassSave");
		else if(button == GLFW_KEY_L && ctrl) game.loadGame("./saves/zusassSave");
	}
	
	@Override
	public void renderBackground(Game<ZUSASSData> game, Renderer r){
		// Draw a solid color for the background
		r.setColor(new ZColor(.05));
		r.drawRectangle(0, 0, game.getScreenWidth(), game.getScreenHeight());
		
		// Draw the rest of the background
		super.renderBackground(game, r);
	}
	
}
