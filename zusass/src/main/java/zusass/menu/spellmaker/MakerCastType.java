package zusass.menu.spellmaker;

import zgame.menu.togglebutton.ToggleButtonValue;
import zusass.game.magic.SpellCastType;

/** An enum holding the selectable cast types for casting a spell, for the spell maker */
public enum MakerCastType implements ToggleButtonValue{
	PROJECTILE("Projectile", SpellCastType.PROJECTILE), SELF("Self", SpellCastType.SELF);
	
	/** The text to display for this cast type */
	private final String text;
	
	/** The cast type which this enum represents */
	private final SpellCastType castType;
	
	/**
	 * Init a new cast type
	 *
	 * @param text See {@link #text}
	 */
	MakerCastType(String text, SpellCastType castType){
		this.text = text;
		this.castType = castType;
	}
	
	/** @return See {@link #castType} */
	public SpellCastType getCastType(){
		return this.castType;
	}
	
	@Override
	public String getText(){
		return this.text;
	}
}
