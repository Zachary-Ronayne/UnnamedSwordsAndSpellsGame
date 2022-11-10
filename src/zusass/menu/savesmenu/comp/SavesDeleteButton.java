package zusass.menu.savesmenu.comp;

import java.io.File;

import zgame.core.Game;
import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;
import zusass.ZusassGame;
import zusass.menu.savesmenu.SavesMenu;

/** A button to delete the selected save file in the saves menu */
public class SavesDeleteButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesLoadButton}
	 * 
	 * @param menu See {@link #getMenu()}
	 * @param zgame The {@link Game} associated with this button
	 */
	public SavesDeleteButton(SavesMenu menu, ZusassGame zgame){
		super(360, 600, "Delete", menu, zgame);
	}
	
	@Override
	public void click(Game game){
		ZusassGame zgame = (ZusassGame)game;
		
		LoadSaveButton button = this.getMenu().getLoadButtons().getSelected();
		if(button == null) return;
		String path = button.getPath();
		try{
			File file = new File(path);
			file.delete();
			this.getMenu().getLoadButtons().setSelected(null);
			this.getMenu().getLoadButtons().populate(zgame);
			
		}catch(SecurityException | NullPointerException e){
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Failed to delete file at path", path);
				e.printStackTrace();
			}
			this.getMenu().showMessage(ZStringUtils.concat("Delete Failed for", button.getText()));
		}
		// TODO make this show a popup confirming to delete the file
	}
	
}
