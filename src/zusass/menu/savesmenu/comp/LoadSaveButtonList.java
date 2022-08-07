package zusass.menu.savesmenu.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zgame.core.Game;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;
import zgame.menu.MenuThing;
import zusass.ZUSASSData;
import zusass.menu.savesmenu.SavesMenu;
import zusass.utils.ZUSASSConfig;

/** A {@link MenuThing} which holds a vertical list of {@link LoadSaveButton}s */
public class LoadSaveButtonList extends MenuThing<ZUSASSData>{
	
	/** The {@link SavesMenu} using this list */
	private SavesMenu menu;
	
	/** The scroller which will interact with this list */
	private SavesMenuScroller scroller;
	
	/** The buttons displayed */
	private List<LoadSaveButton> buttons;
	
	/**
	 * Create a new {@link LoadButtonList} at the specified location
	 * 
	 * @param scroller See {@link #scroller}
	 * @param game The game that uses this list
	 */
	public LoadSaveButtonList(SavesMenu menu, SavesMenuScroller scroller, Game<ZUSASSData> game){
		super();
		this.menu = menu;
		this.scroller = scroller;
		this.populate(game);
		this.scroller.addThing(this);
	}
	
	/**
	 * Find all files at {@link #path} and generate the list of buttons
	 * 
	 * @param game The {@link Game} associated with this list
	 * @return true if the files were found, false otherwise
	 */
	public boolean populate(Game<ZUSASSData> game){
		// Find all files and make sure they exist
		String path = ZUSASSConfig.getSavesLocation();
		String[] files = null;
		try{
			File file = new File(path);
			if(!file.isDirectory()) return false;
			files = file.list();
			
		}catch(NullPointerException | SecurityException e){
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Failed to find file location for saves at:", path);
				e.printStackTrace();
			}
			return false;
		}
		if(files == null) return false;
		
		// Populate the button array
		this.buttons = new ArrayList<LoadSaveButton>();
		int i = 0;
		for(String file : files){
			// Make sure to only include files which count as save files
			if(!ZUSASSConfig.validSaveFileName(file)) continue;
			
			// Add the actual button
			this.addThing(new LoadSaveButton(-LoadSaveButton.WIDTH - 10, i * LoadSaveButton.TOTAL_SPACE, file.replace(ZUSASSConfig.SAVE_FILE_SUFFIX, ""),
					ZStringUtils.concat(path, file), this.menu, game));
			i++;
		}
		// Set the scrollable size to the space the buttons go off screen
		this.scroller.setAmount(Math.min(0, game.getScreenHeight() - (this.buttons.size() + 1) * LoadSaveButton.TOTAL_SPACE - LoadSaveButton.SPACE));
		
		return true;
	}
	
	/**
	 * Also see {@link MenuThing#addThing(MenuThing)}
	 * This object can only hold {@link LoadSaveButton}s, anything else will do nothing and return false
	 */
	@Override
	public boolean addThing(MenuThing<ZUSASSData> thing){
		if(!(thing instanceof LoadSaveButton)) return false;
		LoadSaveButton button = (LoadSaveButton)thing;
		this.buttons.add(button);
		return super.addThing(thing);
	}
}
