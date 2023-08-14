package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ToggleButton;

import java.util.ArrayList;

/** The button for toggling if the spell will be a positive or negative effect */
public class PositiveNegativeButton extends ToggleButton{
	
	/** The text for displaying the buff option */
	public static final String BUFF = "Buff";
	/** The text for displaying the debuff option */
	public static final String DEBUFF = "Debuff";
	
	/**
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public PositiveNegativeButton(ZusassGame zgame){
		super(0, 0, 200, 32, null, zgame);
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(260.0, null, null, 200.0));
		
		var values = new ArrayList<String>();
		values.add(BUFF);
		values.add(DEBUFF);
		this.setValues(values);
		this.setSelectedIndex(0);
	}
	
	@Override
	public void setText(String text){
		super.setText(text);
		this.centerText();
	}
}
