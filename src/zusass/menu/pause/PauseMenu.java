package zusass.menu.pause;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zgame.menu.Menu;
import zgame.menu.MenuThing;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.MainPlay;
import zusass.menu.comp.ZusassMenuText;
import zusass.menu.pause.comp.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

/** The {@link Menu} which displays when the game is paused */
public class PauseMenu extends Menu{
	
	/** See {@link PauseReturnButton} */
	private final PauseReturnButton returnButton;
	/** See {@link PauseMainMenuButton} */
	private final PauseMainMenuButton mainMenuButton;
	/** See {@link PauseQuitButton} */
	private final PauseQuitButton quitButton;
	/** See {@link PauseSaveButton} */
	private final PauseSaveButton saveButton;
	/** See {@link PauseHubButton} */
	private final PauseHubButton hubButton;
	/** See {@link PauseSettingsButton} */
	private final PauseSettingsButton settingsButton;
	
	/**
	 * Make a new pause menu
	 *
	 * @param zgame The game which will use this pause menu
	 */
	public PauseMenu(ZusassGame zgame){
		super(0, 0, 350, 520, false);
		this.setDefaultDestroyRemove(false);
		
		this.center(zgame.getWindow());
		this.setBorder(new ZColor(.2, 0, 0, .5));
		this.setBorderWidth(8);
		this.setFill(new ZColor(.5, 0, 0, .5));
		
		ZusassMenuText title = new ZusassMenuText(0, 20, 330, 120, "Pause", zgame, true);
		title.setFontSize(100);
		title.setFontColor(new ZColor(0));
		title.setFill(new ZColor(.5, .2, .2));
		title.setBorder(new ZColor(.2, 0, 0));
		title.setBorderWidth(2);
		title.centerText();
		
		this.addThing(title);
		
		this.returnButton = new PauseReturnButton(this, zgame);
		this.addThing(this.returnButton);
		
		this.mainMenuButton = new PauseMainMenuButton(this, zgame);
		this.addThing(this.mainMenuButton);
		
		this.quitButton = new PauseQuitButton(this, zgame);
		this.addThing(this.quitButton);
		
		this.saveButton = new PauseSaveButton(this, zgame);
		this.addThing(this.saveButton);
		
		this.hubButton = new PauseHubButton(this, zgame);
		this.addThing(this.hubButton);
		
		this.settingsButton = new PauseSettingsButton(this, zgame);
		this.addThing(this.settingsButton);
	}
	
	@Override
	public boolean addThing(MenuThing thing){
		// Center all things added to this menu after they are added
		boolean success = super.addThing(thing);
		if(success) thing.centerHorizontal();
		return success;
	}
	
	@Override
	public void keyActionFocused(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(game, button, press, shift, alt, ctrl);
		if(press) return;
		
		// On releasing escape or tab, exit the pause menu
		if(button == GLFW_KEY_ESCAPE || button == GLFW_KEY_TAB) this.exitMenu((ZusassGame)game);
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		// Fade the background
		r.setColor(.1, .1, .1, .5);
		r.fill();
		// Then draw the menu
		super.render(game, r, bounds);
	}
	
	/**
	 * Exit out of this pause menu and go back to the game
	 *
	 * @param zgame The game using this button
	 */
	public void exitMenu(ZusassGame zgame){
		MainPlay play = zgame.getPlayState();
		play.fullUnpause();
		play.removeTopMenu(zgame, false);
	}
	
	public void save(ZusassGame zgame){
		ZusassData d = zgame.getData();
		d.checkAutoSave(zgame);
		zgame.saveLoadedGame();
	}
	
}
