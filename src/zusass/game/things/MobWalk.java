package zusass.game.things;

import zgame.things.entity.Walk;
import zusass.game.things.entities.mobs.ZusassMob;
import static zusass.game.stat.ZusassStat.*;

/** A {@link Walk} for {@link ZusassMob}s */
public class MobWalk extends Walk{
	
	/** The same thing as {@link #thing}, but as a {@link ZusassMob} */
	private final ZusassMob mob;
	
	/**
	 * Make a new {@link MobWalk} with the given entity
	 *
	 * @param thing See {@link #mob}
	 */
	public MobWalk(ZusassMob thing){
		super(thing);
		this.mob = thing;
	}
	
	@Override
	public boolean jump(double dt){
		var jumped = super.jump(dt);
		if(jumped) this.mob.getStat(STAMINA).addValue(-6);
		return jumped;
	}
}
