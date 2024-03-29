package zusass.game.stat.attributes;

import zgame.stat.Attribute;
import zgame.stat.Stats;

import static zusass.game.stat.ZusassStat.*;

public class Intelligence extends Attribute{
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Intelligence(Stats stats){
		super(stats, INTELLIGENCE, INTELLIGENCE_BASE, INTELLIGENCE_LEVEL, INTELLIGENCE_REGEN);
	}
}
