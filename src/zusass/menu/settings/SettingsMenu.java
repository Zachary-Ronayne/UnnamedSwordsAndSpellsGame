package zusass.menu.settings;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.game.MainPlay;
import zusass.menu.ZusassMenu;
import zusass.menu.mainmenu.MainMenuState;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The root menu for displaying settings */
public class SettingsMenu extends ZusassMenu{
	
	/** A function to run when exiting this menu */
	private final Consumer<ZusassGame> goBack;
	
	/**
	 * Create the basic settings menu
	 * @param zgame The game using the settings
	 * @param goBack See {@link #goBack}
	 */
	public SettingsMenu(ZusassGame zgame, Consumer<ZusassGame> goBack){
		super(zgame, "Settings");
		this.goBack = goBack;
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE){
			var zgame = (ZusassGame)game;
			this.goBack.accept(zgame);
		}
	}
	
}