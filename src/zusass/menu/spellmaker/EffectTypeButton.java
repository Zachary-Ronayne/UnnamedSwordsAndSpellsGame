package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffectType;
import zusass.menu.comp.ToggleButton;

import java.util.ArrayList;
import java.util.Map;

/** A button for selecting the spell effect type for the spell maker */
public class EffectTypeButton extends ToggleButton{
	
	/** The text for selecting the projectile cast type */
	public static final String STATUS = "Status";
	/** The text for selecting the self cast type */
	public static final String INSTANT = "Instant";
	
	/** Mapping the spell effect type to the text used for selecting the cast type */
	private static final Map<String, SpellEffectType> EFFECT_TYPE_MAP = Map.of(STATUS, SpellEffectType.STATUS_EFFECT, INSTANT, SpellEffectType.STAT_ADD);
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu See {@link #menu}
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public EffectTypeButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 180, 32, null, zgame);
		this.menu = menu;
		
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(620.0, null, null, 200.0));
		
		var values = new ArrayList<String>();
		values.add(STATUS);
		values.add(INSTANT);
		this.setValues(values);
		this.setSelectedIndex(0);
	}
	
	@Override
	public String getText(){
		if(this.menu != null && !this.menu.canSelectInstant()) return STATUS;
		return super.getText();
	}
	
	
	@Override
	public void setText(String text){
		super.setText(text);
		menu.updateDisplayedFields(EFFECT_TYPE_MAP.get(text));
		this.centerText();
	}
}