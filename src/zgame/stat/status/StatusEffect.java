package zgame.stat.status;

import zgame.core.Game;
import zgame.core.file.Saveable;
import zusass.game.things.entities.mobs.ZusassMob;

/** Keeps track of an effect that does something to a thing, potentially for a set amount of time */
public abstract class StatusEffect implements Saveable{
	
	/** The number of seconds this effect should last for. Negative values means the effect lasts forever */
	private double duration;
	
	/** The number of seconds remaining in this effect */
	private double remaining;
	
	/**
	 * Create a new {@link StatusEffect}
	 *
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
		var effect = this.copy();
		effect.remaining = this.duration;
		effect.duration = this.duration;
		return effect;
	}
	
	/** @return A copy of this effect, but as a separate object */
	public abstract StatusEffect copy();
	
	/**
	 * Called when this effect is applied to a mob
	 *
	 * @param mob The mob to apply the effect to
	 * @param sourceId The id representing whatever originally applied the effect
	 */
	public abstract void apply(String sourceId, ZusassMob mob);
	
	/**
	 * Called when this effect has expired and should be removed from the mob
	 *
	 * @param mob The mob to clear the effect from
	 * @param sourceId The id representing whatever originally applied the effect
	 */
	public abstract void clear(String sourceId, ZusassMob mob);
	
	/** @return See {@link #duration} */
	public double getDuration(){
		return this.duration;
	}
	
	/** @param duration See {@link #duration} */
	public void setDuration(double duration){
		this.duration = duration;
	}
	
	/** @return See {@link #remaining} */
	public double getRemaining(){
		return this.remaining;
	}
	
	/** @param remaining See {@link #remaining} */
	public void setRemaining(double remaining){
		this.remaining = remaining;
	}
	
	/** @return true if this effect lasts forever, false otherwise */
	public boolean isPermanent(){
		return this.duration < 0;
	}
	
	/** @return The amount of mana it costs to cast a spell with this effect */
	public abstract double getCost();
	
}
