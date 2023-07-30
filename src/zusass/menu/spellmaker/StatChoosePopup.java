package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.menu.Menu;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The popup to select the stat to effect for the spell */
public class StatChoosePopup extends Menu{
	
	/** The menu using this popup */
	private final SpellMakerMenu menu;
	
	/**
	 * Create the popup for selecting a stat type
	 * @param menu The menu which uses this popup
	 * @param zgame The game this button uses
	 */
	public StatChoosePopup(SpellMakerMenu menu, ZusassGame zgame){
		super();
		this.menu = menu;
		
		this.addThing(new ZusassButton(10, 10, 300, 100, "test", zgame){
			@Override
			public void click(Game game){
				super.click(game);
				zgame.getCurrentState().removeTopMenu();
			}
		});
	}

}
