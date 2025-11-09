package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;

/** The {@link MenuButton} in the main menu for exiting the game */
public class ExitButton extends MainMenuButton{
	
	/** Create the {@link NewGameButton} */
	public ExitButton(){
		super(50, 500, "Exit");
		this.setFill(new ZColor(.9, .8, .8));
	}
	
	@Override
	public void click(){
		super.click();
		Game.get().stop();
	}
}
