package zusass.menu.settings;

import zgame.core.Game;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

import java.util.function.Consumer;

/** The root menu for displaying settings */
public class SettingsMenu extends BaseSettingsMenu{
	
	/** A function to run when exiting this menu */
	private final Consumer<ZusassGame> goBack;
	
	/**
	 * Create the basic settings menu
	 * @param zgame The game using the settings
	 * @param goBack See {@link #goBack}
	 */
	public SettingsMenu(ZusassGame zgame, Consumer<ZusassGame> goBack){
		super("Settings", zgame, null, false);
		this.goBack = goBack;
		
		var videoSettingsButton = new ZusassButton(10, 50, 500, 100, "Video Settings", zgame){
			@Override
			public void click(Game game){
				super.click(game);
				handleVideoSettingsClick((ZusassGame)game);
			}
		};
		this.addThing(videoSettingsButton);
	}
	
	@Override
	public SettingsMenu getSettingsMenu(){
		return this;
	}
	
	/**
	 * Called when the button for going to video settings is clicked
	 * @param zgame The game using the button
	 */
	public void handleVideoSettingsClick(ZusassGame zgame){
		zgame.getCurrentState().setMenu(new VideoSettingsMenu(zgame, this));
	}
	
	/** @return See {@link #goBack} */
	public Consumer<ZusassGame> getGoBack(){
		return this.goBack;
	}
	
	@Override
	public void goBack(ZusassGame zgame){
		this.getGoBack().accept(zgame);
	}
}