package zgame.stat;

/** A variation on {@link RangeStat} that can automatically regenerates or decreases its value over time */
public abstract class RegenStat extends RangeStat{
	
	/** The current rate at which this stat should regenerate per second. Can be negative to decrease instead */
	private double regen;
	
	/** true if {@link #regen} needs to be recalculated before being used, false otherwise */
	private boolean recalculateRegen;
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param value See {@link #value}
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	public RegenStat(double value, Stats stats, StatType type, StatType... dependents){
		super(value, stats, type, dependents);
		this.recalculateRegen = true;
	}
	
	/** @return See {@link #regen} */
	public double getRegen(){
		if(this.recalculateRegen){
			this.regen = this.calculateRegen();
			this.recalculateRegen = false;
		}
		return this.regen;
	}
	
	/** @return The current value for see {@link #regen} */
	public abstract double calculateRegen();
	
	/**
	 * Apply the current regeneration to this stat
	 *
	 * @param dt The number of seconds the regeneration should apply for
	 */
	@Override
	public void tick(double dt){
		if(this.getRegen() == 0) return;
		this.addValue(this.getRegen() * dt);
	}
	
	@Override
	public void flagRecalculate(){
		super.flagRecalculate();
		this.recalculateRegen = true;
	}
}
