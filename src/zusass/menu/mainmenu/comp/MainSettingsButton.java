package zusass.menu.mainmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuButton;
import zusass.ZusassGame;
import zusass.menu.mainmenu.MainMenu;
import zusass.menu.settings.SettingsMenu;

/** The {@link MenuButton} in the main menu for going to the settings menu */
public class MainSettingsButton extends MainMenuButton{
	
	/** Create the {@link MainSettingsButton} */
	public MainSettingsButton(ZusassGame zgame){
		super(600, 500, "Settings", zgame);
		this.setWidth(200);
		this.centerText();
		this.setFill(new ZColor(.5));
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		zgame.getCurrentState().setMenu(zgame, new SettingsMenu(zgame, z -> z.getCurrentState().setMenu(z, new MainMenu(z))));
	}
}
