package zgame.stat;

public class RangeValueStat extends RangeStat{
	
	/** The minimum value for this stat */
	private double min;
	/** The maximum value for this stat */
	private double max;
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param value See {@link #value}
	 * @param min See {@link #min}
	 * @param max See {@link #max}
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	public RangeValueStat(double value, double min, double max, Stats stats, StatType type, StatType... dependents){
		super(value, stats, type, dependents);
		this.setMin(min);
		this.setMax(max);
	}
	
	/** @return See {@link #min} */
	public double getMin(){
		return this.min;
	}
	
	/** @param min See {@link #min} */
	public void setMin(double min){
		this.min = min;
		this.keepInRange();
	}
	
	/** @return See {@link #max} */
	public double getMax(){
		return this.max;
	}
	
	/** @param max See {@link #max} */
	public void setMax(double max){
		this.max = max;
		this.keepInRange();
	}
	
}
