package zusass.game.stat;

import zgame.stat.StatId;
import zgame.stat.StatType;
import zgame.stat.Stats;

/** The {@link StatType}s used by the Zusass game */
public enum ZusassStat implements StatType{
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
		for(var v : values()){
			v.getId();
		}
	}
	
	@Override
	public StatType getFromId(int id){
		for(var v : values()){
			if(id == v.id) return v;
		}
		return null;
	}
	
	/**
	 * A debugging tool. Prints an array of all the stats separated by tabs, copy to something like Excel to make it look normal.
	 * Each row is a stat type.
	 * The columns within a row, say Y if that row's stat type is used when calculating the column's stat type, and a dash otherwise
	 */
	public static void printStats(Stats stats){
		var dependents = Stats.dependents;
		var arr = stats.getArr();
		var sb = new StringBuilder("----------------------------------------------------------------------------------------\n");
		sb.append("\t");
		// Go through all the stats to get their names
		for(int i = 0; i < arr.length; i++){
			sb.append(ZusassStat.ATTACK_SPEED.getFromId(i)).append(" (").append(i).append(")\t");
		}
		sb.append("\n");
		// Go through all the stats to show their dependencies
		for(int i = 0; i < arr.length; i++){
			sb.append(ZusassStat.ATTACK_SPEED.getFromId(i)).append(" (").append(i).append(")\t");
			var d = dependents[i];
			// Go through all the stats
			for(int j = 0; j < arr.length; j++){
				var found = false;
				// Check each stat to see if we found a dependency
				for(int k = 0; k < d.length; k++){
					if(d[k] == j){
						found = true;
						break;
					}
				}
				// Add the correct symbol
				sb.append(found ? "Y" : "-").append("\t");
			}
			sb.append("\n");
		}
		sb.append("----------------------------------------------------------------------------------------\n");
		for(int i = 0; i < dependents.length; i++){
			sb.append(ZusassStat.ATTACK_SPEED.getFromId(i)).append(" (").append(i).append(")\t");
			for(int j = 0; j < dependents[i].length; j++){
				sb.append(ZusassStat.ATTACK_SPEED.getFromId(dependents[i][j])).append(" (").append(dependents[i][j]).append(")\t");
			}
			sb.append("\n");
		}
		sb.append("----------------------------------------------------------------------------------------");
		System.out.println(sb);
	}
}
