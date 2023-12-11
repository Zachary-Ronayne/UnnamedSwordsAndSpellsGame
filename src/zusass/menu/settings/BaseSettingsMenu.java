package zusass.menu.settings;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The starting point for settings menus, mainly for navigation between menus */
public class BaseSettingsMenu extends ZusassMenu{
	
	/** The menu using this sub settings menu */
	private final SettingsMenu settingsMenu;
	
	public BaseSettingsMenu(String title, ZusassGame zgame, SettingsMenu settingsMenu){
		super(zgame, title);
		this.settingsMenu = settingsMenu;
		
		var backButton = new SettingsBackButton(zgame){
			@Override
			public void click(Game game){
				super.click(game);
				handleGoBackInput((ZusassGame)game);
			}
		};
		this.addThing(backButton);
	}
	
	/** @return See {@link #settingsMenu} */
	public SettingsMenu getSettingsMenu(){
		return this.settingsMenu;
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) this.handleGoBackInput((ZusassGame)game);
	}
	
	/** Tell this menu to go back to its previous state */
	private void handleGoBackInput(ZusassGame zgame){
		// TODO make a better way of saving settings that makes more sense, also account for if it should be global or local settings
		zgame.saveGlobalSettings();
		
		this.goBack(zgame);
	}
	
	/**
	 * Called when a user action causes this menu to need to go back to the previous menu. By default, goes one menu back, can override for custom behavior
	 * @param zgame The game where it was told to go back in
	 */
	public void goBack(ZusassGame zgame){
		zgame.getCurrentState().setMenu(new SettingsMenu(zgame, this.getSettingsMenu().getGoBack()));
	}
}
