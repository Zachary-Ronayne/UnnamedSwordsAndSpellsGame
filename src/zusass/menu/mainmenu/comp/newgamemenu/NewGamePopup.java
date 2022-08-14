package zusass.menu.mainmenu.comp.newgamemenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSSASSMenuText;
import zusass.menu.mainmenu.comp.NewGameButton;

/** A {@link Menu} used to create a new game */
public class NewGamePopup extends Menu<ZUSASSData>{
	
	/**
	 * Initialize the {@link NewGamePopup}
	 * 
	 * @param button The {@link NewGameButton} used by this menu
	 * @param game The ZUSASSGame used by this thing
	 */
	public NewGamePopup(NewGameButton button, Game<ZUSASSData> game){
		super(0, 0);
		this.setWidth(game.getScreenWidth());
		this.setHeight(game.getScreenHeight());
		this.setFill(new ZColor(0, .7));
		this.setBorder(new ZColor(0, 0));
		
		NewGameTextBox textBox = new NewGameTextBox(game);
		this.addThing(textBox);

		this.addThing(new CreateGameButton(textBox, game));
		this.addThing(new CancelGameButton(game));
		ZUSSASSMenuText title = new ZUSSASSMenuText(100, 20, 600, 100, "Create new Game", game);
		title.setFill(new ZColor(1, .5));
		title.setBorder(new ZColor(0, 0));
		this.addThing(title);
	}

	@Override
	public void keyAction(Game<ZUSASSData> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) game.getCurrentState().removeTopMenu();
	}
	
}
