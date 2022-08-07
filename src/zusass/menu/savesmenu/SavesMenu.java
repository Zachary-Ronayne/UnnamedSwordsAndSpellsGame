package zusass.menu.savesmenu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zusass.ZUSASSData;
import zusass.menu.ZUSASSMenu;
import zusass.menu.savesmenu.comp.LoadSaveButtonList;
import zusass.menu.savesmenu.comp.SavesBackButton;
import zusass.menu.savesmenu.comp.SavesMenuScroller;

/** A {@link ZUSASSMenu} for managing game saves */
public class SavesMenu extends ZUSASSMenu{

	/** The number of seconds to display {@link #messageText} */
	public static final double MESSAGE_TIME = 4;

	/** The list of buttons used to load and save files */
	LoadSaveButtonList loadButtons;

	/** The scroller that allows the list of buttons to move up and down */
	SavesMenuScroller scroller;

	/** Text to display for a temporary amount of time */
	private String messageText;
	/** The amount of time remaining to display {@link #messageText} */
	private double messageTimer;

	/**
	 * Create a new blank {@link SavesMenu}
	 * @param game The game that uses this menu
	 */
	public SavesMenu(Game<ZUSASSData> game){
		super("Saves");
		this.setTitleX(50);
		
		this.addThing(new SavesBackButton(game));
		
		this.scroller = new SavesMenuScroller();
		this.loadButtons = new LoadSaveButtonList(this, this.scroller, game);
		this.addThing(scroller);
		
		this.messageText = "";
		this.messageTimer = 0;
	}
	
	@Override
	public void tick(Game<ZUSASSData> game, double dt){
		super.tick(game, dt);
		if(this.messageTimer > 0) this.messageTimer -= dt;
	}

	@Override
	public void renderBackground(Game<ZUSASSData> game, Renderer r){
		super.renderBackground(game, r);
		if(this.messageTimer >= 0){
			r.setFont(game.getFont("zfont"));
			r.setFontSize(25);
			r.setColor(new ZColor(.8));
			r.drawText(10, 700, this.messageText);
		}
	}

	/**
	 * Display a temporary message to the menu
	 * @param message The message text to display
	 */
	public void showMessage(String message){
		this.messageText = message;
		this.messageTimer = MESSAGE_TIME;
	}
	
}

// TODO add a system to have menus display on top of others, have the bottom still render, but only the top one tick and take input, basically a popup
// use this as a way to allow files to be deleted from in the menu
