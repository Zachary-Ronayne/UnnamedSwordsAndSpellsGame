package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.PlayState;
import zusass.ZUSASSData;
import zusass.ZUSASSGame;
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
			ZUSASSData d = game.getData();
			d.checkAutoSave(game);
			if(shift) this.enterMainMenu(game);
			else this.enterHub(game);
		}
		else if(button == GLFW_KEY_S && ctrl){
			// TODO make a pause menu with a save button
			((ZUSASSGame)game).saveLoadedGame();
		}
	}
	
	@Override
	public void renderBackground(Game<ZUSASSData> game, Renderer r){
		super.renderBackground(game, r);

		// Draw a solid color for the background
		r.setColor(new ZColor(.05));
		r.drawRectangle(0, 0, game.getScreenWidth(), game.getScreenHeight());
		
		// Draw the rest of the background
		super.renderBackground(game, r);
	}
	
}
