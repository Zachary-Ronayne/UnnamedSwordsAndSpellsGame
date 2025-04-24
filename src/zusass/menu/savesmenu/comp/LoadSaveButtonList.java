package zusass.menu.savesmenu.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zgame.core.Game;
import zgame.core.utils.NotNullList;
import zgame.core.utils.ZStringUtils;
import zgame.menu.MenuHolder;
import zusass.ZusassGame;
import zusass.menu.savesmenu.SavesMenu;
import zusass.utils.ZusassConfig;

/** A {@link MenuHolder} which holds a vertical list of {@link LoadSaveButton}s */
public class LoadSaveButtonList extends MenuHolder{
	
	/** The {@link SavesMenu} using this list */
	private final SavesMenu menu;
	
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
		this.getAllThings().addClass(SavesLoadButton.class);
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
		var buttons = new ArrayList<>(this.getButtons());
		for(var b : buttons) removeThing(b);
		
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
					ZStringUtils.concat(path, name), this.menu));
			i++;
		}
		// Set the scrollable size to the space the buttons go off screen
		buttons = this.getButtons();
		// TODO make this not need zgame
		this.menu.getScroller().setAmount(Math.min(0, zgame.getScreenHeight() - (buttons.size() + 1) * LoadSaveButton.TOTAL_SPACE - LoadSaveButton.SPACE));
		
		return true;
	}
	
	public NotNullList<SavesLoadButton> getButtons(){
		return this.getAllThings().get(SavesLoadButton.class);
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
