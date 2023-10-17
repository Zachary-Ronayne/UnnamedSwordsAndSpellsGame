package zusass.game.magic.effect;

import zgame.core.utils.ClassEnum;

/** An enum for holding the types of effects that spells can have */
public enum SpellEffectType implements ClassEnum<SpellEffect>{
	/** See {@link SpellEffectNone} */
	NONE(SpellEffectNone.class),
	/** See {@link SpellEffectStatAdd} */
	STAT_ADD(SpellEffectStatAdd.class),
	/** See {@link SpellEffectStatusEffect} */
	STATUS_EFFECT(SpellEffectStatusEffect.class),
	;
	
	/** The class associated with this type */
	private final Class<? extends SpellEffect> clazz;
	
	/** @param clazz See {@link #clazz} */
	SpellEffectType(Class<? extends SpellEffect> clazz){
		this.clazz = clazz;
	}
	
	@Override
	public Class<? extends SpellEffect> getClazz(){
		return this.clazz;
	}
	
	/**
	 * @param clazz the class type to look for
	 * @return The name of the enum type associated with the given class. Or the string for {@link #NONE} if no type could be found
	 */
	public static String name(Class<? extends SpellEffect> clazz){
		return ClassEnum.name(clazz, SpellEffectType.values(), SpellEffectType.NONE);
	}
}
