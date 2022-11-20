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
import zusass.menu.pause.comp.PauseHubButton;
import zusass.menu.pause.comp.PauseMainMenuButton;
import zusass.menu.pause.comp.PauseSaveButton;
import zusass.menu.pause.comp.PauseQuitButton;
import zusass.menu.pause.comp.PauseReturnButton;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/** The {@link Menu} which displays when the game is paused */
public class PauseMenu extends Menu{
	
	/** See object docs */
	private PauseReturnButton returnButton;
	/** See object docs */
	private PauseMainMenuButton mainMenuButton;
	/** See object docs */
	private PauseQuitButton quitButton;
	/** See object docs */
	private PauseSaveButton saveButton;
	/** See object docs */
	private PauseHubButton hubButton;
	
	/**
	 * Make a new pause menu
	 * 
	 * @param zgame The game which will use this pause menu
	 */
	public PauseMenu(ZusassGame zgame){
		super(0, 0, 350, 500, false);
		this.center(zgame.getWindow());
		this.setBorder(new ZColor(0, 0, 0, 0));
		this.setFill(new ZColor(.5, 0, 0, .5));

		ZusassMenuText title = new ZusassMenuText(0, 20, 330, 120, "Pause", zgame, true);
		title.setFontSize(100);
		title.setFontColor(new ZColor(0));
		title.setFill(new ZColor(.5, .2, .2));
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
	}
	
	@Override
	public boolean addThing(MenuThing thing){
		// Center all things added to this menu after they are added
		boolean success = super.addThing(thing);
		if(success) thing.centerHorizontal();
		return success;
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(press) return;
		
		// On releasing escape, exit the pause menu
		if(button == GLFW_KEY_ESCAPE) this.exitMenu((ZusassGame)game);
	}

	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		// Fade the background
		r.setColor(.1, .1, .1, .3);
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
		play.removeTopMenu();
	}
	
	public void save(ZusassGame zgame){
		// if(shift) this.enterMainMenu(zgame);
		// else this.enterHub(zgame);
		
		ZusassData d = zgame.getData();
		d.checkAutoSave(zgame);
		zgame.saveLoadedGame();
	}
	
}
