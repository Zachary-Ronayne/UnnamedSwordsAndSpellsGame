package zusass.menu.spellmaker;

import zusass.game.stat.ZusassStat;

/** An enum holding the additional data used for selecting a stat for a spell */
public enum StatSpellType{
	// TODO add all effects, also account for if an effect isn't valid, like speed can't be an instant effect
	HEALTH(ZusassStat.HEALTH, ZusassStat.HEALTH_REGEN, "Health"),
	MOVE_SPEED(ZusassStat.MOVE_SPEED, "Move Speed"),
	ATTACK_RANGE(ZusassStat.ATTACK_RANGE, "Attack Range"),
	;
	
	/** The stat to effect when the spell is instant */
	private final ZusassStat instant;
	/** The stat to effect when the spell is a status effect */
	private final ZusassStat status;
	/** The text to display or the stat */
	private final String display;
	
	/**
	 * Init a stat spell type where instant and status are the same
	 *
	 * @param stat See {@link #instant} and {@link #status}
	 * @param display See {@link #display}
	 */
	StatSpellType(ZusassStat stat, String display){
		this(stat, stat, display);
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
}