package zgame.stat.status;

import zgame.core.utils.NotNullList;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.HashMap;
import java.util.Map;

/** Keeps track of all the {@link StatusEffect}s applying to a thing */
public class StatusEffects{
	
	/** The current effects applied in this list, mapped by the sourceId which applied the effect */
	private final Map<String, NotNullList<StatusEffect>> effects;
	
	/** Initialize an empty status effects list */
	public StatusEffects(){
		this.effects = new HashMap<>();
	}
	
	/** @return See {@link #effects} */
	public Map<String, NotNullList<StatusEffect>> get(){
		return this.effects;
	}
	
	/**
	 * @param effect An effect to add to this effects list, and apply it to the given mob
	 * @param sourceId The id representing whatever originally applied the effect
	 * @param mob The mob to add the effect to
	 */
	public final void addEffect(StatusEffect effect, String sourceId, ZusassMob mob){
		effect = effect.resetCopy();
		var list = this.effects.get(sourceId);
		if(list == null){
			list = new NotNullList<>();
			this.effects.put(sourceId, list);
		}
		list.add(effect);
		effect.apply(sourceId, mob);
	}
	
	/**
	 * @param effect An effect to remove from this effects list, and clear it from its owner. This must be the effect object itself to remove, not a copy.
	 * 		This object can be obtained by reading from {@link #effects}
	 * @param sourceId The id representing whatever originally applied the effect
	 * @param mob The mob to remove the effect from
	 * @return true if the effect was removed, false otherwise
	 */
	public final boolean removeEffect(StatusEffect effect, String sourceId, ZusassMob mob){
		var list = this.effects.get(sourceId);
		if(list == null) return false;
		var removed = list.remove(effect);
		effect.clear(sourceId, mob);
		return removed;
	}
	
	/**
	 * Clear every non-permanent status effect from the mob
	 *
	 * @param mob The mob to remove effects from
	 */
	public void removeAllTemporary(ZusassMob mob){
		var effectList = this.effects.entrySet().stream().toList();
		for(var e : effectList){
			var id = e.getKey();
			var list = e.getValue().stream().toList();
			for(var effect : list){
				if(!effect.isPermanent()) effect.clear(id, mob);
			}
		}
	}
	
	/**
	 * Update the state of {@link #effects}, clearing them when they run out of duration
	 *
	 * @param dt The amount of time that passed in this update
	 * @param mob The mob to update the state of the effect by
	 */
	public void tick(double dt, ZusassMob mob){
		var effectList = this.effects.entrySet().stream().toList();
		for(var e : effectList){
			var id = e.getKey();
			var list = e.getValue().stream().toList();
			for(var effect : list){
				var removed = effect.tick(dt);
				if(removed) this.removeEffect(effect, id, mob);
			}
		}
	}
	
}
