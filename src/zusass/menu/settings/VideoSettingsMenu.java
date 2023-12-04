package zusass.menu.settings;

import zgame.core.Game;
import zgame.settings.BooleanTypeSetting;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import zusass.menu.comp.ToggleButton;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The menu used for displaying specific settings related to video options */
public class VideoSettingsMenu extends ZusassMenu{
	
	// TODO do this in a better way, not doing string comparisons
	/** The text to display for vsync being enabled */
	public static final String VSYNC_ENABLED_TEXT = "Vsync Enabled";
	/** The text to display for vsync being disabled */
	public static final String VSYNC_DISABLED_TEXT = "Vsync Disabled";
	
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
		
		// TODO make some kind of abstract way of making these buttons based on a setting
		var vsyncValues = new ArrayList<String>();
		vsyncValues.add(VSYNC_ENABLED_TEXT);
		vsyncValues.add(VSYNC_DISABLED_TEXT);
		var vsyncButton = new ToggleButton(10, 50, 350, 100, vsyncValues, zgame){
			@Override
			public void setText(String text){
				super.setText(text);
				zgame.set(BooleanTypeSetting.V_SYNC, text.equals(VSYNC_ENABLED_TEXT), false);
			}
		};
		// TODO make this in a better way instead of based on indexes
		vsyncButton.setSelectedIndex(zgame.get(BooleanTypeSetting.V_SYNC) ? 0 : 1);
		vsyncButton.centerText();
		this.addThing(vsyncButton);
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
