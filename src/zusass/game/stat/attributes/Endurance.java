package zusass.game.stat.attributes;

import zgame.stat.Attribute;
import zgame.stat.Stats;
import static zusass.game.stat.ZusassStat.*;

public class Endurance extends Attribute{
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Endurance(Stats stats){
		super(stats, ENDURANCE, ENDURANCE_BASE, ENDURANCE_LEVEL, ENDURANCE_REGEN);
	}
}
