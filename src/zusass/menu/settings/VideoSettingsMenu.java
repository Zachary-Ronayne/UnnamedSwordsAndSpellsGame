package zusass.menu.settings;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The menu used for displaying specific settings related to video options */
public class VideoSettingsMenu extends ZusassMenu{
	
	// TODO abstract out this kind of thing so that a new menu and button isn't needed for every new sub menu type?
	
	/** The menu using this sub settings menu */
	private final SettingsMenu settingsMenu;
	
	/**
	 * Init the new menu
	 * @param zgame The game using the menu
	 */
	public VideoSettingsMenu(ZusassGame zgame, SettingsMenu settingsMenu){
		super(zgame, "Video Settings");
		this.getTitleThing().setFontSize(60);
		
		this.settingsMenu = settingsMenu;
		
		// TODO Abstract this out to be in all settings menus
		var backButton = new SettingsBackButton(zgame){
			@Override
			public void click(Game game){
				super.click(game);
				handleGoBack((ZusassGame)game);
			}
		};
		this.addThing(backButton);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		// TODO abstract this out to all settings menus
		if(button == GLFW_KEY_ESCAPE) this.handleGoBack((ZusassGame)game);
	}
	
	/** Tell this menu to go back to its previous state */
	public void handleGoBack(ZusassGame zgame){
		zgame.getCurrentState().setMenu(new SettingsMenu(zgame, this.settingsMenu.getGoBack()));
	}
}
