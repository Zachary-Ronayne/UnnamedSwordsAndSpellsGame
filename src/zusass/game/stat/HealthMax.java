package zusass.game.stat;

import zgame.stat.Stat;
import zgame.stat.Stats;
import static zusass.game.stat.ZusassStat.*;
import zusass.game.things.entities.mobs.ZusassMob;

/** A stat keeping track of the maximum health a {@link ZusassMob} can have */
public class HealthMax extends Stat{
	
	/**
	 * Create the stat for holding max health
	 * @param stats The {@link Stats} which use this stat
	 */
	public HealthMax(Stats stats){
		super(stats, HEALTH_MAX, STRENGTH);
	}
	
	@Override
	public double calculateValue(){
		return super.getOther(STRENGTH) * 5;
	}
}
