package zusass.game.magic;

import zgame.core.utils.ClassEnum;

/** An enum holding the valid values for a casting type of spell */
public enum SpellCastType implements ClassEnum<Spell>{
	/** See {@link NoneSpell} */
	NONE(NoneSpell.class),
	/** See {@link MultiSpell} */
	MULTI(MultiSpell.class),
	/** See {@link ProjectileSpell} */
	PROJECTILE(ProjectileSpell.class),
	/** See {@link SelfSpell} */
	SELF(SelfSpell.class),
	;
	
	/** The class associated with this type */
	private final Class<? extends Spell> clazz;
	
	/** @param clazz See {@link #clazz} */
	SpellCastType(Class<? extends Spell> clazz){
		this.clazz = clazz;
	}
	
	@Override
	public Class<? extends Spell> getClazz(){
		return this.clazz;
	}
	
	/**
	 * @param clazz the class type to look for
	 * @return The name of the enum type associated with the given class. Or the string for {@link #NONE} if no type could be found
	 */
	public static String name(Class<? extends Spell> clazz){
		return ClassEnum.name(clazz, SpellCastType.values(), SpellCastType.NONE);
	}
	
}
