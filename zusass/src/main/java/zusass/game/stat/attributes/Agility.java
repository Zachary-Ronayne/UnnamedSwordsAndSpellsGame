package zusass.game.stat.attributes;

import zgame.stat.Attribute;
import zgame.stat.Stats;

import static zusass.game.stat.ZusassStat.*;

public class Agility extends Attribute{
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Agility(Stats stats){
		super(stats, AGILITY, AGILITY_BASE, AGILITY_LEVEL, AGILITY_REGEN);
	}
}
