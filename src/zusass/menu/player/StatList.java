package zusass.menu.player;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.menu.MenuHolder;
import zgame.menu.format.PixelFormatter;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;

/** An object holding the list of stats to display */
public class StatList extends MenuHolder{
	
	/** The space between each stat thing in the list */
	public static double TEXT_SPACE = 33;
	/** The space between each header of the stat things in the list */
	public static double HEADER_SPACE = TEXT_SPACE * 1.3;
	
	/** The menu holding this list */
	private final StatsMenu menu;
	
	/** The list of items storing the stats displayed along with their current menu text objects */
	private final ArrayList<StatListItem> textList;
	
	/** The item in {@link #textList} which is selected to show its description, or null if none are selected */
	private StatListItem selectedStat;
	
	/**
	 * Create the list of stats
	 *
	 * @param menu See {@link #menu}
	 * @param mob The mob to get stats from
	 */
	public StatList(StatsMenu menu, ZusassMob mob){
		super();
		this.menu = menu;
		this.textList = new ArrayList<>();
		this.selectedStat = null;
		this.setWidth(1);
		this.setHeight(1);
		
		this.setFormatter(new PixelFormatter(StatsMenu.BORDER_SIZE * 1.5, StatsMenu.BORDER_SIZE * 2.5, StatsMenu.DRAGGABLE_HEIGHT + StatsMenu.BORDER_SIZE * 3, null));
		this.invisible();
		
		// Generate the list of text things
		double baseY = 0;
		double y = baseY;
		y = this.addTextThing("Resources:", HEADER_SPACE, y,
				"Current value / Max value (Regen per second)");
		y = this.addResourceStat("Health", ZusassStat.HEALTH, ZusassStat.HEALTH_MAX, ZusassStat.HEALTH_REGEN, y,
				"If health reaches zero, you die.");
		y = this.addResourceStat("Stamina", ZusassStat.STAMINA, ZusassStat.STAMINA_MAX, ZusassStat.STAMINA_REGEN, y,
				"How tired you are. Lower stamina means attacks are less effective.");
		y = this.addResourceStat("Mana", ZusassStat.MANA, ZusassStat.MANA_MAX, ZusassStat.MANA_REGEN, y,
				"Used to cast spells. You cannot cast spells if you don't have enough mana for it.");
		y = this.addTextSpaceThing(y);
		
		y = this.addTextThing("Attributes:", HEADER_SPACE, y,
				"Values that govern how effective your actions are.");
		y = this.addAttributeStat("Strength", ZusassStat.STRENGTH, y,
				"Governs attack damage and max health.");
		y = this.addAttributeStat("Endurance", ZusassStat.ENDURANCE, y,
				"Governs max stamina and stamina regen.");
		y = this.addAttributeStat("Intelligence", ZusassStat.INTELLIGENCE, y,
				"Governs max mana and mana regen.");
		y = this.addAttributeStat("Agility", ZusassStat.AGILITY, y,
				"Governs jump power and height.");
		y = this.addTextSpaceThing(y);
		
		y = this.addTextThing("Attack", HEADER_SPACE, y,
				"How effective your attacks are.");
		y = this.addAttributeStat("Damage", ZusassStat.ATTACK_DAMAGE, y,
				"The base damage you do per hit.");
		y = this.addAttributeStat("Speed", ZusassStat.ATTACK_SPEED, y,
				"The number of attacks you can perform in a second.");
		y = this.addAttributeStat("Range", ZusassStat.ATTACK_RANGE, y,
				"The distance away from you that your attacks can hit enemies.");
		y = this.addTextSpaceThing(y);
		
		y = this.addTextThing("Misc", HEADER_SPACE, y,
				"Other stats");
		y = this.addAttributeStat("Move Speed", ZusassStat.MOVE_SPEED, y,
				"The number of units you move per second");
		
		this.setHeight(y - baseY);
		
		this.regenerateText(mob);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param baseName The name for the base of the attribute
	 * @param stat The stat to display
	 * @param y The current y position to place a list item
	 * @param description Some short text describing what the stat does
	 * @return The next y position to place an item
	 */
	private double addAttributeStat(String baseName, ZusassStat stat, double y, String description){
		var size = TEXT_SPACE;
		return this.addTextThing(new AttributeListItem(size, this, baseName, stat), null, size, y, description);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param baseName The name for the base of the resource
	 * @param current The stat holding the current value of the stat
	 * @param max The stat holding the max value of the stat
	 * @param regen The stat holding the regeneration value of the stat
	 * @param y The current y position to place a list item
	 * @param description Some short text describing what the stat does
	 * @return The next y position to place an item
	 */
	private double addResourceStat(String baseName, ZusassStat current, ZusassStat max, ZusassStat regen, double y, String description){
		var size = TEXT_SPACE;
		return this.addTextThing(new ResourceListItem(size, this, baseName, current, max, regen), null, size, y, description);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param y The current y position to place a list item
	 * @return The next y position to place an item
	 */
	private double addTextSpaceThing(double y){
		return this.addTextThing("", TEXT_SPACE * 0.5, y, null);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param baseString The base text to use to display the thing
	 * @param size The size of the thing
	 * @param y The current y position to place a list item
	 * @param description Some short text describing what the stat does
	 * @return The next y position to place an item
	 */
	private double addTextThing(String baseString, double size, double y, String description){
		return this.addTextThing(new StatListItem(size, this,null), baseString, size, y, description);
	}
	
	/**
	 * Add a text thing to this stat list
	 * @param item The item to display
	 * @param baseString The base text to use to display the thing
	 * @param size The size of the thing
	 * @param y The current y position to place a list item
	 * @param description Some short text describing what the stat does
	 * @return The next y position to place an item
	 */
	private double addTextThing(StatListItem item, String baseString, double size, double y, String description){
		if(baseString != null) item.setText(baseString);
		item.setFontSize(size * .8);
		item.setRelY(y);
		item.setDescription(description);
		if("".equals(baseString)) item.invisible();
		this.textList.add(item);
		this.addThing(item);
		return y + size;
	}
	
	/**
	 * Regenerate the state of the text on the stats
	 * @param mob The mob to get stats from
	 */
	public void regenerateText(ZusassMob mob){
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
	
	/** @return See {@link #selectedStat} */
	public StatListItem getSelectedStat(){
		return this.selectedStat;
	}
	
	/** @param selectedStat See {@link #selectedStat} */
	public void setSelectedStat(StatListItem selectedStat){
		this.selectedStat = selectedStat;
	}
	
	@Override
	public void drawThings(Game game, Renderer r, boolean reposition){
		var b = this.menu.getRelBounds();
		b.y += StatsMenu.SCROLLER_POSITION;
		b.height -= StatsMenu.SCROLLER_POSITION;
		r.pushLimitedBoundsIntersection(b);
		super.drawThings(game, r, reposition);
		r.popLimitedBounds();
	}
}
