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
		// TODO make headers a bigger font
		// TODO abstract these out to be more concise
		double y = 0;
		y = this.addTextThing(new StatListItem(null, zgame), "Resources:", y);
		y = this.addTextThing(new ResourceListItem("Health", ZusassStat.HEALTH, ZusassStat.HEALTH_MAX, ZusassStat.HEALTH_REGEN, zgame), null, y);
		y = this.addTextThing(new ResourceListItem("Stamina", ZusassStat.STAMINA, ZusassStat.STAMINA_MAX, ZusassStat.STAMINA_REGEN, zgame), null, y);
		y = this.addTextThing(new ResourceListItem("Mana", ZusassStat.MANA, ZusassStat.MANA_MAX, ZusassStat.MANA_REGEN, zgame), null, y);
		y = this.addTextThing(new StatListItem(null, zgame), "", y);
		
		y = this.addTextThing(new StatListItem(null, zgame), "Attributes:", y);
		y = this.addTextThing(new AttributeListItem("Strength", ZusassStat.STRENGTH, zgame), null, y);
		y = this.addTextThing(new AttributeListItem("Endurance", ZusassStat.ENDURANCE, zgame), null, y);
		y = this.addTextThing(new AttributeListItem("Intelligence", ZusassStat.INTELLIGENCE, zgame), null, y);
		y = this.addTextThing(new StatListItem(null, zgame), "", y);
		
		y = this.addTextThing(new StatListItem(null, zgame), "Attack:", y);
		y = this.addTextThing(new AttributeListItem("Damage", ZusassStat.ATTACK_DAMAGE, zgame), null, y);
		y = this.addTextThing(new AttributeListItem("Speed", ZusassStat.ATTACK_SPEED, zgame), null, y);
		y = this.addTextThing(new AttributeListItem("Range", ZusassStat.ATTACK_RANGE, zgame), null, y);
		y = this.addTextThing(new StatListItem(null, zgame), "", y);
		
		y = this.addTextThing(new StatListItem(null, zgame), "Misc:", y);
		this.addTextThing(new AttributeListItem("Move Speed", ZusassStat.MOVE_SPEED, zgame), null, y);
		
		this.regenerateText(zgame, mob);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param item The item to display
	 * @param baseString The base text to use to display the thing
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	public double addTextThing(StatListItem item, String baseString, double y){
		if(baseString != null) item.setText(baseString);
		item.setRelY(y);
		this.textList.add(item);
		this.addThing(item);
		return y + 30;
	}
	
	/**
	 * Regenerate the state of the text on the stats
	 * @param zgame The game to update the list
	 * @param mob The mob to get stats from
	 */
	public void regenerateText(ZusassGame zgame, ZusassMob mob){
		if(mob == null) return;
		
		// Loop through each text thing and update it
		for(var t : this.textList) t.updateText(mob);
	}
	
	/** @return See {@link #menu} */
	public StatsMenu getMenu(){
		return this.menu;
	}
	
}
