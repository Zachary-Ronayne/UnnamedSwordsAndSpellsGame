package zusass.menu.player;

import zgame.core.graphics.TextOption;
import zusass.ZusassGame;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;

/** An item holding text for the {@link StatList}, for a resource stat */
public class ResourceListItem extends StatListItem{
	
	/** The stat representing the current value of this stat */
	private final ZusassStat currentType;
	/** The stat representing the max value of this stat */
	private final ZusassStat maxType;
	/** The stat representing the regeneration value of this stat */
	private final ZusassStat regenType;
	/** The name used to describe this stat */
	private final String baseName;

	/**
	 * Create a new stat item
	 * @param size The size of this item
	 * @param statList See {@link #statList}
	 * @param baseName See {@link #baseName}
	 * @param currentType See {@link #currentType}
	 * @param maxType See {@link #maxType}
	 * @param regenType See {@link #regenType}
	 * @param zgame The game to use to create the item
	 */
	public ResourceListItem(double size, StatList statList, String baseName, ZusassStat currentType, ZusassStat maxType, ZusassStat regenType, ZusassGame zgame){
		super(size, statList, null, zgame);
		this.baseName = baseName;
		this.currentType = currentType;
		this.maxType = maxType;
		this.regenType = regenType;
	}
	
	@Override
	public void updateTextOptions(ZusassMob mob){
		var options = new ArrayList<TextOption>(7);
		options.add(new TextOption(new StringBuilder(this.baseName).append(": ").toString(), BASE_TEXT_COLOR));
		options.add(this.makeTextOption(mob.getStat(this.currentType)));
		options.add(new TextOption("/", BASE_TEXT_COLOR));
		options.add(this.makeTextOption(mob.getStat(this.maxType)));
		options.add(new TextOption("(", BASE_TEXT_COLOR));
		options.add(this.makeTextOption(mob.getStat(this.regenType)));
		options.add(new TextOption(")", BASE_TEXT_COLOR));
		
		this.getTextBuffer().setOptions(options);
	}
}
