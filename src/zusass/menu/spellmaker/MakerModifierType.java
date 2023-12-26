package zusass.menu.spellmaker;

import zgame.menu.togglebutton.ToggleButtonValue;
import zgame.stat.modifier.ModifierType;

/** An enum holding the selectable modifier types for a spell effecting stats, for the spell maker */
public enum MakerModifierType implements ToggleButtonValue{
	ADD("Add", ModifierType.ADD),
	MULT_MULT("Multiply", ModifierType.MULT_MULT),
	MULT_ADD("Multiply Add", ModifierType.MULT_ADD),
	;
	
	/** The text to display for this modifier type */
	private final String text;
	
	/** The modifier type which this enum represents */
	private final ModifierType modifierType;
	
	/**
	 * Init a new modifier type
	 *
	 * @param text See {@link #text}
	 */
	MakerModifierType(String text, ModifierType modifierType){
		this.text = text;
		this.modifierType = modifierType;
	}
	
	/** @return See {@link #modifierType} */
	public ModifierType getModifierType(){
		return this.modifierType;
	}
	
	@Override
	public String getText(){
		return this.text;
	}
}
