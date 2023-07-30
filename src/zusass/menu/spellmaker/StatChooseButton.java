package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The button that, when clicked, opens a button a popup to select the stat to effect for the spell */
public class StatChooseButton extends ZusassButton{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * Create the button for selecting a stat type
	 * @param menu The menu which uses this button
	 * @param zgame The game this button uses
	 */
	public StatChooseButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 200, 32, "Select Stat...", zgame);
		this.menu = menu;
		this.setFontSize(20);
		this.centerText();
		this.setFormatter(new PixelFormatter(20.0, null, null, 200.0));
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		game.getCurrentState().popupMenu(new StatChoosePopup(this.menu, (ZusassGame)game));
	}
}
