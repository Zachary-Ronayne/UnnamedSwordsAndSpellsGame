package zusass.menu.savesmenu.comp;

import java.io.File;

import zgame.core.utils.ZConfig;
import zgame.core.utils.ZStringUtils;
import zusass.menu.savesmenu.SavesMenu;

/** A button to delete the selected save file in the saves menu */
public class SavesDeleteButton extends SavesMenuButton{
	
	/**
	 * Create the {@link SavesLoadButton}
	 *
	 * @param menu See {@link #getMenu()}
	 */
	public SavesDeleteButton(SavesMenu menu){
		super(360, 600, "Delete", menu);
	}
	
	@Override
	public void click(){
		SavesMenu menu = this.getMenu();
		LoadSaveButton button = menu.getLoadButtons().getSelected();
		
		if(button == null) return;
		String path = button.getPath();
		try{
			File file = new File(path);
			var success = file.delete();
			if(success) menu.getLoadButtons().setSelected(null);
			menu.getLoadButtons().populate();
			menu.showMessage(ZStringUtils.concat("Delete ", success ? "success" : "failed", " for: ", button.getText()));
			
		}catch(SecurityException | NullPointerException e){
			ZConfig.error(e, "Failed to delete file at path", path);
			menu.showMessage(ZStringUtils.concat("Delete failed for: ", button.getText()));
		}
	}
	
}
