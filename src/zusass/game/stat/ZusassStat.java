package zusass.game.stat;

import zgame.stat.StatId;
import zgame.stat.StatType;

/** The {@link StatType}s used by the Zusass game */
public enum ZusassStat implements StatType<ZusassStat>{
	/** For now, governs health and attack damage */
	STRENGTH(2), STRENGTH_BASE, STRENGTH_LEVEL, STRENGTH_REGEN,
	/** For now, governs max speed, max stamina, and stamina regen */
	ENDURANCE(1), ENDURANCE_BASE, ENDURANCE_LEVEL, ENDURANCE_REGEN,
	/** For now, governs max mana and mana regen */
	INTELLIGENCE(1.5), INTELLIGENCE_BASE, INTELLIGENCE_LEVEL, INTELLIGENCE_REGEN,
	
	/** The health resource of a thing before it dies */
	HEALTH(2), HEALTH_MIN, HEALTH_MAX, HEALTH_REGEN,
	/** The stamina resource of a thing */
	STAMINA(.5), STAMINA_MIN, STAMINA_MAX, STAMINA_REGEN,
	/** The mana resource of a thing */
	MANA(1), MANA_MIN, MANA_MAX, MANA_REGEN,
	
	/** The amount of time between attacks */
	ATTACK_SPEED(.5),
	/** The number of units away a thing can attack from */
	ATTACK_RANGE(.7),
	/** The amount of damage dealt with a melee attack */
	ATTACK_DAMAGE(1.3),
	
	/** The speed at which a thing can move around */
	MOVE_SPEED(.8),
	;
	
	/** The id representing this enum */
	private final int id;
	
	/** How valuable this stat is, i.e. how difficult it should be to cast spells that effect this stat */
	private double value;
	
	/** Initialize the enum with the next id */
	ZusassStat(){
		this(1);
	}
	
	/**
	 * Initialize the enum with the next id
	 *
	 * @param value The default number for {@link #value}, can be changed by settings
	 */
	ZusassStat(double value){
		this.id = StatId.next();
		this.value = value;
	}
	
	/** @return  See {@link #value} */
	public double getValue(){
		return this.value;
	}
	
	/** @param value See {@link #value}. Should only be set when changing settings */
	public void setValue(double value){
		this.value = value;
	}
	
	@Override
	public int getId(){
		return id;
	}
	
	/** Must call this before the game is initialized to ensure stats work */
	public static void init(){
		for(var v : values()) v.getId();
		StatType.add(ZusassStat.values());
	}
	
	@Override
	public ZusassStat getFromId(int id){
		for(var v : values()){
			if(id == v.id) return v;
		}
		return null;
	}
}
