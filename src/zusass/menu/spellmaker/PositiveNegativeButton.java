package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zgame.stat.modifier.ModifierType;
import zusass.ZusassGame;
import zusass.menu.comp.ToggleButton;

import java.util.ArrayList;

/** The button for toggling if the spell will be a positive or negative effect */
public class PositiveNegativeButton extends ToggleButton{
	
	/** The text for displaying the buff option */
	public static final String BUFF = "Buff";
	/** The text for displaying the debuff option */
	public static final String DEBUFF = "Debuff";
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu The menu using this button
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public PositiveNegativeButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 180, 32, null, zgame);
		this.menu = menu;
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(220.0, null, null, 200.0));
		
		var values = new ArrayList<String>();
		values.add(BUFF);
		values.add(DEBUFF);
		this.setValues(values);
		this.setSelectedIndex(0);
	}
	
	@Override
	public String getText(){
		if(this.menu != null && this.menu.getSelectedModifierType() == ModifierType.MULT_MULT) return BUFF;
		return super.getText();
	}
	
	@Override
	public void setText(String text){
		super.setText(text);
		this.centerText();
	}
}
