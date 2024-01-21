package zusass.game.stat;

import zgame.stat.StatId;
import zgame.stat.StatType;

/** The {@link StatType}s used by the Zusass game */
public enum ZusassStat implements StatType<ZusassStat>{
	/** For now, governs health and attack damage */
	STRENGTH(2), STRENGTH_BASE(2), STRENGTH_LEVEL(2), STRENGTH_REGEN(2),
	/** For now, governs max speed, max stamina, and stamina regen */
	ENDURANCE(1), ENDURANCE_BASE(1), ENDURANCE_LEVEL(1), ENDURANCE_REGEN(1),
	/** For now, governs max mana and mana regen */
	INTELLIGENCE(1.5), INTELLIGENCE_BASE(1.5), INTELLIGENCE_LEVEL(1.5), INTELLIGENCE_REGEN(1.5),
	/** For now, governs jump height */
	AGILITY(1), AGILITY_BASE(1), AGILITY_LEVEL(1), AGILITY_REGEN(1),
	
	/** The health resource of a thing before it dies */
	HEALTH(2), HEALTH_MIN(2), HEALTH_MAX(2), HEALTH_REGEN(2),
	/** The stamina resource of a thing */
	STAMINA(.5), STAMINA_MIN(.5), STAMINA_MAX(.5), STAMINA_REGEN(.5),
	/** The mana resource of a thing */
	MANA(1, 100), MANA_MIN(1), MANA_MAX(1), MANA_REGEN(10),
	
	/** The number of attacks per second */
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
	
	/** How valuable this stat is when it is a buff, i.e. how difficult it should be to cast spells that effect this stat */
	private final double buffValue;
	
	/** How valuable this stat is when it is a debuff, i.e. how difficult it should be to cast spells that effect this stat */
	private final double debuffValue;
	
	/** Initialize the enum with the next id */
	ZusassStat(){
		this(1);
	}
	
	/**
	 * Initialize the enum with the next id
	 *
	 * @param buffValue The default number for {@link #buffValue} {@link #debuffValue}, can be changed by settings
	 */
	ZusassStat(double buffValue){
		this(buffValue, buffValue);
	}
	
	/**
	 * Initialize the enum with the next id
	 *
	 * @param buffValue The default number for {@link #buffValue}, can be changed by settings
	 */
	ZusassStat(double buffValue, double debuffValue){
		this.id = StatId.next();
		this.buffValue = buffValue;
		this.debuffValue = debuffValue;
	}
	
	/** @return  See {@link #buffValue} */
	public double getBuffValue(){
		return this.buffValue;
	}
	
	/** @return See {@link #debuffValue} */
	public double getDebuffValue(){
		return this.debuffValue;
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
