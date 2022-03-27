package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;

/** The {@link MenuButton} in the main menu for continuing the last opened game */
public class ContinueGameButton extends MenuButton{
	
	/** Create the {@link ContinueGameButton} */
	public ContinueGameButton(){
		super(50, 250, 500, 150);
		this.setBorder(new ZColor(.6));
		this.setFill(new ZColor(.5));
		this.setBorderWidth(2);
	}
	
	@Override
	public void click(Game game){
		game.enterPlayState();
	}
}
