package zusass.game.stat;

import zgame.stat.Stats;

import static zusass.game.stat.ZusassStat.*;

/** How strong a thing is. For now, governs max health and attack damage */
public class Strength extends Attribute{
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Strength(Stats stats){
		super(stats, STRENGTH, STRENGTH_MIN, STRENGTH_MAX, STRENGTH_REGEN);
	}
}
