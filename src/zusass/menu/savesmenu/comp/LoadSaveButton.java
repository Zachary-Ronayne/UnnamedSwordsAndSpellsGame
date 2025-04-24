package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRect2D;
import zgame.core.utils.ZStringUtils;
import zusass.ZusassGame;
import zusass.game.MainPlay;
import zusass.menu.savesmenu.SavesMenu;
import zusass.utils.ZusassConfig;

/** A specific {@link SavesMenuButton} to manage a save file */
public class LoadSaveButton extends SavesMenuButton{
	
	/** The width of a {@link SavesMenuButton} */
	public static final double WIDTH = 400;
	/** The height of a {@link SavesMenuButton} */
	public static final double HEIGHT = 40;
	/** The space between {@link SavesMenuButton}s */
	public static final double SPACE = 5;
	/** The total of the space between two {@link SavesMenuButton}s and the height of one */
	public static final double TOTAL_SPACE = SPACE + HEIGHT;
	
	/** The path to the file that this button should load */
	private final String path;
	
	/**
	 * Create a new {@link LoadSaveButton} with the specified values
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The name of the file to display
	 * @param path See {@link #path}
	 */
	public LoadSaveButton(double x, double y, String text, String path, SavesMenu menu){
		super(x, y, text, menu);
		this.path = path;
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setFontSize(HEIGHT * 0.6);
		this.setTextX(5);
		this.setTextY(HEIGHT * 0.7);
	}
	
	/*
	 * This class is giving it relative coordinates, which works for where to render it, but not for checking if it should be rendered
	 * Rework the relative rendering system for when it's not using a buffer?
	 */
	
	@Override
	public void render(Game game, Renderer r, ZRect2D bounds){
		super.render(game, r, bounds);
		// If this button is selected, draw an additional highlight
		if(this.getMenu().getLoadButtons().getSelected() == this){
			r.setColor(new ZColor(.2, .2, .5, .3));
			r.drawRectangle(bounds);
		}
	}
	
	@Override
	public void click(Game game){
		this.getMenu().getLoadButtons().setSelected(this);
	}
	
	@Override
	public void doubleClick(Game game){
		ZusassGame zgame = (ZusassGame)game;
		this.attemptLoad(zgame);
	}
	
	/**
	 * Attempt to load the file at {@link #path} into the game
	 *
	 * @param zgame The game to load into
	 * @return true if the file loaded, false otherwise
	 */
	public boolean attemptLoad(ZusassGame zgame){
		boolean success = zgame.loadGame(ZusassConfig.createSaveFileSuffix(path));
		// If the load was successful, enter the play state
		if(success){
			zgame.setCurrentState(new MainPlay(zgame));
		}
		// Otherwise, say that it failed to load
		else this.getMenu().showMessage(ZStringUtils.concat("Load failed for: ", this.getText()));
		return success;
	}
	
	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
}
