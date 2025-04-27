package zusass.menu.pause;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect2D;
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
	 */
	public PauseMenu(){
		super(0, 0, 350, 520, false);
		this.setDefaultDestroyRemove(false);
		
		this.center(ZusassGame.window());
		this.setBorder(new ZColor(.2, 0, 0, .5));
		this.setBorderWidth(8);
		this.setFill(new ZColor(.5, 0, 0, .5));
		
		ZusassMenuText title = new ZusassMenuText(0, 20, 330, 120, "Pause", true);
		title.setFontSize(100);
		title.setFontColor(new ZColor(0));
		title.setFill(new ZColor(.5, .2, .2));
		title.setBorder(new ZColor(.2, 0, 0));
		title.setBorderWidth(2);
		title.centerText();
		
		this.addThing(title);
		
		this.returnButton = new PauseReturnButton(this);
		this.addThing(this.returnButton);
		
		this.mainMenuButton = new PauseMainMenuButton(this);
		this.addThing(this.mainMenuButton);
		
		this.quitButton = new PauseQuitButton(this);
		this.addThing(this.quitButton);
		
		this.saveButton = new PauseSaveButton(this);
		this.addThing(this.saveButton);
		
		this.hubButton = new PauseHubButton(this);
		this.addThing(this.hubButton);
		
		this.settingsButton = new PauseSettingsButton(this);
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
	public void keyActionFocused(int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(button, press, shift, alt, ctrl);
		if(press) return;
		
		// On releasing escape or tab, exit the pause menu
		if(button == GLFW_KEY_ESCAPE || button == GLFW_KEY_TAB) this.exitMenu();
	}
	
	@Override
	public void render(Renderer r, ZRect2D bounds){
		// Fade the background
		r.setColor(.1, .1, .1, .5);
		r.fill();
		// Then draw the menu
		super.render(r, bounds);
	}
	
	/**
	 * Exit out of this pause menu and go back to the game
	 */
	public void exitMenu(){
		MainPlay play = ZusassGame.get().getPlayState();
		play.fullUnpause();
		play.removeTopMenu(false);
	}
	
	public void save(){
		var zgame = ZusassGame.get();
		ZusassData d = zgame.getData();
		d.checkAutoSave();
		zgame.saveLoadedGame();
	}
	
}
