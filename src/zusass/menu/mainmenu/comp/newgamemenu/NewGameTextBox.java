package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.menu.MenuTextBox;
import zusass.menu.comp.ZusassTextBox;

/** A {@link MenuTextBox} for typing in the name of a new game */
public class NewGameTextBox extends ZusassTextBox{

	/** The popup which uses this text box */
	private final NewGamePopup popup;
	
	/**
	 * Initialize the {@link NewGameTextBox}
	 * 
	 * @param popup See {@link #popup}
	 */
	public NewGameTextBox(NewGamePopup popup){
		super(500, 350, 470, 50);
		this.popup = popup;
		this.setHint("Save name...");
	}

	@Override
	public void setText(String text){
		super.setText(text);
		if(this.popup == null) return;
		this.popup.updateCreateVisible(text);
	}
	
}
