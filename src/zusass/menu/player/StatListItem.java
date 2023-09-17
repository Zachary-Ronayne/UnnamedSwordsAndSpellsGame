package zusass.menu.player;

import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZArrayUtils;
import zgame.menu.format.PercentFormatter;
import zgame.stat.Stat;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.menu.comp.ZusassMenuText;

import java.text.DecimalFormat;

/** An item holding text for the {@link StatList} */
public class StatListItem extends ZusassMenuText{
	
	/** A formatter used to display text for these items */
	public static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("0.00");
	
	/** The color for the fill of the stat backgrounds */
	public static final ZColor FILL_COLOR = new ZColor(1);
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
	
	/**
	 * Create a new stat item
	 *
	 * @param statType See {@link #statType}
	 * @param zgame The game to use to create the item
	 */
	public StatListItem(ZusassStat statType, ZusassGame zgame){
		super(0, 0, 1, HEIGHT, "", zgame);
		this.statType = statType;
		this.setFormatter(new PercentFormatter(1.0, null, 0.5, null));
		
		this.setFill(FILL_COLOR);
		this.removeBorder();
		this.setBorderWidth(0);
		this.setFontSize(HEIGHT * .85);
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
	public static TextOption makeTextOption(Stat stat){
		var text = NUMBER_FORMATTER.format(stat.get());
		ZColor color;
		
		// Update color
		if(stat.buffed()) color = BUFF_TEXT_COLOR;
		else if(stat.debuffed()) color = DEBUFF_TEXT_COLOR;
		else color = BASE_TEXT_COLOR;
		return new TextOption(text, color);
	}
}
