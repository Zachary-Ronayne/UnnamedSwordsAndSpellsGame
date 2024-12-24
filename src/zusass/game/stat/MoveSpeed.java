package zusass.game.stat;

import zgame.stat.Stat;
import zgame.stat.Stats;
import zgame.stat.ValueStat;

import static zusass.game.stat.ZusassStat.*;

/** The {@link Stat} keeping track of a thing's speed */
public class MoveSpeed extends Stat{
	
	/**
	 * Create a new {@link ValueStat}
	 *
	 * @param stats See {@link #stats}
	 */
	public MoveSpeed(Stats stats){
		super(stats, MOVE_SPEED, ENDURANCE);
	}
	
	@Override
	public double calculateValue(){
		return this.getOther(ENDURANCE) * 0.1;
	}
}