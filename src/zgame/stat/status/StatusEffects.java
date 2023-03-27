package zgame.stat.status;

import zgame.core.Game;
import zgame.core.utils.NotNullList;
import zusass.game.things.entities.mobs.ZusassMob;

/** Keeps track of all the {@link StatusEffect}s applying to a thing */
public class StatusEffects{
	
	/** The current effects applied in this list */
	private final NotNullList<StatusEffect> effects;
	
	/** Initialize an empty status effects list */
	public StatusEffects(){
		this.effects = new NotNullList<>();
	}
	
	/** @return See {@link #effects} */
	public NotNullList<StatusEffect> get(){
		return this.effects;
	}
	
	/**
	 * @param effect An effect to add to this effects list, and apply it to the given mob
	 * @param mob The mob to add the effect to
	 */
	public void addEffect(StatusEffect effect, ZusassMob mob){
		effect = effect.resetCopy();
		this.effects.add(effect);
		effect.apply(mob);
	}
	
	/**
	 * @param effect An effect to remove from this effects list, and clear it from its owner
	 * @param mob The mob to remove the effect from
	 */
	public void removeEffect(StatusEffect effect, ZusassMob mob){
		this.effects.remove(effect);
		effect.clear(mob);
	}
	
	/**
	 * @param i The index of the effect to remove from this effects list, and clear it from its owner
	 * @param mob The mob to remove the effect from
	 */
	public void removeEffect(int i, ZusassMob mob){
		var effect = this.effects.remove(i);
		effect.clear(mob);
	}
	
	/**
	 * Clear every non-permanent status effect from the mob
	 * @param mob The mob to remove effects from
	 */
	public void removeAllTemporary(ZusassMob mob){
		for(int i = 0; i < this.effects.size(); i++){
			var e = this.effects.get(i);
			if(!e.isPermanent()){
				e.clear(mob);
				this.effects.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Update the state of {@link #effects}, clearing them when they run out of duration
	 *
	 * @param game The game where this tick happened
	 * @param dt The amount of time that passed in this update
	 * @param mob The mob to update the state of the effect by
	 */
	public void tick(Game game, double dt, ZusassMob mob){
		for(int i = 0; i < this.effects.size(); i++){
			var ef = this.effects.get(i);
			var removed = ef.tick(game, dt);
			if(removed){
				this.removeEffect(i, mob);
				i--;
			}
		}
	}
	
}
