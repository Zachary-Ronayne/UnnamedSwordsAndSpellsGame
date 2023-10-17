package zusass.menu.player;

import zgame.core.Game;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZArrayUtils;
import zgame.menu.format.PercentFormatter;
import zgame.stat.Stat;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.menu.comp.ZusassButton;

import java.text.DecimalFormat;

/** An item holding text for the {@link StatList} */
public class StatListItem extends ZusassButton{
	
	/** A formatter used to display stats with decimals places for these items */
	public static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("0.00");
	/** A formatter used to display stats without decimals places for these items */
	public static final DecimalFormat INT_FORMATTER = new DecimalFormat("0");
	
	/** The color for the fill of the stat backgrounds */
	public static final ZColor FILL_COLOR = new ZColor(1);
	/** The color for the border of the stat rectangle */
	public static final ZColor BORDER_COLOR = new ZColor(0);
	/** The text color for stats which are not modified */
	public static final ZColor BASE_TEXT_COLOR = new ZColor(0);
	/** The text color for buffed stats */
	public static final ZColor BUFF_TEXT_COLOR = new ZColor(0, .5, 0);
	/** The text color for debuffed stats */
	public static final ZColor DEBUFF_TEXT_COLOR = new ZColor(1, 0, 0);
	/** The height of a stat list item */
	public static final double HEIGHT = 26;
	
	/** The stat which should be displayed on this text thing, or null if the text should not be updated by a stat */
	private final ZusassStat statType;
	
	/** The stat list holding this item */
	private final StatList statList;
	
	/** A short bit of text explaining what this stat does, or null if none exists */
	private String description;
	
	/**
	 * Create a new stat item
	 *
	 * @param size The size of this item
	 * @param statType See {@link #statType}
	 * @param zgame The game to use to create the item
	 */
	public StatListItem(double size, StatList statList, ZusassStat statType, ZusassGame zgame){
		super(0, 0, 1, size, "", zgame);
		this.statList = statList;
		this.statType = statType;
		this.setFormatter(new PercentFormatter(1.0, null, 0.5, null));
		
		this.setFill(FILL_COLOR);
		this.setBorderWidth(1);
		this.setBorder(BORDER_COLOR);
		this.bufferWidthToWindow(zgame);
	}
	
	/**
	 * Update the text of this stat
	 *
	 * @param mob The mob to get the stat from
	 */
	public void updateText(ZusassMob mob){
		this.updateTextOptions(mob);
		this.centerTextVertical();
		
		// Update x position
		this.alignTextXLeft(4);
	}
	
	/** @return See {@link #statType} */
	public ZusassStat getStatType(){
		return this.statType;
	}
	
	/**
	 * Based on the given mob, update the text options to display for this item based on the mob's stats
	 *
	 * @param mob The mob to get stats from
	 */
	public void updateTextOptions(ZusassMob mob){
		var type = this.getStatType();
		if(type == null) return;
		
		this.getTextBuffer().setOptions(ZArrayUtils.singleList(makeTextOption(mob.getStat(type))));
	}
	
	/**
	 * Generate the text option for a specific stat
	 *
	 * @param stat The stat to make a text option for
	 * @return The text option
	 */
	public TextOption makeTextOption(Stat stat){
		var text = (this.statList.isDisplayDecimals() ? DECIMAL_FORMATTER : INT_FORMATTER).format(stat.get());
		ZColor color;
		
		// Update color
		if(stat.buffed()) color = BUFF_TEXT_COLOR;
		else if(stat.debuffed()) color = DEBUFF_TEXT_COLOR;
		else color = BASE_TEXT_COLOR;
		return new TextOption(text, color);
	}
	
	/** @return See {@link #description} */
	public String getDescription(){
		return this.description;
	}
	
	/** @param description See {@link #description} */
	public void setDescription(String description){
		this.description = description;
	}
	
	@Override
	public void mouseEnter(Game game){
		super.mouseEnter(game);
		// When the mouse moves to this item, select this stat
		this.statList.setSelectedStat(this);
	}
	
	@Override
	public void mouseExit(Game game){
		super.mouseExit(game);
		// If the mouse moves away from this item and this item is currently selected, deselect it
		if(this.statList.getSelectedStat() == this) this.statList.setSelectedStat(null);
	}
}
