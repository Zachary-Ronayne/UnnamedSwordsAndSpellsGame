package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zusass.menu.savesmenu.SavesMenuState;

/** A Button used to load a saved game, i.e. open the saves menu */
public class LoadGameButton extends MainMenuButton{
	
	public LoadGameButton(){
		super(50, 200, "Load Game");
		this.setFill(new ZColor(.9));
	}
	
	@Override
	public void click(){
		super.click();
		Game.get().setCurrentState(new SavesMenuState());
	}
	
}
