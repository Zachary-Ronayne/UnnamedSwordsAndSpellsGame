package zusass.menu.player;

import zgame.menu.MenuHolder;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;

/** An object holding the list of stats to display */
public class StatList extends MenuHolder{
	
	/** The space between each stat thing in the list */
	public static double TEXT_SPACE = 30;
	
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
		double baseY = 0;
		double y = baseY;
		y = this.addTextThing(zgame, "Resources:", y);
		y = this.addResourceStat(zgame,"Health", ZusassStat.HEALTH, ZusassStat.HEALTH_MAX, ZusassStat.HEALTH_REGEN, y);
		y = this.addResourceStat(zgame,"Stamina", ZusassStat.STAMINA, ZusassStat.STAMINA_MAX, ZusassStat.STAMINA_REGEN, y);
		y = this.addResourceStat(zgame,"Mana", ZusassStat.MANA, ZusassStat.MANA_MAX, ZusassStat.MANA_REGEN, y);
		y = this.addTextSpaceThing(zgame, y);
		
		y = this.addTextThing(zgame, "Attributes:", y);
		y = this.addAttributeStat(zgame, "Strength", ZusassStat.STRENGTH, y);
		y = this.addAttributeStat(zgame, "Endurance", ZusassStat.ENDURANCE, y);
		y = this.addAttributeStat(zgame, "Intelligence", ZusassStat.INTELLIGENCE, y);
		y = this.addTextSpaceThing(zgame, y);
		
		y = this.addTextThing(zgame, "Attack", y);
		y = this.addAttributeStat(zgame, "Damage", ZusassStat.ATTACK_DAMAGE, y);
		y = this.addAttributeStat(zgame, "Speed", ZusassStat.ATTACK_SPEED, y);
		y = this.addAttributeStat(zgame, "Range", ZusassStat.ATTACK_RANGE, y);
		y = this.addTextSpaceThing(zgame, y);
		
		y = this.addTextThing(zgame, "Misc", y);
		y = this.addAttributeStat(zgame, "Move Speed", ZusassStat.MOVE_SPEED, y);
		
		this.setHeight(y - baseY);
		
		this.regenerateText(zgame, mob);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param zgame The game using this list
	 * @param baseName The name for the base of the attribute
	 * @param stat The stat to display
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	private double addAttributeStat(ZusassGame zgame, String baseName, ZusassStat stat, double y){
		return this.addTextThing(new AttributeListItem(this, baseName, stat, zgame), null, y);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param zgame The game using this list
	 * @param baseName The name for the base of the resource
	 * @param current The stat holding the current value of the stat
	 * @param max The stat holding the max value of the stat
	 * @param regen The stat holding the regeneration value of the stat
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	private double addResourceStat(ZusassGame zgame, String baseName, ZusassStat current, ZusassStat max, ZusassStat regen, double y){
		return this.addTextThing(new ResourceListItem(this, baseName, current, max, regen, zgame), null, y);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param zgame The game using this list
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	private double addTextSpaceThing(ZusassGame zgame, double y){
		return this.addTextThing(zgame, "", y);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param zgame The game using this list
	 * @param baseString The base text to use to display the thing
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	private double addTextThing(ZusassGame zgame, String baseString, double y){
		return this.addTextThing(new StatListItem(this,null, zgame), baseString, y);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param item The item to display
	 * @param baseString The base text to use to display the thing
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	private double addTextThing(StatListItem item, String baseString, double y){
		if(baseString != null) item.setText(baseString);
		item.setRelY(y);
		this.textList.add(item);
		this.addThing(item);
		return y + TEXT_SPACE;
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
	
	/** @return See {@link StatsMenu#displayDecimals} */
	public boolean isDisplayDecimals(){
		return this.menu.isDisplayDecimals();
	}
	
}
