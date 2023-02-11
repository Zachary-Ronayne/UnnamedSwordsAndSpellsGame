package zusass.game.stat;

import zgame.stat.Stat;
import zgame.stat.Stats;
import zgame.stat.ValueStat;
import zusass.game.things.entities.mobs.ZusassMob;

import static zusass.game.stat.ZusassStat.*;

/** The {@link Stat} keeping track of a thing's speed */
public class MoveSpeed extends ValueStat{
	
	// issue#24
	
	/** The {@link ZusassMob} using this stat */
	private final ZusassMob mob;
	
	/**
	 * Create a new {@link ValueStat}
	 *
	 * @param value See {@link #value}
	 * @param stats See {@link #stats}
	 * @param mob See {@link #mob}
	 */
	public MoveSpeed(double value, Stats stats, ZusassMob mob){
		super(value, stats, MOVE_SPEED);
		this.mob = mob;
		this.setInstantRecalculate(true);
	}
	
	@Override
	public void recalculate(){
		super.recalculate();
		var v = this.get();
		this.mob.getWalk().setWalkSpeedMax(v);
		this.mob.getWalk().setWalkAcceleration(v * 7);
		this.mob.getWalk().setWalkStopFriction(v / 30);
	}
}