package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zusass.ZusassGame;
import zusass.game.magic.SelfSpell;
import zusass.game.magic.effect.SpellEffectStatusEffect;
import zusass.game.stat.ZusassStat;
import zusass.game.status.StatEffect;
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
		
		var name = this.menu.getEnteredName();
		if(name == null || name.isBlank()) return;
		
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		
		// TODO determine the information for the spell from the creation menu
		var spell = new SelfSpell(new SpellEffectStatusEffect(new StatEffect(10, new StatModifier(100, ModifierType.ADD), ZusassStat.HEALTH_MAX)));
		spell.setName(name);
		player.getSpells().addSpell(spell);
		
		var inventoryMenu = zgame.getPlayState().getInventoryMenu();
		inventoryMenu.regenerateThings(zgame);
		inventoryMenu.updateScrollAmount();
		
		this.menu.reset();
	}
}
