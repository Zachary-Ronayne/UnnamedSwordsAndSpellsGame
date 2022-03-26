package zusass;

import zgame.core.Game;
import zgame.core.window.GameWindow;
import zusass.game.MainPlay;
import zusass.menu.mainmenu.MainMenuState;

/**
 * The main class for the ZUSASS Game.
 * ZUSASS is an acronym
 * Zac's
 * Untitled
 * Swords
 * and
 * Spells
 * Sandbox
 */
public class ZUSASSGame extends Game{
	
	/** Create the only instance of ZUSASSGame from this class. This constructor will place the game in the main menu */
	private ZUSASSGame(){
		super();
		// Window and performance settings
		this.setTps(100);
		this.setCurrentState(new MainMenuState());
		GameWindow w = this.getWindow();
		w.setUseVsync(true);
		w.center();

		// Play state
		this.setPlayState(new MainPlay());
	}

	/** The only instance of {@link ZUSASSGame} which can exist */
	private static ZUSASSGame game = null;

	public static void main(String[] args){
		init();
		game.start();
	}
	
	/** Initialize the object {@link #game} */
	public static void init(){
		if(game != null) return;
		game = new ZUSASSGame();
	}
}
