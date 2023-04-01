package zusass.game.stat;

import zgame.stat.StatId;
import zgame.stat.StatType;

/** The {@link StatType}s used by the Zusass game */
public enum ZusassStat implements StatType<ZusassStat>{
	/** For now, governs health and attack damage */
	STRENGTH, STRENGTH_BASE, STRENGTH_LEVEL, STRENGTH_REGEN,
	/** For now, governs max speed, max stamina, and stamina regen */
	ENDURANCE, ENDURANCE_BASE, ENDURANCE_LEVEL, ENDURANCE_REGEN,
	/** For now, governs max mana and mana regen */
	INTELLIGENCE, INTELLIGENCE_BASE, INTELLIGENCE_LEVEL, INTELLIGENCE_REGEN,
	
	/** The health resource of a thing before it dies */
	HEALTH, HEALTH_MIN, HEALTH_MAX, HEALTH_REGEN,
	/** The stamina resource of a thing */
	STAMINA, STAMINA_MIN, STAMINA_MAX, STAMINA_REGEN,
	/** The mana resource of a thing */
	MANA, MANA_MIN, MANA_MAX, MANA_REGEN,
	
	/** The amount of time between attacks */
	ATTACK_SPEED,
	/** The number of unites away a thing can attack from */
	ATTACK_RANGE,
	/** The amount of damage dealt with a melee attack */
	ATTACK_DAMAGE,
	
	/** The speed at which a thing can move around */
	MOVE_SPEED,
	;
	
	/** The id representing this enum */
	private final int id;
	
	/** Initialize the enum with the next id */
	ZusassStat(){
		this.id = StatId.next();
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
