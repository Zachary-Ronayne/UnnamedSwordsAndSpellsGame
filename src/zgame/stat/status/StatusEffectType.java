package zgame.stat.status;

import zgame.core.utils.ClassEnum;
import zusass.game.status.NoneEffect;
import zusass.game.status.StatEffect;

/** Different types of {@link StatusEffect} */
public enum StatusEffectType implements ClassEnum<StatusEffect>{
	/** See {@link StatEffect} */
	STAT_EFFECT(StatEffect.class),
	NONE(NoneEffect.class)
	;
	
	/** The class associated with this type */
	private final Class<? extends StatusEffect> clazz;
	
	/** @param clazz See {@link #clazz} */
	StatusEffectType(Class<? extends StatusEffect> clazz){
		this.clazz = clazz;
	}
	
	@Override
	public Class<? extends StatusEffect> getClazz(){
		return this.clazz;
	}
	
	/**
	 * @param clazz the class type to look for
	 * @return The name of the enum type associated with the given class. Or the string for {@link #NONE} if no type could be found
	 */
	public static String name(Class<? extends StatusEffect> clazz){
		return ClassEnum.name(clazz, StatusEffectType.values(), StatusEffectType.NONE);
	}
}
