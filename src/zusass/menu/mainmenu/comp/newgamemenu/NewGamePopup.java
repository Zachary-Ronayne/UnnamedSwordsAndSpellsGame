package zusass.menu.mainmenu.comp.newgamemenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zusass.ZUSASSData;
import zusass.menu.comp.ZUSASSButton;
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
		textBox.centerHorizontal();
		
		ZUSASSButton create = new CreateGameButton(textBox, game);
		this.addThing(create);
		create.centerHorizontal();
		create.moveX(-(create.getWidth() * .5 + 10));
		
		ZUSASSButton cancel = new CancelGameButton(game);
		this.addThing(cancel);
		cancel.centerHorizontal();
		cancel.moveX(cancel.getWidth() * .5 + 10);
		
		ZUSSASSMenuText title = new ZUSSASSMenuText(100, 100, 600, 100, "Create new Game", game);
		this.addThing(title);
		title.setFill(new ZColor(.5, .8));
		title.setBorder(new ZColor(0, 0));
		title.setFontSize(50);
		title.centerText();
		title.centerHorizontal();
	}
	
	@Override
	public void keyAction(Game<ZUSASSData> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		if(button == GLFW_KEY_ESCAPE) game.getCurrentState().removeTopMenu();
	}
	
}
