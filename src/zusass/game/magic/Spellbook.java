package zusass.game.magic;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;

import java.util.ArrayList;

/** An object tracking of all the spells that something can cast */
public class Spellbook implements Saveable{
	
	/** The json key used to store which spell is selected */
	public final static String SELECTED_KEY = "selected";
	/** The json key used to store the spells which this book has */
	public final static String SPELLS_KEY = "spells";
	/** The json key used to store the type of spell */
	public final static String CAST_TYPE_KEY = "castType";
	/** The json key used to store the spell itself */
	public final static String SPELL_KEY = "spell";
	
	/** The spells known to this spellbook */
	private ArrayList<Spell> spells;
	
	/** The current spell selected to be cast */
	private Spell selectedSpell;
	
	/** The selected index in {@link #spells} */
	private int selectedSpellIndex;
	
	/**
	 * Load a spellbook from the given json
	 *
	 * @param e The json to load from
	 */
	public Spellbook(JsonElement e){
		this.load(e);
	}
	
	/** Create a new blank spellbook */
	public Spellbook(){
		this.spells = new ArrayList<>();
		this.selectedSpell = new NoneSpell();
		this.selectedSpellIndex = 0;
	}
	
	/** @return See {@link #spells}. This is the list storing the spells itself. Do not directly modify this list */
	public ArrayList<Spell> getSpellList(){
		return this.spells;
	}
	
	/** @param spell The spell to add to the spellbook, and to select */
	public void addAndSelectSpell(Spell spell){
		this.addSpell(spell);
		// Select the last spell in the list, i.e. the one that was just added
		this.setSelectedSpellIndex(-1);
	}
	
	/** @param spell The spell to add to the spellbook */
	public void addSpell(Spell spell){
		this.spells.add(spell);
	}
	
	/**
	 * @param index The index of the spell to add to remove
	 * @return The removed spell, or null if the spell could not be removed
	 */
	public Spell removeSpell(int index){
		if(index < 0 || index > this.spells.size()) return null;
		return this.spells.remove(index);
	}
	
	/** @return See {@link #selectedSpell} */
	public Spell getSelectedSpell(){
		return this.selectedSpell;
	}
	
	/** Go to the next spell in the spellbook */
	public void nextSpell(){
		this.setSelectedSpellIndex(this.selectedSpellIndex + 1);
	}
	
	/** Go to the previous spell in the spellbook */
	public void previousSpell(){
		this.setSelectedSpellIndex(this.selectedSpellIndex - 1);
	}
	
	/** @return See {@link #selectedSpellIndex} */
	public int getSelectedSpellIndex(){
		return this.selectedSpellIndex;
	}
	
	/**
	 * Go to the given spell index in the spellbook
	 *
	 * @param index The index to go to.
	 * 		If this value is less than 0, the selected spell will be last in the list.
	 * 		If this value is the size of the spellbook or greater, the selected spell will be the first.
	 * 		If this spellbook contains no spells, a {@link NoneSpell} will be selected
	 */
	public void setSelectedSpellIndex(int index){
		var size = this.spells.size();
		if(index < 0) index = size - 1;
		else if(index >= size) index = 0;
		this.selectedSpellIndex = index;
		if(size == 0) this.selectedSpell = new NoneSpell();
		else this.selectedSpell = this.spells.get(this.selectedSpellIndex);
	}
	
	@Override
	public boolean save(JsonElement e){
		var arr = Saveable.newArr(SPELLS_KEY, e);
		
		for(var s : this.spells){
			var spell = Saveable.newObj(arr);
			spell.addProperty(CAST_TYPE_KEY, SpellCastType.name(s.getClass()));
			Saveable.save(SPELL_KEY, spell, s);
		}
		var obj = e.getAsJsonObject();
		obj.addProperty(SELECTED_KEY, this.selectedSpellIndex);
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var spells = Saveable.arr(SPELLS_KEY, e);
		this.spells = new ArrayList<>();
		for(var s : spells) this.spells.add(Saveable.obj(CAST_TYPE_KEY, SpellCastType.class, SPELL_KEY, s, SpellCastType.NONE));
		this.setSelectedSpellIndex(Saveable.i(SELECTED_KEY, e, 0));
		return true;
	}
	
}
