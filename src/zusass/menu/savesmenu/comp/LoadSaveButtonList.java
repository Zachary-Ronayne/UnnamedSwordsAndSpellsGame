package zusass.menu.savesmenu.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zgame.core.Game;
import zgame.core.utils.ZStringUtils;
import zgame.menu.MenuHolder;
import zgame.menu.MenuThing;
import zusass.ZusassGame;
import zusass.menu.savesmenu.SavesMenu;
import zusass.utils.ZusassConfig;

/** A {@link MenuHolder} which holds a vertical list of {@link LoadSaveButton}s */
public class LoadSaveButtonList extends MenuHolder{
	
	/** The {@link SavesMenu} using this list */
	private final SavesMenu menu;
	
	/** The buttons displayed */
	private List<LoadSaveButton> buttons;
	
	/** The button in the list which is currently selected, or null if no button is selected */
	private LoadSaveButton selected;
	
	/**
	 * Create a new {@link LoadSaveButtonList} at the specified location
	 *
	 * @param menu See {@link #menu}
	 * @param zgame The game that uses this list
	 */
	public LoadSaveButtonList(SavesMenu menu, ZusassGame zgame){
		super(SavesMenuScroller.X, SavesMenuScroller.Y);
		this.menu = menu;
		this.selected = null;
		this.populate(zgame);
	}
	
	/**
	 * Find all files at {@link #} and generate the list of buttons
	 *
	 * @param zgame The {@link Game} associated with this list
	 * @return true if the files were found, false otherwise
	 */
	public boolean populate(ZusassGame zgame){
		// Reset the button array
		this.setSelected(null);
		this.removeAll();
		if(this.buttons != null){
			for(var b : this.buttons) removeThing(b);
		}
		this.buttons = new ArrayList<>();
		
		// Find all files and make sure they exist
		String path = ZusassConfig.getSavesLocation();
		List<File> files = ZusassConfig.getAllFiles();
		if(files == null) return false;
		
		// Populate the button array
		int i = 0;
		for(File file : files){
			String name = file.getName();
			
			// Make sure to only include files which count as save files
			if(!ZusassConfig.validSaveFileName(name)) continue;
			
			// Add the actual button
			this.addThing(new LoadSaveButton(-LoadSaveButton.WIDTH - 10, i * LoadSaveButton.TOTAL_SPACE, name.replace(ZusassConfig.SAVE_FILE_SUFFIX, ""),
					ZStringUtils.concat(path, name), this.menu, zgame));
			i++;
		}
		// Set the scrollable size to the space the buttons go off screen
		this.menu.getScroller().setAmount(Math.min(0, zgame.getScreenHeight() - (this.buttons.size() + 1) * LoadSaveButton.TOTAL_SPACE - LoadSaveButton.SPACE));
		
		return true;
	}
	
	/**
	 * Also see {@link MenuThing#addThing(MenuThing)}
	 * This object can only hold {@link LoadSaveButton}s, anything else will do nothing and return false
	 */
	@Override
	public boolean addThing(MenuThing thing){
		if(!(thing instanceof LoadSaveButton button)) return false;
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
