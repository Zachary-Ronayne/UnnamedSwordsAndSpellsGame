package zusass.game.stat;

import zgame.stat.StatType;

public enum ZusassStat implements StatType{
	/** How strong a thing is. For now, governs health and attack damage */
	STRENGTH, STRENGTH_MIN, STRENGTH_MAX, STRENGTH_REGEN,
	
	/** The current health of a thing */
	HEALTH, HEALTH_MIN, HEALTH_MAX, HEALTH_REGEN,
	
	/** The amount of time between attacks */
	ATTACK_SPEED,
	/** The number of unites away a thing can attack from */
	ATTACK_RANGE,
	/** The amount of damage dealt with a melee attack */
	ATTACK_DAMAGE,
	
	/** The speed at which a thing can move around */
	MOVE_SPEED,
}
