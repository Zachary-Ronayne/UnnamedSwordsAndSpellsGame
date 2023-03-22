package zgame.stat.status;

import zgame.core.Game;
import zgame.core.utils.NotNullList;

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
	
	/** @param effect An effect to add to this effects list, and apply it to its owner */
	public void addEffect(StatusEffect effect){
		effect = effect.resetCopy();
		this.effects.add(effect);
		effect.apply();
	}
	
	/** @param effect An effect to remove from this effects list, and clear it from its owner */
	public void removeEffect(StatusEffect effect){
		this.effects.remove(effect);
		effect.clear();
	}
	
	/** @param i The index of the effect to remove from this effects list, and clear it from its owner */
	public void removeEffect(int i){
		var effect = this.effects.remove(i);
		effect.clear();
	}
	
	/** Clear every non-permanent status effect from this object's owner */
	public void removeAllTemporary(){
		for(int i = 0; i < this.effects.size(); i++) {
			var e = this.effects.get(i);
			if(!e.isPermanent()){
				e.clear();
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
	 */
	public void tick(Game game, double dt){
		for(int i = 0; i < this.effects.size(); i++){
			var ef = this.effects.get(i);
			var removed = ef.tick(game, dt);
			if(removed){
				this.removeEffect(i);
				i--;
			}
		}
	}
	
}
