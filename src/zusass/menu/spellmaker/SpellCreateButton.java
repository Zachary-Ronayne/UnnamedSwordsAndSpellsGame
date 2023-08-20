package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zusass.ZusassGame;
import zusass.game.magic.ProjectileSpell;
import zusass.game.magic.SelfSpell;
import zusass.game.magic.Spell;
import zusass.game.magic.SpellCastType;
import zusass.game.magic.effect.SpellEffectStatusEffect;
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
		
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		
		var duration = this.menu.getDoubleInput(SpellMakerMenu.DURATION);
		var magnitude = this.menu.getDoubleInput(SpellMakerMenu.MAGNITUDE);
		var size = this.menu.getDoubleInput(SpellMakerMenu.SIZE);
		var range = this.menu.getDoubleInput(SpellMakerMenu.RANGE);
		var speed = this.menu.getDoubleInput(SpellMakerMenu.SPEED);
		var name = this.menu.getStringInput(SpellMakerMenu.NAME);
		var stat = this.menu.getSelectedStat();
		var buff = this.menu.isBuffSelected();
		var modifier = new StatModifier(buff ? magnitude : -magnitude, ModifierType.ADD);
		var castType = this.menu.getSelectedCastType();
		
		var spellEffectStatusEffect = new SpellEffectStatusEffect(new StatEffect(duration, modifier, stat.getStatus()));
		
		Spell spell;
		if(castType == SpellCastType.PROJECTILE) spell = new ProjectileSpell(spellEffectStatusEffect, size, range, speed);
		else spell = new SelfSpell(spellEffectStatusEffect);
		spell.setName(name);
		player.getSpells().addSpell(spell);
		
		var inventoryMenu = zgame.getPlayState().getInventoryMenu();
		inventoryMenu.regenerateThings(zgame);
		inventoryMenu.updateScrollAmount();
		
		this.menu.reset();
	}
}
