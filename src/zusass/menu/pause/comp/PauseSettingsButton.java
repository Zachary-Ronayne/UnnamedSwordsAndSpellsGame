package zusass.menu.pause.comp;

import zgame.menu.MenuButton;
import zusass.ZusassGame;
import zusass.menu.pause.PauseMenu;
import zusass.menu.settings.SettingsMenu;

/** The {@link MenuButton} in the pause menu for going to the settings menu */
public class PauseSettingsButton extends PauseMenuButton{
	
	/**
	 * Create the {@link PauseSettingsButton}
	 *
	 * @param menu The menu which uses this button
	 */
	public PauseSettingsButton(PauseMenu menu){
		super(0, 450, "Settings", menu);
	}
	
	@Override
	public void click(){
		ZusassGame.get().getCurrentState().setMenu(new SettingsMenu(() -> {
			var p = ZusassGame.get().getPlayState();
			p.removeTopMenu();
			p.openPauseMenu();
		}));
	}
}
