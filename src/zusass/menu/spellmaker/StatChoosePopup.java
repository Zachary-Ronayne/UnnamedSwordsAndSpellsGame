package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The popup to select the stat to effect for the spell */
public class StatChoosePopup extends Menu{
	
	/** The menu using this popup */
	private final StatChooseButton button;
	
	/**
	 * Create the popup for selecting a stat type
	 * @param button The button which creates this popup
	 * @param zgame The game this button uses
	 */
	public StatChoosePopup(StatChooseButton button, ZusassGame zgame){
		super();
		this.button = button;
		
		var values = StatSpellType.values();
		var buttonHeight = 30.0;
		var buttonWidth = 300.0;
		var buttonSpace = 2;
		var offset = 10.0;
		var border = 4.0;
		this.setHeight(values.length * buttonHeight + (buttonSpace * (values.length - 1)) + offset * 2);
		this.setWidth(buttonWidth + offset * 2);
		this.setFill(new ZColor(.7, .7, 1, .5));
		this.setBorder(new ZColor(0, 0, .2, .5));
		this.setBorderWidth(border);
		this.setPropagateInput(false);
		
		for(int i = 0; i < values.length; i++){
			var s = values[i];
			var b = new ZusassButton(offset, offset + buttonHeight * i + (i == 0 ? 0 : i * buttonSpace), buttonWidth, buttonHeight, s.getDisplay(), zgame){
				@Override
				public void click(Game game){
					super.click(game);
					button.setSelectedStat(s);
					zgame.getCurrentState().removeTopMenu(game);
				}
			};
			b.setFontSize(20);
			b.centerText();
			b.setTextY(b.getTextY() - 4);
			this.addThing(b);
		}

		this.center(zgame.getWindow());
	}
	
}
