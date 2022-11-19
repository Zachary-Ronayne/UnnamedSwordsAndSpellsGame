package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.state.MenuNode;
import zgame.core.state.PlayState;
import zusass.ZusassGame;
import zusass.menu.mainmenu.MainMenuState;
import zusass.menu.pause.PauseMenu;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The main {@link PlayState} used by the Zusass game
 * I initially called this ZusassPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	/**
	 * Initialize the main play state for the Zusass game
	 * 
	 * @param game The {@link Game} using this state
	 */
	public MainPlay(ZusassGame zgame){
		this.enterHub(zgame);
	}
	
	/**
	 * Set the current room of the game to the main hub
	 * 
	 * @param game The {@link Game} using this state
	 */
	public void enterHub(ZusassGame zgame){
		this.setCurrentRoom(new Hub(zgame));
	}
	
	/**
	 * Set the current state of the game to the main menu
	 * 
	 * @param zgame The {@link Game} using this state
	 */
	public void enterMainMenu(ZusassGame zgame){
		zgame.setCurrentState(new MainMenuState(zgame));
	}
	
	@Override
	public void playKeyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.playKeyAction(game, button, press, shift, alt, ctrl);
		if(press) return;

		// On releasing escape, enter the pause menu
		if(button == GLFW_KEY_ESCAPE){
			ZusassGame zgame = (ZusassGame)game;
			MenuNode pauseNode = MenuNode.withAll(new PauseMenu(zgame));
			zgame.getPlayState().fullPause();
			this.popupMenu(pauseNode);
		}
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
		super.renderBackground(game, r);
		
		// Draw a solid color for the background
		r.setColor(new ZColor(.05));
		r.drawRectangle(0, 0, game.getScreenWidth(), game.getScreenHeight());
		
		// Draw the rest of the background
		super.renderBackground(game, r);
	}
	
}
