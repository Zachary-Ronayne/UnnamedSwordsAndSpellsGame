package zusass.menu.spellmaker;

import zusass.game.stat.ZusassStat;

/** An enum holding the additional data used for selecting a stat for a spell */
public enum StatSpellType{
	HEALTH(ZusassStat.HEALTH, ZusassStat.HEALTH_REGEN, "Health"),
	HEALTH_MAX(ZusassStat.HEALTH_MAX, "Max Health"),
	
	STAMINA(ZusassStat.STAMINA, ZusassStat.STAMINA_REGEN, "Stamina"),
	STAMINA_MAX(ZusassStat.STAMINA_MAX, "Max Stamina"),
	
	MANA(ZusassStat.MANA, ZusassStat.MANA_REGEN, "Mana"),
	MANA_MAX(ZusassStat.MANA_MAX, "Max Mana"),
	
	STRENGTH(ZusassStat.STRENGTH, "Modify Strength"),
	RESTORE_STRENGTH(ZusassStat.STRENGTH, ZusassStat.STRENGTH_REGEN, "Regen Strength"),
	
	ENDURANCE(ZusassStat.ENDURANCE, "Modify Endurance"),
	RESTORE_ENDURANCE(ZusassStat.ENDURANCE, ZusassStat.ENDURANCE_REGEN, "Regen Endurance"),
	
	INTELLIGENCE(ZusassStat.ENDURANCE, "Modify Intelligence"),
	RESTORE_INTELLIGENCE(ZusassStat.INTELLIGENCE, ZusassStat.INTELLIGENCE_REGEN, "Regen Intelligence"),
	
	AGILITY(ZusassStat.AGILITY, "Modify agility"),
	RESTORE_AGILITY(ZusassStat.AGILITY, ZusassStat.AGILITY_REGEN, "Regen agility"),
	
	MOVE_SPEED(ZusassStat.MOVE_SPEED, "Move Speed"),
	ATTACK_SPEED(ZusassStat.ATTACK_SPEED, "Attack Speed"),
	ATTACK_DAMAGE(ZusassStat.ATTACK_DAMAGE, "Attack Damage"),
	ATTACK_RANGE(ZusassStat.ATTACK_RANGE, "Attack Range"),
	;
	
	/** The stat to effect when the spell is instant, or null if this spell type cannot be used as an instant effect */
	private final ZusassStat instant;
	/** The stat to effect when the spell is a status effect */
	private final ZusassStat status;
	/** The text to display or the stat */
	private final String display;
	
	/**
	 * Init a stat spell type where only {@link #status} is a valid type
	 *
	 * @param stat See {@link #status}
	 * @param display See {@link #display}
	 */
	StatSpellType(ZusassStat stat, String display){
		this(null, stat, display);
	}
	
	/**
	 * Init a stat spell type
	 *
	 * @param instant See {@link #instant}
	 * @param status See {@link #status}
	 * @param display See {@link #display}
	 */
	StatSpellType(ZusassStat instant, ZusassStat status, String display){
		this.instant = instant;
		this.status = status;
		this.display = display;
	}
	
	/** @return See {@link #instant} */
	public ZusassStat getInstant(){
		return this.instant;
	}
	
	/** @return See {@link #status} */
	public ZusassStat getStatus(){
		return this.status;
	}
	
	/** @return See {@link #display} */
	public String getDisplay(){
		return this.display;
	}
	
	public boolean canUseInstant(){
		return this.getInstant() != null;
	}
}