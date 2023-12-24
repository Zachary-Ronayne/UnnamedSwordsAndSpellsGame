package zusass.menu.mainmenu.comp.newgamemenu;

import zgame.menu.MenuTextBox;
import zusass.ZusassGame;

/** A {@link MenuTextBox} for typing in the name of a new game */
public class NewGameTextBox extends ZusassTextBox{

	/** The popup which uses this text box */
	private final NewGamePopup popup;
	
	/**
	 * Initialize the {@link NewGameTextBox}
	 * 
	 * @param zgame The Zusass game used by this thing
	 * @param popup See {@link #popup}
	 */
	public NewGameTextBox(ZusassGame zgame, NewGamePopup popup){
		super(500, 400, 470, 50, zgame);
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
