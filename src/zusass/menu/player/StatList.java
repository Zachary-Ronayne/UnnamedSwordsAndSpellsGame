package zusass.menu.player;

import zgame.menu.MenuHolder;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;

/** An object holding the list of stats to display */
public class StatList extends MenuHolder{
	
	/** The menu holding this list */
	private final StatsMenu menu;
	
	/** The list of items storing the stats displayed along with their current menu text objects */
	private final ArrayList<StatListItem> textList;
	
	/**
	 * Create the list of stats
	 *
	 * @param menu See {@link #menu}
	 * @param zgame The game containing this menu
	 * @param mob The mob to get stats from
	 */
	public StatList(StatsMenu menu, ZusassGame zgame, ZusassMob mob){
		super();
		this.menu = menu;
		this.textList = new ArrayList<>();
		
		// TODO abstract out this formatting
		this.setFormatter(new PixelFormatter(StatsMenu.BORDER_SIZE * 1.5, StatsMenu.BORDER_SIZE * 2.5, StatsMenu.DRAGGABLE_HEIGHT + StatsMenu.BORDER_SIZE * 3, null));
		this.invisible();
		
		// Generate the list of text things
		// TODO make the text buffer be the whole screen width like the spell maker menu
		// TODO add all stats
		// TODO add dividers
		// TODO make headers a bigger font
		// TODO abstract these out to be more concise
		this.addTextThing(zgame, null, "Stats:", true, 0);
		
		this.addTextThing(zgame, null, "Health:", false, 30);
		this.addTextThing(zgame, ZusassStat.HEALTH, "", true, 30);
		
		this.addTextThing(zgame, null, "Stamina:", false, 60);
		this.addTextThing(zgame, ZusassStat.STAMINA, "", true, 60);
		
		this.addTextThing(zgame, null, "Mana:", false, 90);
		this.addTextThing(zgame, ZusassStat.MANA, "", true, 90);
		
		this.addTextThing(zgame, null, "Strength:", false, 120);
		this.addTextThing(zgame, ZusassStat.STRENGTH, "", true, 120);
		
		this.addTextThing(zgame, null, "Endurance:", false, 150);
		this.addTextThing(zgame, ZusassStat.ENDURANCE, "", true, 150);
		
		this.addTextThing(zgame, null, "Intelligence:", false, 180);
		this.addTextThing(zgame, ZusassStat.INTELLIGENCE, "", true, 180);
		
		this.regenerateText(zgame, mob);
	}
	
	public void addTextThing(ZusassGame zgame, ZusassStat statType, String baseString, boolean newLine, double y){
		var item = new StatListItem(statType, newLine, zgame);
		item.setText(baseString);
		item.setRelY(y);
		this.textList.add(item);
		this.addThing(item);
	}
	
	/**
	 * Regenerate the state of the text on the stats
	 * @param zgame The game to update the list
	 * @param mob The mob to get stats from
	 */
	public void regenerateText(ZusassGame zgame, ZusassMob mob){
		if(mob == null) return;
		
		// Loop through each text thing and update it
		StatListItem previous = null;
		for(var t : this.textList) {
			t.updateText(previous, mob);
			previous = t.isNewLine() ? null : t;
		}
	}
	
	/** @return See {@link #menu} */
	public StatsMenu getMenu(){
		return this.menu;
	}
	
}
