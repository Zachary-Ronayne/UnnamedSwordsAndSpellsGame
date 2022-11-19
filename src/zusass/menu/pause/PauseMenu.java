package zusass.menu.pause;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.utils.ZRect;
import zgame.menu.Menu;
import zusass.ZusassGame;
import zusass.game.MainPlay;
import zusass.menu.comp.ZusassMenuText;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The {@link Menu} which displays when the game is paused */
public class PauseMenu extends Menu{
	// TODO actually pause the game
	
	// TODO abstract this out to be a generic pause menu object, then make the specific implementation for Zusass
	
	/**
	 * Make a new pause menu
	 * 
	 * @param zgame The game which will use this pause menu
	 */
	public PauseMenu(ZusassGame zgame){
		super();
		this.addThing(new ZusassMenuText(10, 10, 500, 200, "Pause", zgame));
	}

	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		r.setColor(.5, 0, .0, .5);
		r.drawRectangle(100, 100, 900, 600);
	}
	
	// ZusassData d = zgame.getData();
	// d.checkAutoSave(zgame);
	// if(shift) this.enterMainMenu(zgame);
	// else this.enterHub(zgame);
	// else if(button == GLFW_KEY_S && ctrl){
	// // TODO make a pause menu with a save button
	// zgame.saveLoadedGame();
	// }
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;

		// On releasing escape, exit the pause menu
		if(button == GLFW_KEY_ESCAPE){
			ZusassGame zgame = (ZusassGame)game;
			MainPlay play = zgame.getPlayState();
			play.fullUnpause();
			play.removeTopMenu();
		}
	}
}
