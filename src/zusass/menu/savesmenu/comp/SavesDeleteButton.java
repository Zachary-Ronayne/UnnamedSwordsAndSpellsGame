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
		SavesMenu menu = this.getMenu();
		LoadSaveButton button = menu.getLoadButtons().getSelected();
		
		if(button == null) return;
		String path = button.getPath();
		try{
			File file = new File(path);
			var success = file.delete();
			if(success) menu.getLoadButtons().setSelected(null);
			menu.getLoadButtons().populate(zgame);
			menu.showMessage(ZStringUtils.concat("Delete ", success ? "success" : "failed", " for: ", button.getText()));
			
		}catch(SecurityException | NullPointerException e){
			if(ZConfig.printErrors()){
				ZStringUtils.prints("Failed to delete file at path", path);
				e.printStackTrace();
			}
			menu.showMessage(ZStringUtils.concat("Delete failed for: ", button.getText()));
		}
	}
	
}
