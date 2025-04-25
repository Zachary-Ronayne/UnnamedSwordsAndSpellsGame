package zusass.menu.mainmenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zusass.menu.ZusassMenu;
import zusass.menu.mainmenu.comp.*;
import zusass.utils.ZusassConfig;

/** The {@link ZusassMenu} for the main menu of the game */
public class MainMenu extends ZusassMenu{
	
	/**
	 * Initialize the {@link MainMenu}
	 */
	public MainMenu(){
		super("ZUSASS");
		this.initButtons();
	}
	
	/**
	 * Initialize this menu to a default state
	 */
	public void initButtons(){
		// Only show the continue and load buttons if at least one valid save file exists
		String file = ZusassConfig.getMostRecentSave();
		if(file != null){
			this.addThing(new ContinueGameButton());
			this.addThing(new LoadGameButton());
		}
		this.addThing(new NewGameButton());
		this.addThing(new ExitButton());
		this.addThing(new MainSettingsButton());
	}
	
	@Override
	public void keyActionFocused(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(game, button, press, shift, alt, ctrl);
		if(!press && button == GLFW_KEY_F5) this.initButtons();
	}
	
}
