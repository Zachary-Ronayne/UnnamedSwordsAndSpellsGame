package zusass.game.magic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zgame.core.file.Saveable;
import zgame.core.utils.NotNullList;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffectNone;
import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link Spell} which causes multiple spells to happen at the same time */
public class MultiSpell extends Spell{
	
	/** The json key for saving the list of spells */
	private static final String SPELLS_KEY = "spells";
	/** The json key for saving the type of spell */
	private static final String CAST_TYPE_KEY = "castType";
	/** The json key for saving the spell itself */
	private static final String SPELL_KEY = "spell";
	
	/** The spells to cast when this multi spell is cast */
	private NotNullList<Spell> spells;
	
	/** Load a new object from the json element */
	public MultiSpell(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(e);
	}
	
	/**
	 * Create a spell which casts multiple other spells at the same time
	 *
	 * @param spells The spells to place in {@link #spells}
	 */
	public MultiSpell(Spell... spells){
		this(new NotNullList<>(spells));
	}
	
	/**
	 * Create a spell which casts multiple other spells at the same time
	 *
	 * @param spells See {@link #spells}
	 */
	public MultiSpell(NotNullList<Spell> spells){
		// Multi spells don't do anything on their own, the spells object is what casts the spells
		super(new SpellEffectNone());
		this.spells = spells;
	}
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){
		for(var s : spells) s.cast(zgame, caster);
	}
	
	@Override
	public double getCost(){
		var totalCost = 0;
		for(var s : this.spells) totalCost += s.getCost();
		return totalCost;
	}
	
	@Override
	public boolean save(JsonElement e){
		// Don't need to call super, none of that information is needed for a multi spell, just save the name
		var obj = e.getAsJsonObject();
		obj.addProperty(NAME_KEY, this.getName());
		
		var arr = Saveable.newArr(SPELLS_KEY, e);
		for(var s : this.spells){
			obj = new JsonObject();
			arr.add(obj);
			
			obj.addProperty(CAST_TYPE_KEY, SpellCastType.name(s.getClass()));
			Saveable.save(SPELL_KEY, obj, s);
		}
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		// Don't need to call super, none of that information is needed for a multi spell, just save the name
		this.setName(Saveable.s(Spell.NAME_KEY, e, "spell"));
		
		var arr = Saveable.arr(SPELLS_KEY, e);
		this.spells = new NotNullList<>();
		for(var s : arr) this.spells.add(Saveable.obj(CAST_TYPE_KEY, SpellCastType.class, SPELL_KEY, s, SpellCastType.NONE));
		return true;
	}
}
