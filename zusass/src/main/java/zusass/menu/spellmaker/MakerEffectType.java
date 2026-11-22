package zusass.menu.spellmaker;

import zgame.menu.togglebutton.ToggleButtonValue;
import zusass.game.magic.effect.SpellEffectType;

/** An enum holding the selectable effect for a spell, for the spell maker */
public enum MakerEffectType implements ToggleButtonValue{
	STATUS("Status", SpellEffectType.STATUS_EFFECT), INSTANT("Instant", SpellEffectType.STAT_ADD);
	
	/** The text to display for this effect type */
	private final String text;
	
	/** The effect type which this enum represents */
	private final SpellEffectType effectType;
	
	/**
	 * Init a new effect type
	 *
	 * @param text See {@link #text}
	 */
	MakerEffectType(String text, SpellEffectType effectType){
		this.text = text;
		this.effectType = effectType;
	}
	
	/** @return See {@link #effectType} */
	public SpellEffectType getEffectType(){
		return this.effectType;
	}
	
	@Override
	public String getText(){
		return this.text;
	}
}
