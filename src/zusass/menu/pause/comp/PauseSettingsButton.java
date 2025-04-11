package zusass.menu.pause.comp;

import zgame.core.Game;
import zgame.menu.MenuButton;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;
import zusass.menu.settings.SettingsMenu;

/** The {@link MenuButton} in the pause menu for going to the settings menu */
public class PauseSettingsButton extends PauseMenuButton{
	
	/** Create the {@link PauseSettingsButton} */
	public PauseSettingsButton(PauseMenu menu, ZusassGame zgame){
		super(0, 450, "Settings", menu, zgame);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		zgame.getCurrentState().setMenu(zgame, new SettingsMenu(zgame, z -> {
			var p = z.getPlayState();
			p.removeTopMenu(z);
			p.openPauseMenu(z);
		}));
	}
}
