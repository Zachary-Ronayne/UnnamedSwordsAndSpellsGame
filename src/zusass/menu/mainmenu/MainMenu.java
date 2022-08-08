package zusass.menu.mainmenu;

import zgame.core.Game;
import zusass.ZUSASSData;
import zusass.menu.ZUSASSMenu;
import zusass.menu.mainmenu.comp.ContinueGameButton;
import zusass.menu.mainmenu.comp.ExitButton;
import zusass.menu.mainmenu.comp.LoadGameButton;
import zusass.menu.mainmenu.comp.NewGameButton;

/** The {@link ZUSASSMenu} for the main menu of the game */
public class MainMenu extends ZUSASSMenu{
	
	/** Initialize the {@link MainMenu} */
	public MainMenu(Game<ZUSASSData> game){
		super("ZUSASS");
		this.addThing(new ContinueGameButton(game));
		this.addThing(new LoadGameButton(game));
		this.addThing(new NewGameButton(game));
		this.addThing(new ExitButton(game));
		// TODO hide continue / load buttons if there are no files to load
	}
	
}
