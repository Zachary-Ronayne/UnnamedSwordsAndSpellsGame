package zusass.game.status;

import zgame.stat.Stat;
import zgame.stat.StatType;
import zgame.stat.Stats;
import zgame.stat.modifier.StatModifier;
import zgame.stat.modifier.TypedModifier;
import zgame.stat.status.StatusEffect;

import java.util.List;

/** A {@link StatusEffect} which modifies one or many {@link Stat}s */
public class StatEffect extends StatusEffect{
	
	/** All the stat modifiers applied by this effect */
	private final List<TypedModifier> modifiers;
	
	/** The {@link Stats} which this effect modifies */
	private final Stats stats;
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param stats See {@link #stats}
	 * @param duration The duration of the effect
	 * @param statType The stat to effect
	 */
	public StatEffect(Stats stats, double duration, StatModifier mod, StatType statType){
		this(stats, duration, List.of(new TypedModifier(mod, statType)));
	}
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param stats See {@link #stats}
	 * @param duration The duration of the effect
	 * @param modifiers The modifiers to apply during the effect
	 */
	public StatEffect(Stats stats, double duration, List<TypedModifier> modifiers){
		super(duration);
		this.stats = stats;
		this.modifiers = modifiers;
	}
	
	@Override
	public void apply(){
		for(var m : modifiers) this.stats.get(m.getOrdinal()).addModifier(m.modifier());
	}
	
	@Override
	public void clear(){
		for(var m : modifiers) this.stats.get(m.getOrdinal()).removeModifier(m.modifier());
	}
}
