package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.core.state.MenuNode;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The button that, when clicked, opens a button a popup to select the stat to effect for the spell */
public class StatChooseButton extends ZusassButton{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/** The selected stat to use for a spell */
	private StatSpellType selectedStat;
	
	/**
	 * Create the button for selecting a stat type
	 * @param menu The menu which uses this button
	 * @param zgame The game this button uses
	 */
	public StatChooseButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 180, 32, "", zgame);
		
		this.setSelectedStat(null);
		
		// TODO disable this button while the popup is open
		
		this.menu = menu;
		this.setFontSize(20);
		this.centerText();
		this.setFormatter(new PixelFormatter(20.0, null, null, 200.0));
	}
	
	/**
	 * Update the text to display for {@link #selectedStat}
	 */
	public void updateText(){
		String s;
		if(this.selectedStat == null) s = "Select Stat...";
		else s = this.selectedStat.getDisplay();
		this.setText(s);
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		game.getCurrentState().popupMenu(game, new MenuNode(new StatChoosePopup(this, (ZusassGame)game),
				false, false, false));
	}
	
	/** @return See {@link #selectedStat} */
	public StatSpellType getSelectedStat(){
		return this.selectedStat;
	}
	
	/** @param selectedStat See {@link #selectedStat} */
	public void setSelectedStat(StatSpellType selectedStat){
		this.selectedStat = selectedStat;
		this.updateText();
		if(this.menu != null) this.menu.updateMenuState();
	}
	
}
