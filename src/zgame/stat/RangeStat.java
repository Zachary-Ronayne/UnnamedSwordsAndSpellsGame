package zgame.stat;

/** A {@link Stat} with a minimum and maximum value */
public abstract class RangeStat extends ValueStat{
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param value See {@link #value}
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	public RangeStat(double value, Stats stats, StatType type, StatType... dependents){
		super(value, stats, type, dependents);
	}
	
	/** @return The minimum value for this stat */
	public abstract double getMin();
	
	/** @return The maximum value for this stat */
	public abstract double getMax();
	
	/**
	 * Keep the given value in the range of this {@link RangeStat}
	 * @param value The value
	 * @return The value in the range
	 */
	public double keepInRange(double value){
		return Math.max(this.getMin(), Math.min(this.getMax(), value));
	}
	
	/** Ensure that {@link #value} stays between {@link #getMin()} and {@link #getMax()} */
	public void keepInRange(){
		this.setValue(this.getValue());
	}
	
	@Override
	public void setValue(double value){
		super.setValue(this.keepInRange(value));
	}
	
	@Override
	public final double calculateValue(){
		return this.keepInRange(super.calculateValue());
	}
}
