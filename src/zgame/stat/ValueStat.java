package zgame.stat;

/** A {@link Stat} which holds a value */
public class ValueStat extends Stat{
	
	/** The current value of this stat */
	private double value;
	
	/**
	 * Create a new {@link ValueStat}
	 *
	 * @param value See {@link #value}
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	public ValueStat(double value, Stats stats, StatType<?> type, StatType<?>... dependents){
		super(stats, type, dependents);
		this.value = value;
	}
	
	/** @return See {@link #value} */
	public double getValue(){
		return this.value;
	}
	
	/** @param value See {@link #value} */
	public void setValue(double value){
		this.value = value;
		super.setValue(value);
	}
	
	@Override
	public void addValue(double value){
		super.addValue(value);
		this.setValue(this.getValue() + value);
	}
	
	@Override
	public double calculateValue(){
		return this.getValue();
	}
}
