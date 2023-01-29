package zusass.menu.savesmenu;

import static org.lwjgl.glfw.GLFW.*;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect;
import zgame.menu.MenuHolder;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import zusass.menu.savesmenu.comp.LoadSaveButtonList;
import zusass.menu.savesmenu.comp.SavesBackButton;
import zusass.menu.savesmenu.comp.SavesDeleteButton;
import zusass.menu.savesmenu.comp.SavesLoadButton;
import zusass.menu.savesmenu.comp.SavesMenuScroller;
import zusass.menu.savesmenu.comp.SavesRefreshButton;

/** A {@link ZusassMenu} for managing game saves */
public class SavesMenu extends ZusassMenu{
	
	/** The number of seconds to display {@link #messageText} */
	public static final double MESSAGE_TIME = 4;
	
	/** The list of buttons used to load and save files */
	private final LoadSaveButtonList loadButtons;
	
	/** The scroller that allows the list of buttons to move up and down */
	private final SavesMenuScroller scroller;
	
	/** An object to hold the buttons that will be hidden when no file is selected */
	private final MenuHolder extraButtonHolder;
	
	/** Text to display for a temporary amount of time */
	private String messageText;
	/** The amount of time remaining to display {@link #messageText} */
	private double messageTimer;
	
	/**
	 * Create a new blank {@link SavesMenu}
	 *
	 * @param zgame The game that uses this menu
	 */
	public SavesMenu(ZusassGame zgame){
		super(zgame, "Saves");
		this.setTitleX(50);
		
		this.addThing(new SavesBackButton(this, zgame));
		this.addThing(new SavesRefreshButton(this, zgame));
		
		this.scroller = new SavesMenuScroller(zgame);
		this.loadButtons = new LoadSaveButtonList(this, zgame);
		this.scroller.setMovingThing(this.loadButtons);
		this.addThing(this.scroller);
		this.addThing(this.loadButtons);
		
		this.extraButtonHolder = new MenuHolder();
		this.extraButtonHolder.addThing(new SavesLoadButton(this, zgame));
		this.extraButtonHolder.addThing(new SavesDeleteButton(this, zgame));
		
		this.messageText = "";
		this.messageTimer = 0;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.extraButtonHolder.destroy();
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		if(this.messageTimer > 0) this.messageTimer -= dt;
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		super.render(game, r, bounds);
		if(this.messageTimer < 0) return;
		
		r.setFont(game.getFont("zfont"));
		r.setFontSize(25);
		r.setColor(new ZColor(.8));
		r.drawText(10, 700, this.messageText);
	}
	
	@Override
	public void keyAction(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		ZusassGame zgame = (ZusassGame)game;
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(!press && button == GLFW_KEY_F5) this.getLoadButtons().populate(zgame);
	}
	
	/** @return See {@link #scroller} */
	public SavesMenuScroller getScroller(){
		return this.scroller;
	}
	
	/** @return See {@link #loadButtons} */
	public LoadSaveButtonList getLoadButtons(){
		return this.loadButtons;
	}
	
	/**
	 * Show or hide the extra buttons for loading and deleting files
	 *
	 * @param show true to show the buttons, false to hide
	 */
	public void showExtraButtons(boolean show){
		if(show) this.addThing(this.extraButtonHolder);
		else this.removeThing(this.extraButtonHolder, false);
	}
	
	/**
	 * Display a temporary message to the menu
	 *
	 * @param message The message text to display
	 */
	public void showMessage(String message){
		this.messageText = message;
		this.messageTimer = MESSAGE_TIME;
	}
	
}
