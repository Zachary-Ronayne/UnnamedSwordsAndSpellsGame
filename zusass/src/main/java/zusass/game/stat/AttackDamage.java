package zusass.game.stat;

import zgame.stat.Stat;
import zgame.stat.Stats;
import zusass.game.things.entities.mobs.ZusassMob;

import static zusass.game.stat.ZusassStat.*;

/** A stat keeping track of the amount of damage a {@link ZusassMob} does with a melee attack*/
public class AttackDamage extends Stat{
	
	/**
	 * Create the stat for holding max health
	 * @param stats The {@link Stats} which use this stat
	 */
	public AttackDamage(Stats stats){
		super(stats, ATTACK_DAMAGE, STRENGTH, STAMINA, STAMINA_MAX);
	}
	
	@Override
	public double calculateValue(){
		return this.getOther(STRENGTH) * 2 * (0.5 + 0.5 * (this.getOther(STAMINA) / this.getOther(STAMINA_MAX)));
	}
}
