package zusass.menu.settings;

import zusass.ZusassGame;
import zusass.menu.ZusassMenu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The starting point for settings menus, mainly for navigation between menus */
public class BaseSettingsMenu extends ZusassMenu{
	
	/** The menu using this sub settings menu */
	private final SettingsMenu settingsMenu;
	
	/** The button used by this menu to confirm settings, can be null if not used */
	private final SettingsConfirmButton confirmButton;
	
	public BaseSettingsMenu(String title, SettingsMenu settingsMenu, boolean addConfirmButton){
		super(title);
		this.settingsMenu = settingsMenu;
		
		var backButton = new SettingsBackButton(){
			@Override
			public void click(){
				super.click();
				handleGoBackInput();
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
	public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) this.handleGoBackInput();
	}
	
	/** Tell this menu to go back to its previous state */
	private void handleGoBackInput(){
		// For now just go back, probably should add a warning here if there are unsaved changes to settings
		this.goBack();
	}
	
	/**
	 * Called when a user action causes this menu to need to go back to the previous menu. By default, goes one menu back, can override for custom behavior
	 */
	public void goBack(){
		ZusassGame.get().getCurrentState().setMenu(new SettingsMenu(this.getSettingsMenu().getGoBack()));
	}
	
	/** @return See {@link #confirmButton} */
	public SettingsConfirmButton getConfirmButton(){
		return this.confirmButton;
	}
}
