package zgame.stat;

/** A {@link Stat} with a minimum and maximum value */
public abstract class RangeStat extends ValueStat{
	
	/** The current minimum value for this stat */
	private double min;
	/** The current maximum value for this stat */
	private double max;
	
	/** true if {@link #min} needs to be recalculated before being used, false otherwise */
	private boolean recalculateMin;
	/** true if {@link #max} needs to be recalculated before being used, false otherwise */
	private boolean recalculateMax;
	
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
		this.recalculateMin = true;
		this.recalculateMax = true;
	}
	
	// TODO need a way to update min and max for max health whenever strength is updated
	
	/** @return See {@link #min} */
	public double getMin(){
		if(this.recalculateMin){
			this.min = this.calculateMin();
			super.setValue(Math.max(this.min, this.getValue()));
			this.recalculateMin = false;
		}
		return this.min;
	}
	
	/** @return The current minimum value for this stat */
	public abstract double calculateMin();
	
	/** @return See {@link #max} */
	public double getMax(){
		if(this.recalculateMax){
			this.max = this.calculateMax();
			super.setValue(Math.min(this.max, this.getValue()));
			this.recalculateMax = false;
		}
		return this.max;
	}
	
	/** @return The current maximum value for this stat */
	public abstract double calculateMax();
	
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
	public double calculateValue(){
		return this.keepInRange(super.calculateValue());
	}
	
	@Override
	public void flagRecalculate(){
		super.flagRecalculate();
		this.recalculateMin = true;
		this.recalculateMax = true;
	}
}
