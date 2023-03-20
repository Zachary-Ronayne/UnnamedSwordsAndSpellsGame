package zusass.game.magic;

import zgame.physics.ZVector;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.projectile.MagicProjectile;

/** A {@link Spell} that creates a projectile when cast */
public class ProjectileSpell extends Spell{
	
	/**
	 * Create a new spell that casts a projectile
	 *
	 * @param effect {@link #effect}
	 */
	public ProjectileSpell(SpellEffect effect){
		super(effect);
	}
	
	// TODO account for projectile size, range, etc for spell cost
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){
		var r = zgame.getCurrentRoom();
		var vel = new ZVector(caster.getAttackDirection(), 400, false);
		var p = new MagicProjectile(caster.centerX(), caster.centerY(), caster.getUuid(), vel, this.getEffect());
		p.addX(-p.getWidth() * .5);
		p.addY(-p.getHeight() * .5);
		r.addThing(p);
	}
}
