package zusass.menu.mainmenu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.Menu;
import zusass.menu.mainmenu.comp.ContinueGameButton;
import zusass.menu.mainmenu.comp.ExitButton;
import zusass.menu.mainmenu.comp.NewGameButton;

/** The {@link Menu} for the main menu of the game */
public class MainMenu extends Menu{
	
	/** Initialize the {@link MainMenu} */
	public MainMenu(){
		this.addThing(new NewGameButton());
		this.addThing(new ContinueGameButton());
		this.addThing(new ExitButton());
	}
	
	@Override
	public void renderBackground(Game game, Renderer r){
		super.renderBackground(game, r);
		r.setColor(0.2, 0.2, 0.2);
		r.fill();
	}
	
}
