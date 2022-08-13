package zusass.menu.mainmenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.ZUSASSMenu;
import zusass.menu.mainmenu.comp.ContinueGameButton;
import zusass.menu.mainmenu.comp.ExitButton;
import zusass.menu.mainmenu.comp.LoadGameButton;
import zusass.menu.mainmenu.comp.NewGameButton;
import zusass.utils.ZUSASSConfig;

/** The {@link ZUSASSMenu} for the main menu of the game */
public class MainMenu extends ZUSASSMenu{

	/** Initialize the {@link MainMenu} */
	public MainMenu(Game<ZUSASSData> game){
		super("ZUSASS");
		
		this.initButtons(game);
	}

	/**
	 * Initialize this menu to a default state
	 * @param game The game to base the buttons on
	 */
	public void initButtons(Game<ZUSASSData> game){
		// Only show the continue and load buttons if at least one valid save file exists
		String file = ZUSASSConfig.getMostRecentSave();
		if(file != null){
			this.addThing(new ContinueGameButton(game));
			this.addThing(new LoadGameButton(game));
		}
		this.addThing(new NewGameButton(game));
		this.addThing(new ExitButton(game));
	}

	@Override
	public void keyAction(Game<ZUSASSData> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(!press && button == GLFW_KEY_F5) this.initButtons(game);
	}
	
}
