package zgame.stat;

import java.util.Arrays;
import java.util.HashSet;

/** An object keeping track of a single stat used an object */
public abstract class Stat{
	
	/** The stats object which is used by this {@link Stat} */
	private final Stats stats;
	
	/** The {@link StatType} identifying this {@link Stats} */
	private final StatType type;
	
	/** The stats that this {@link Stat} uses in calculating itself */
	private final HashSet<StatType> dependents;
	
	/** true if this stat needs to be recalculated before it is used again */
	private boolean recalculate;
	
	/** The value of this stat since it was last calculated */
	private double calculated;
	
	/**
	 * Create a new stat
	 *
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	public Stat(Stats stats, StatType type, StatType... dependents){
		this.recalculate = true;
		this.stats = stats;
		this.type = type;
		this.dependents = new HashSet<>();
		this.dependents.addAll(Arrays.asList(dependents));
	}
	
	/**
	 * Get the current value of a stat in {@link #stats}
	 *
	 * @param type The type of stat to get
	 * @return The value, or 0 if the given stat doesn't exist
	 */
	public double getOther(StatType type){
		var stat = this.stats.get(type);
		if(stat == null) return 0;
		return stat.get();
	}
	
	/** @return See {@link #type} */
	public StatType getType(){
		return this.type;
	}
	
	/** Tell this {@link Stat} that it needs to be recalculated before {@link #calculated} can be used again */
	public void flagRecalculate(){
		this.recalculate = true;
		for(var s : this.dependents){
			this.stats.get(s).flagRecalculate();
		}
	}
	
	/** @return What {@link #calculated} should be based on the current state of {@link #stats} */
	public abstract double calculateValue();
	
	/** @return See {@link #calculated} */
	public double get(){
		if(this.recalculate){
			this.calculated = this.calculateValue();
			this.recalculate = false;
		}
		
		return this.calculated;
	}
	
	/**
	 * @param value The new value for this stat. This method just calls {@link #flagRecalculate()} by default. This will only change a value if the {@link Stat}
	 * 		implementation permits setting the value
	 */
	public void setValue(double value){
		this.flagRecalculate();
	}
	
	/**
	 * @param value The new value to add to the current value of this stat. This method just calls {@link #flagRecalculate()} by default. This will only change a value if
	 * 		the {@link Stat} implementation permits setting the value
	 */
	public void addValue(double value){
		this.flagRecalculate();
	}
	
}
