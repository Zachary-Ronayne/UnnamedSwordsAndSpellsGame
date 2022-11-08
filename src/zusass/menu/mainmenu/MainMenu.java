package zusass.menu.mainmenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import zusass.menu.mainmenu.comp.ContinueGameButton;
import zusass.menu.mainmenu.comp.ExitButton;
import zusass.menu.mainmenu.comp.LoadGameButton;
import zusass.menu.mainmenu.comp.NewGameButton;
import zusass.utils.ZusassConfig;

/** The {@link ZusassMenu} for the main menu of the game */
public class MainMenu extends ZusassMenu{
	
	/**
	 * Initialize the {@link MainMenu}
	 * 
	 * @param game The Zusass game associated with this {@link MainMenu}
	 */
	public MainMenu(ZusassGame zgame){
		super(zgame, "ZUSASS");
		this.initButtons(zgame);
	}
	
	/**
	 * Initialize this menu to a default state
	 * 
	 * @param zgame The game to base the buttons on
	 */
	public void initButtons(ZusassGame zgame){
		// Only show the continue and load buttons if at least one valid save file exists
		String file = ZusassConfig.getMostRecentSave();
		if(file != null){
			this.addThing(new ContinueGameButton(zgame));
			this.addThing(new LoadGameButton(zgame));
		}
		this.addThing(new NewGameButton(zgame));
		this.addThing(new ExitButton(zgame));
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		ZusassGame zgame = (ZusassGame)game;
		super.keyAction(zgame, button, press, shift, alt, ctrl);
		if(!press && button == GLFW_KEY_F5) this.initButtons(zgame);
	}
	
}
