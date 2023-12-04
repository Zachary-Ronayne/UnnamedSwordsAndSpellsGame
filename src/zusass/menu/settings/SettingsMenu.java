package zusass.menu.settings;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import zusass.menu.comp.ZusassButton;

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
		
		var videoSettingsButton = new ZusassButton(10, 50, 500, 100, "Video Settings", zgame){
			@Override
			public void click(Game game){
				super.click(game);
				handleVideoSettingsClick((ZusassGame)game);
			}
		};
		this.addThing(videoSettingsButton);
		
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
	
	/**
	 * Called when the button for going to video settings is clicked
	 * @param zgame The game using the button
	 */
	public void handleVideoSettingsClick(ZusassGame zgame){
		zgame.getCurrentState().setMenu(new VideoSettingsMenu(zgame, this));
	}
	
	/** Tell this menu to go back to its previous state */
	public void handleGoBack(ZusassGame zgame){
		this.goBack.accept(zgame);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) this.handleGoBack((ZusassGame)game);
	}
	
	/** @return See {@link #goBack} */
	public Consumer<ZusassGame> getGoBack(){
		return this.goBack;
	}
}