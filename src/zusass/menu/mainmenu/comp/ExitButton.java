package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;

/** The {@link MenuButton} in the main menu for exiting the game */
public class ExitButton extends MainMenuButton{
	
	/** Create the {@link NewGameButton} */
	public ExitButton(){
		super(50, 500, "Exit");
		this.setFill(new ZColor(.7, .4, .4));
	}
	
	@Override
	public void click(){
		Game.get().stop();
	}
}
