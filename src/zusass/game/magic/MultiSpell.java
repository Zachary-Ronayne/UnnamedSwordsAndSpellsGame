package zusass.game.magic;

import com.google.gson.JsonElement;
import zgame.core.utils.NotNullList;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.magic.effect.SpellEffectNone;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link Spell} which causes multiple spells to happen at the same time */
public class MultiSpell extends Spell{
	
	// TODO finish implementation
	
	/** The spells to cast when this multi spell is cast */
	private NotNullList<Spell> spells;
	
	/** Load a new object from the json element */
	public MultiSpell(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(e);
	}
	
	/** */
	public MultiSpell(NotNullList<SpellEffect> effects){
		super(new SpellEffectNone());
	}
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){
	
	}
	
	@Override
	public double getCost(){
		return super.getCost();
	}
	
	@Override
	public boolean save(JsonElement e){
		return super.save(e);
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		return super.load(e);
	}
}
