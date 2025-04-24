package zusass.menu.settings;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The starting point for settings menus, mainly for navigation between menus */
public class BaseSettingsMenu extends ZusassMenu{
	
	/** The menu using this sub settings menu */
	private final SettingsMenu settingsMenu;
	
	/** The button used by this menu to confirm settings, can be null if not used */
	private final SettingsConfirmButton confirmButton;
	
	public BaseSettingsMenu(String title, ZusassGame zgame, SettingsMenu settingsMenu, boolean addConfirmButton){
		super(zgame, title);
		this.settingsMenu = settingsMenu;
		
		var backButton = new SettingsBackButton(){
			@Override
			public void click(Game game){
				super.click(game);
				handleGoBackInput((ZusassGame)game);
			}
		};
		this.addThing(backButton);
		
		if(addConfirmButton){
			this.confirmButton = new SettingsConfirmButton();
			this.addThing(confirmButton);
		}
		else confirmButton = null;
	}
	
	/** @return See {@link #settingsMenu} */
	public SettingsMenu getSettingsMenu(){
		return this.settingsMenu;
	}
	
	@Override
	public void keyActionFocused(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) this.handleGoBackInput((ZusassGame)game);
	}
	
	/** Tell this menu to go back to its previous state */
	private void handleGoBackInput(ZusassGame zgame){
		// For now just go back, probably should add a warning here if there are unsaved changes to settings
		
		this.goBack(zgame);
	}
	
	/**
	 * Called when a user action causes this menu to need to go back to the previous menu. By default, goes one menu back, can override for custom behavior
	 * @param zgame The game where it was told to go back in
	 */
	public void goBack(ZusassGame zgame){
		zgame.getCurrentState().setMenu(zgame, new SettingsMenu(zgame, this.getSettingsMenu().getGoBack()));
	}
	
	/** @return See {@link #confirmButton} */
	public SettingsConfirmButton getConfirmButton(){
		return this.confirmButton;
	}
}
