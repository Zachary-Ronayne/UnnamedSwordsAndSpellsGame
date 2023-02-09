package zusass.game.stat;

/** A {@link Stat} with a minimum and maximum value */
public class RangeStat extends Stat{
	
	/** The minimum value for this stat */
	private double min;
	/** The maximum value for this stat */
	private double max;
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param value See {@link #value}
	 */
	public RangeStat(double value, double min, double max){
		super(value);
		this.setMin(min);
		this.setMax(max);
	}
	
	/** Ensure {@link #value} does not exceed {@link #max} and does not go below {@link #min} */
	private void keepInRange(){
		super.setValue(Math.max(this.getMin(), Math.min(this.getMax(), this.getValue())));
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
	
	@Override
	public void setValue(double value){
		super.setValue(value);
		this.keepInRange();
	}
}
