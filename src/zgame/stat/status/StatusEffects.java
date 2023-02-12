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
	
	/** @param effect An effect to add to this effects list, and apply it to its owner */
	public void addEffect(StatusEffect effect){
		this.effects.add(effect);
		effect.apply();
	}
	
	/** @param effect An effect to remove from this effects list, and clear it from its owner */
	public void removeEffect(StatusEffect effect){
		this.effects.remove(effect);
		effect.clear();
	}
	
	/**
	 * Update the state of {@link #effects}, clearing them when they run out of duration
	 *
	 * @param game The game where this tick happened
	 * @param dt The amount of time that passed in this update
	 */
	public void tick(Game game, double dt){
		for(int i = 0; i < this.effects.size(); i++){
			this.effects.get(i).tick(game, dt);
		}
	}
	
}
