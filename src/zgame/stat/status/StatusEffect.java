package zgame.stat.status;

import zgame.core.Game;

/** Keeps track of an effect that does something to a thing, potentially for a set amount of time */
public abstract class StatusEffect {

	/** The number of seconds this effect should last for. Negative values means the effect lasts forever  */
	private double duration;

	/** The number of seconds remaining in this effect*/
	private double remaining;
	
	/**
	 * Create a new {@link StatusEffect}
	 * @param duration The maximum and current duration of this effect
	 */
	public StatusEffect(double duration){
		this.duration = duration;
		this.remaining = this.duration;
	}
	
	/**
	 * Update the state of this effect, clearing it if it runs out of duration
	 *
	 * @param game The game where this tick happened
	 * @param dt The amount of time that passed in this update
	 * @return true if the effect should be cleared, false otherwise
	 */
	public boolean tick(Game game, double dt){
		if(this.isPermanent()) return false;
		this.remaining -= dt;
		return this.remaining <= 0;
	}
	
	/** @return The same effect as a different object, but with {@link #remaining} set to {@link #duration} */
	public final StatusEffect resetCopy(){
		var effect = copy();
		effect.remaining = this.duration;
		effect.duration = this.duration;
		return effect;
	}
	
	/** @return A copy of this effect, but as a separate object */
	public abstract StatusEffect copy();
	
	/** Called when this effect is applied to its owner */
	public abstract void apply();
	
	/** Called when this effect has expired and should be removed from its owner */
	public abstract void clear();
	
	/** @return See {@link #duration} */
	public double getDuration(){
		return this.duration;
	}
	
	/** @return See {@link #remaining} */
	public double getRemaining(){
		return this.remaining;
	}
	
	/** @return true if this effect lasts forever, false otherwise */
	public boolean isPermanent(){
		return this.duration < 0;
	}
	
}
