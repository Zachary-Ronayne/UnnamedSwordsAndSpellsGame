package zusass.game.status;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.core.utils.NotNullList;
import zgame.stat.Stat;
import zgame.stat.StatType;
import zgame.stat.modifier.StatModifier;
import zgame.stat.modifier.TypedModifier;
import zgame.stat.status.StatusEffect;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.List;

/** A {@link StatusEffect} which modifies one or many {@link Stat}s */
public class StatEffect extends StatusEffect{
	
	/** The json key storing {@link #duration} */
	public static final String DURATION_KEY = "duration";
	/** The json key storing {@link #modifiers} */
	public static final String MODS_KEY = "mods";
	
	/** All the stat modifiers applied by this effect */
	private NotNullList<TypedModifier> modifiers;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public StatEffect(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(0);
		this.load(e);
	}
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param duration The duration of the effect
	 * @param statType The stat to effect
	 */
	public StatEffect(double duration, StatModifier mod, StatType<?> statType){
		this(duration, new NotNullList<>(new TypedModifier(mod, statType)));
	}
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param duration The duration of the effect
	 * @param modifiers The modifiers to apply during the effect
	 */
	public StatEffect(double duration, TypedModifier... modifiers){
		this(duration, new NotNullList<>(modifiers));
	}
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param duration The duration of the effect
	 * @param modifiers The modifiers to apply during the effect
	 */
	public StatEffect(double duration, NotNullList<TypedModifier> modifiers){
		super(duration);
		this.modifiers = modifiers;
	}
	
	/** @return See {@link #modifiers} */
	public List<TypedModifier> getModifiers(){
		return this.modifiers;
	}
	
	@Override
	public void apply(String sourceId, ZusassMob mob){
		for(var m : this.modifiers) mob.getStats().get(m.getId()).addModifier(sourceId, m.modifier());
	}
	
	@Override
	public void clear(String sourceId, ZusassMob mob){
		for(var m : this.modifiers) mob.getStats().get(m.getId()).removeModifier(sourceId, m.modifier());
	}
	
	@Override
	public StatusEffect copy(){
		var modifiersCopy = new NotNullList<TypedModifier>();
		for(var m : this.modifiers) modifiersCopy.add(m.copy());
		return new StatEffect(this.getDuration(), modifiersCopy);
	}
	
	@Override
	public double getCost(){
		// This is a very arbitrary calculation atm
		// Basically bigger numbers mean higher cost
		// Should make positive and negative effects cancel the cost out, i.e. a speed spell that also damages strength should cost less than if it only granted speed
		double totalCost = 0;
		for(var m : this.getModifiers()){
			var mod = m.modifier();
			var modType = mod.getType();
			double base = modType.getValue();
			var modValue = switch(modType){
				case ADD, MULT_ADD -> m.modifier().getValue();
				case MULT_MULT -> {
					var v = m.modifier().getValue();
					if(v > 1) yield v - 1;
					yield 1 - v;
				}
			};
			var buff = switch(modType){
				case ADD, MULT_ADD -> modValue > 0;
				case MULT_MULT -> modValue > 1;
			};
			var stat = ((ZusassStat)m.type());
			totalCost += Math.abs(base * modValue) * (buff ? stat.getBuffValue() : stat.getDebuffValue());
		}
		return totalCost * this.getDuration() * .5;
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(DURATION_KEY, this.getDuration());
		Saveable.saveArr(MODS_KEY, e, this.modifiers);
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var arr = Saveable.arr(MODS_KEY, e);
		this.modifiers = new NotNullList<>();
		for(var m : arr) this.modifiers.add(new TypedModifier(m));
		this.setDuration(Saveable.d(DURATION_KEY, e, 0));
		return true;
	}
}
