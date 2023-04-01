package zusass.menu.mainmenu.comp.newgamemenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zgame.menu.MenuHolder;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;
import zusass.menu.comp.ZusassMenuText;
import zusass.menu.mainmenu.comp.NewGameButton;

/** A {@link Menu} used to create a new game */
public class NewGamePopup extends Menu{
	
	/** Holds the create button if it should be visible */
	private final MenuHolder createHolder;
	/** The button used to create a new game */
	private final ZusassButton createButton;

	/**
	 * Initialize the {@link NewGamePopup}
	 * 
	 * @param button The {@link NewGameButton} used by this menu
	 * @param zgame The Zusass game used by this thing
	 */
	public NewGamePopup(NewGameButton button, ZusassGame zgame){
		super(0, 0);
		this.setWidth(zgame.getScreenWidth());
		this.setHeight(zgame.getScreenHeight());
		this.setFill(new ZColor(0, .7));
		this.setBorder(new ZColor(0, 0));
		
		NewGameTextBox textBox = new NewGameTextBox(zgame, this);
		this.addThing(textBox);
		textBox.centerHorizontal();
		
		this.createHolder = new MenuHolder();
		this.addThing(this.createHolder);
		this.createButton = new CreateGameButton(textBox, zgame);
		this.createButton.centerHorizontal();
		this.createButton.moveX(-(this.createButton.getWidth() * .5) + 20);
		
		ZusassButton cancel = new CancelGameButton(zgame);
		this.addThing(cancel);
		cancel.centerHorizontal();
		cancel.moveX(cancel.getWidth() * .5 + 10);
		
		ZusassMenuText title = new ZusassMenuText(100, 100, 600, 100, "Create new Game", zgame);
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
		if(text == null || text.isBlank()) this.createHolder.removeThing(this.createButton, false);
		else this.createHolder.addThing(this.createButton);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) game.getCurrentState().removeTopMenu();
		else if(button == GLFW_KEY_ENTER) createButton.click(game);
	}
	
}
