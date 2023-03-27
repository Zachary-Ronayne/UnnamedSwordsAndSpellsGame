package zgame.stat.modifier;

import zgame.stat.StatType;

/**
 * An object holding a {@link StatModifier} and {@link StatType}
 *
 * @param modifier The modifier of this object
 * @param type The type of stat of this object
 */
public record TypedModifier(StatModifier modifier, StatType type){
	/** @return The id of the {@link StatType} used by this object */
	public int getId(){
		return this.type.getId();
	}
}
