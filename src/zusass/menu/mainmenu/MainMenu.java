package zusass.menu.mainmenu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zusass.ZUSASSData;
import zusass.menu.mainmenu.comp.ContinueGameButton;
import zusass.menu.mainmenu.comp.ExitButton;
import zusass.menu.mainmenu.comp.NewGameButton;

/** The {@link Menu} for the main menu of the game */
public class MainMenu extends Menu<ZUSASSData>{
	
	/** Initialize the {@link MainMenu} */
	public MainMenu(Game<ZUSASSData> game){
		this.addThing(new NewGameButton(game));
		this.addThing(new ContinueGameButton(game));
		this.addThing(new ExitButton(game));
	}
	
	@Override
	public void renderBackground(Game<ZUSASSData> game, Renderer r){
		super.renderBackground(game, r);
		
		// Background color
		r.setColor(0.2, 0.2, 0.2);
		r.fill();
		
		// Title
		r.setColor(new ZColor(.8));
		r.setFont(game.getFont("zfont"));
		r.setFontSize(100);
		r.drawText(600, 110, "ZUSASS");
	}
	
}
