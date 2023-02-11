package zusass.game.stat;

import zgame.stat.StatType;

public enum ZusassStat implements StatType{
	/** Temporary stat for amount of damage done with melee attacks */
	STRENGTH,
	
	/** The maximum amount of health a thing can have */
	HEALTH_MAX,
	/** The current health of a thing */
	HEALTH_CURRENT,
	/** The amount of health a thing restores each second */
	HEALTH_REGEN,
	
	/** The amount of time between attacks */
	ATTACK_SPEED,
	/** The number of unites away a thing can attack from */
	ATTACK_RANGE,
	/** The amount of damage dealt with a melee attack */
	ATTACK_DAMAGE,
	
	/** The speed at which a thing can move around */
	MOVE_SPEED,
}
