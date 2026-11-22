package zusass.menu.spellmaker;

import zgame.menu.format.PixelFormatter;
import zgame.menu.togglebutton.BoolToggleButtonValue;
import zgame.stat.modifier.ModifierType;
import zusass.menu.comp.ZusassBoolToggleButton;

/** The button for toggling if the spell will be a positive or negative effect */
public class PositiveNegativeButton extends ZusassBoolToggleButton{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * @param menu The menu using this button
	 */
	public PositiveNegativeButton(SpellMakerMenu menu){
		super(0, 0, 180, 32, true, "Buff", "Debuff");
		this.menu = menu;
		this.onValueChange(this.getSelectedValue());
		this.setFontSize(20);
		this.setFormatter(new PixelFormatter(220.0, null, null, 200.0));
	}
	
	/**
	 * Update the display state of this button depending on if the menu has selected MULT_MULT or not, depending on if it should display buff or debuff
	 *
	 * @return true if the value was set here, false otherwise
	 */
	public boolean updateForMultMult(){
		if(this.menu == null || this.menu.getSelectedModifierType() != ModifierType.MULT_MULT) return false;
		
		Double amount = this.menu.getDoubleInput(SpellMakerMenu.MAGNITUDE);
		if(amount == null) amount = 1.0;
		var newValue = amount >= 1;
		if(this.getSelectedValue().isTrue() != newValue) {
			this.set(newValue);
			return true;
		}
		return false;
	}
	
	@Override
	public void onValueChange(BoolToggleButtonValue value){
		// Don't set the value explicitly in this case, the call to set will call this method with the updated value
		if(this.updateForMultMult()) return;
		
		super.onValueChange(value);
	}
}
