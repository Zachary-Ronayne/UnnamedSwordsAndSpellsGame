package zusass.game.stat;

import zgame.stat.StatType;

public enum ZusassStat implements StatType{
	/** For now, governs health and attack damage */
	STRENGTH, STRENGTH_MIN, STRENGTH_MAX, STRENGTH_REGEN,
	/** For now, governs max speed, max stamina, and stamina regen */
	ENDURANCE, ENDURANCE_MIN, ENDURANCE_MAX, ENDURANCE_REGEN,
	
	/** The health resource of a thing before it dies */
	HEALTH, HEALTH_MIN, HEALTH_MAX, HEALTH_REGEN,
	/** The stamina resource of a thing */
	STAMINA, STAMINA_MIN, STAMINA_MAX, STAMINA_REGEN,
	
	/** The amount of time between attacks */
	ATTACK_SPEED,
	/** The number of unites away a thing can attack from */
	ATTACK_RANGE,
	/** The amount of damage dealt with a melee attack */
	ATTACK_DAMAGE,
	
	/** The speed at which a thing can move around */
	MOVE_SPEED,
}
