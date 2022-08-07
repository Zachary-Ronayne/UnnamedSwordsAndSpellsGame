package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zgame.core.utils.ZStringUtils;
import zusass.ZUSASSData;
import zusass.ZUSASSGame;
import zusass.game.MainPlay;
import zusass.menu.savesmenu.SavesMenu;
import zusass.utils.ZUSASSConfig;

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
	private String path;

	/** The {@link SavesMenu} using this button */
	private SavesMenu menu;

	/**
	 * Create a new {@link LoadSaveButton} with the specified values
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param text The name of the file to display
	 * @param path See {@link #path}
	 * @param game The {@link ZUSASSGame} associated with this button
	 */
	public LoadSaveButton(double x, double y, String text, String path, SavesMenu menu, Game<ZUSASSData> game){
		super(x, y, text, game);
		this.menu = menu;
		this.path = path;
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setFontSize(HEIGHT * 0.6);
		this.setTextX(5);
		this.setTextY(HEIGHT * 0.7);
	}

	@Override
	public void click(Game<ZUSASSData> game){
		// TODO make clicking a file once select it and show an option to open it, and clicking it twice will open it automatically
		boolean success = game.loadGame(ZUSASSConfig.createSaveFileSuffix(path));
		// If the load was successful, enter the play state
		if(success){
			game.setPlayState(new MainPlay(game));
			game.enterPlayState();
		}
		// Otherwise, say that it failed to load
		this.menu.showMessage(ZStringUtils.concat("Load failed for: ", this.getText()));
	}

	@Override
	public void mouseMove(Game<ZUSASSData> game, double x, double y){
		super.mouseMove(game, x, y);
		// TODO make hovering a file select it, then give a delete button option to delete the file
	}

	/** @return See {@link #path} */
	public String getPath(){
		return this.path;
	}
	
}
