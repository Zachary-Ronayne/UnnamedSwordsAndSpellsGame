package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zusass.menu.comp.ZusassEnumToggleButton;

/** A button for selecting the spell effect type for the spell maker */
public class EffectTypeButton extends ZusassEnumToggleButton<MakerEffectType>{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu See {@link #menu}
	 */
	public EffectTypeButton(SpellMakerMenu menu){
		super(0, 0, 180, 32, MakerEffectType.STATUS, MakerEffectType.values());
		this.menu = menu;
		this.onValueChange(this.getSelectedValue());
		
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(620.0, null, null, 200.0));
	}
	
	@Override
	public void onValueChange(MakerEffectType value){
		if(this.menu != null && !this.menu.canSelectInstant()) {
			this.set(value);
			return;
		}
		
		super.onValueChange(value);
		if(menu != null) menu.updateDisplayedFields(value.getEffectType());
	}
}