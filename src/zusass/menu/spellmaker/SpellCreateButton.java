package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.comp.ZusassButton;

/** The button in the spell creation menu which performs the actual spell creation */
public class SpellCreateButton extends ZusassButton{
	
	/** The menu using this button */
	private final SpellMakerMenu menu;
	
	/**
	 * Create a {@link SpellCreateButton} with the appropriate parameters
	 *
	 * @param zgame The {@link ZusassGame} that uses this button
	 */
	public SpellCreateButton(SpellMakerMenu menu, ZusassGame zgame){
		super(0, 0, 200, 32, "Create", zgame);
		this.menu = menu;
		this.setFontSize(20);
		this.centerText();
		this.setFormatter(new MultiFormatter(new PixelFormatter(null, null, null, 40.0), new PercentFormatter(null, null, .5, null)));
	}
	
	@Override
	public void click(Game game){
		super.click(game);
		
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		
		var spell = this.menu.createSpell();
		if(spell == null) return;
		
		player.getSpells().addSpell(spell);
		
		var inventoryMenu = zgame.getPlayState().getInventoryMenu();
		inventoryMenu.regenerateThings(zgame);
		inventoryMenu.updateScrollAmount();
		
		this.menu.setTextBoxText(SpellMakerMenu.NAME, "");
	}
}
