package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zgame.stat.modifier.ModifierType;
import zusass.ZusassGame;
import zusass.menu.comp.ToggleButton;

import java.util.ArrayList;
import java.util.Map;

/** A button for selecting the stat effect type for a spell in the spell maker */
public class ModifierTypeButton extends ToggleButton{
	
	/** The text for add modifier type */
	public static final String ADD = "Add";
	/** The text for multiply modifier type */
	public static final String MULT = "Multiply";
	/** The text for multiplying add modifier type */
	public static final String MULT_ADD = "Multiplier Add";
	
	/** Mapping the spell cast type to the text used for selecting the cast type */
	private static final Map<String, ModifierType> MODIFIER_TYPE_MAP = Map.of(ADD, ModifierType.ADD, MULT, ModifierType.MULT_MULT, MULT_ADD, ModifierType.MULT_ADD);
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu See {@link #menu}
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public ModifierTypeButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 180, 32, null, zgame);
		this.menu = menu;
		
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(820.0, null, null, 200.0));
		
		var values = new ArrayList<String>();
		values.add(ADD);
		values.add(MULT);
		values.add(MULT_ADD);
		this.setValues(values);
		this.setSelectedIndex(0);
	}
	
	@Override
	public void setText(String text){
		super.setText(text);
		menu.updateModifierType(MODIFIER_TYPE_MAP.get(text));
		this.centerText();
	}
}
