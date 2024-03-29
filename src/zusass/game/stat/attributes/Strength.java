package zusass.game.stat.attributes;

import zgame.stat.Attribute;
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
		super(stats, STRENGTH, STRENGTH_BASE, STRENGTH_LEVEL, STRENGTH_REGEN);
	}
}
