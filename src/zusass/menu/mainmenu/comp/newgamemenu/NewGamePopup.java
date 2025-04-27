package zusass.menu.mainmenu.comp.newgamemenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;
import zusass.menu.comp.ZusassMenuText;
import zusass.menu.mainmenu.comp.NewGameButton;

/** A {@link Menu} used to create a new game */
public class NewGamePopup extends Menu{
	
	/** The button used to create a new game */
	private final ZusassButton createButton;
	
	/** The text box for entering the level name */
	private final NewGameTextBox newGameTextBox;
	
	/** The text box for entering the seed */
	private final SeedTextBox seedTextBox;

	/**
	 * Initialize the {@link NewGamePopup}
	 * 
	 * @param button The {@link NewGameButton} used by this menu
	 */
	public NewGamePopup(NewGameButton button){
		super(0, 0);
		var window = ZusassGame.window();
		this.setWidth(window.getScreenWidth());
		this.setHeight(window.getScreenHeight());
		this.setFill(new ZColor(0, .7));
		this.setBorder(new ZColor(0, 0));
		
		this.newGameTextBox = new NewGameTextBox(this);
		this.addThing(this.newGameTextBox);
		this.newGameTextBox.centerHorizontal();
		
		var seedLabel = new ZusassMenuText(100, 410, 430, 35, "Leave blank for random seed");
		seedLabel.setFontSize(30);
		this.addThing(seedLabel);
		seedLabel.centerHorizontal();
		seedLabel.setFullColor(seedLabel.getFill().alpha(0.2));
		// TODO why does the font look transparent unless explicitly set here?
		seedLabel.setFontColor(new ZColor(0.5));
		seedLabel.setBorderWidth(0);
		seedLabel.centerText();
		seedLabel.setTextY(seedLabel.getTextY() - 6);
		
		this.seedTextBox = new SeedTextBox();
		this.addThing(this.seedTextBox);
		this.seedTextBox.centerHorizontal();
		
		this.createButton = new CreateGameButton(this);
		this.addThing(this.createButton);
		this.createButton.centerHorizontal();
		this.createButton.moveX(-(this.createButton.getWidth() * .5) - 20);
		this.createButton.disable();
		
		var cancelButton = new CancelGameButton();
		this.addThing(cancelButton);
		cancelButton.centerHorizontal();
		cancelButton.moveX(cancelButton.getWidth() * .5 + 20);
		
		ZusassMenuText title = new ZusassMenuText(100, 100, 600, 100, "Create new Game");
		this.addThing(title);
		title.setFill(new ZColor(.5, .8));
		title.setBorder(new ZColor(0, 0));
		title.setFontSize(50);
		title.centerText();
		title.centerHorizontal();
	}

	/**
	 * Update whether or not the create button should be visible
	 * @param text The current text of create button
	 */
	public void updateCreateVisible(String text){
		this.createButton.setDisabled(text == null || text.isBlank());
	}
	
	@Override
	public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) Game.get().getCurrentState().removeTopMenu();
		else if(button == GLFW_KEY_ENTER) createButton.click();
	}
	
	/** @return The text currently entered for the level name */
	public String getLevelNameText(){
		return this.newGameTextBox.getCurrentText();
	}
	
	/** @return The text currently entered for the new seed, can be null or empty for a random seed */
	public String getSeed(){
		return this.seedTextBox.getCurrentText();
	}
	
}
