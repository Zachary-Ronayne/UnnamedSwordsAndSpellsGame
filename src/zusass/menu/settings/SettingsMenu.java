package zusass.menu.settings;

import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The root menu for displaying settings */
public class SettingsMenu extends BaseSettingsMenu{
	
	/** A function to run when exiting this menu */
	private final Runnable goBack;
	
	/**
	 * Create the basic settings menu
	 *
	 * @param goBack See {@link #goBack}
	 */
	public SettingsMenu(Runnable goBack){
		super("Settings", null, false);
		this.goBack = goBack;
		
		var videoSettingsButton = new ZusassButton(10, 50, 500, 100, "Video Settings"){
			@Override
			public void click(){
				super.click();
				handleVideoSettingsClick();
			}
		};
		this.addThing(videoSettingsButton);
		
		var allSettingsButton = new ZusassButton(10, 160, 500, 100, "All Settings"){
			@Override
			public void click(){
				super.click();
				handleAllSettingsClick();
			}
		};
		this.addThing(allSettingsButton);
	}
	
	@Override
	public SettingsMenu getSettingsMenu(){
		return this;
	}
	
	/**
	 * Called when the button for going to video settings is clicked
	 */
	public void handleVideoSettingsClick(){
		ZusassGame.get().getCurrentState().setMenu(new VideoSettingsMenu(this));
	}
	
	/**
	 * Called when the button for going to all settings is clicked
	 */
	public void handleAllSettingsClick(){
		ZusassGame.get().getCurrentState().setMenu(new AllSettingsMenu(this));
	}
	
	/** @return See {@link #goBack} */
	public Runnable getGoBack(){
		return this.goBack;
	}
	
	@Override
	public void goBack(){
		this.getGoBack().run();
	}
}