package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.PlayState;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.menu.mainmenu.MainMenuState;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the Zusass game
 * I initially called this ZusassPlay, but I um... changed it
 */
public class MainPlay extends PlayState<ZusassData>{
	
	/**
	 * Initialize the main play state for the Zusass game
	 * 
	 * @param game The {@link Game} using this state
	 */
	public MainPlay(Game<ZusassData> game){
		this.enterHub(game);
	}
	
	/**
	 * Set the current room of the game to the main hub
	 * 
	 * @param game The {@link Game} using this state
	 */
	public void enterHub(Game<ZusassData> game){
		this.setCurrentRoom(new Hub(game));
	}
	
	/**
	 * Set the current state of the game to the main menu
	 * 
	 * @param game The {@link Game} using this state
	 */
	public void enterMainMenu(Game<ZusassData> game){
		game.setCurrentState(new MainMenuState(game));
	}
	
	@Override
	public void keyAction(Game<ZusassData> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE){
			ZusassData d = game.getData();
			d.checkAutoSave(game);
			if(shift) this.enterMainMenu(game);
			else this.enterHub(game);
		}
		else if(button == GLFW_KEY_S && ctrl){
			// TODO make a pause menu with a save button
			((ZusassGame)game).saveLoadedGame();
		}
	}
	
	@Override
	public void renderBackground(Game<ZusassData> game, Renderer r){
		super.renderBackground(game, r);
		
		// Draw a solid color for the background
		r.setColor(new ZColor(.05));
		r.drawRectangle(0, 0, game.getScreenWidth(), game.getScreenHeight());
		
		// Draw the rest of the background
		super.renderBackground(game, r);
	}
	
}
