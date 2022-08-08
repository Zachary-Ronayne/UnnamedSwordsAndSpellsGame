package zusass.menu.savesmenu.comp;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
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
		super(x, y, text, menu, game);
		this.path = path;
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setFontSize(HEIGHT * 0.6);
		this.setTextX(5);
		this.setTextY(HEIGHT * 0.7);
	}

	@Override
	public void render(Game<ZUSASSData> game, Renderer r){
		super.render(game, r);
		// If this button is selected, draw an additional highlight
		if(this.getMenu().getLoadButtons().getSelected() == this){
			r.setColor(new ZColor(.2, .2, .5, .3));
			r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
	}

	@Override
	public void click(Game<ZUSASSData> game){
		// TODO double click to automatically load
		this.getMenu().getLoadButtons().setSelected(this);
	}

	/**
	 * Attempt to load the file at {@link #path} into the game
	 * 
	 * @param game The game to load into
	 * @return true if the file loaded, false otherwise
	 */
	public boolean attemptLoad(Game<ZUSASSData> game){
		boolean success = game.loadGame(ZUSASSConfig.createSaveFileSuffix(path));
		// If the load was successful, enter the play state
		if(success){
			game.setPlayState(new MainPlay(game));
			game.enterPlayState();
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
