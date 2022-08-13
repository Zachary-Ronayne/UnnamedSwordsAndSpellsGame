package zusass.menu.savesmenu.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zgame.core.Game;
import zgame.core.utils.ZStringUtils;
import zgame.menu.MenuThing;
import zusass.ZUSASSData;
import zusass.menu.savesmenu.SavesMenu;
import zusass.utils.ZUSASSConfig;

/** A {@link MenuThing} which holds a vertical list of {@link LoadSaveButton}s */
public class LoadSaveButtonList extends MenuThing<ZUSASSData>{
	
	/** The {@link SavesMenu} using this list */
	private SavesMenu menu;
	
	/** The buttons displayed */
	private List<LoadSaveButton> buttons;
	
	/** The button in the list which is currently selected, or null if no button is selected */
	private LoadSaveButton selected;
	
	/**
	 * Create a new {@link LoadButtonList} at the specified location
	 * 
	 * @param scroller See {@link #scroller}
	 * @param game The game that uses this list
	 */
	public LoadSaveButtonList(SavesMenu menu, Game<ZUSASSData> game){
		super();
		this.menu = menu;
		this.selected = null;
		this.populate(game);
		this.menu.getScroller().addThing(this);
	}
	
	/**
	 * Find all files at {@link #path} and generate the list of buttons
	 * 
	 * @param game The {@link Game} associated with this list
	 * @return true if the files were found, false otherwise
	 */
	public boolean populate(Game<ZUSASSData> game){
		// Reset the button array
		this.setSelected(null);
		this.removeAll();
		if(this.buttons != null) this.buttons.forEach(b -> removeThing(b));
		this.buttons = new ArrayList<LoadSaveButton>();

		// Find all files and make sure they exist
		String path = ZUSASSConfig.getSavesLocation();
		List<File> files = ZUSASSConfig.getAllFiles();
		if(files == null) return false;
		
		// Populate the button array
		int i = 0;
		for(File file : files){
			String name = file.getName();
			
			// Make sure to only include files which count as save files
			if(!ZUSASSConfig.validSaveFileName(name)) continue;
			
			// Add the actual button
			this.addThing(new LoadSaveButton(-LoadSaveButton.WIDTH - 10, i * LoadSaveButton.TOTAL_SPACE, name.replace(ZUSASSConfig.SAVE_FILE_SUFFIX, ""),
					ZStringUtils.concat(path, name), this.menu, game));
			i++;
		}
		// Set the scrollable size to the space the buttons go off screen
		this.menu.getScroller().setAmount(Math.min(0, game.getScreenHeight() - (this.buttons.size() + 1) * LoadSaveButton.TOTAL_SPACE - LoadSaveButton.SPACE));
		
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
	
	/** @return See {@link #selected} */
	public LoadSaveButton getSelected(){
		return this.selected;
	}
	
	/** @param selected See {@link #selected} */
	public void setSelected(LoadSaveButton selected){
		this.selected = selected;
		this.menu.showExtraButtons(this.selected != null);
	}
	
}
