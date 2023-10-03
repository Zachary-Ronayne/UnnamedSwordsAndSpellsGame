package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuHolder;
import zgame.menu.MenuTextBox;
import zgame.menu.MenuThing;
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
import zusass.game.magic.effect.SpellEffect;
import zusass.game.magic.effect.SpellEffectStatAdd;
import zusass.game.magic.effect.SpellEffectStatusEffect;
import zusass.game.magic.effect.SpellEffectType;
import zusass.game.status.StatEffect;
import zusass.menu.ZusassMenu;
import zusass.menu.comp.ZusassButton;
import zusass.menu.comp.ZusassMenuText;
import zusass.menu.mainmenu.comp.newgamemenu.ZusassTextBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** The {@link ZusassMenu} for creating spells */
public class SpellMakerMenu extends ZusassMenu{
	
	/** The key for the text box holding the user entered name for the spell */
	public static final String NAME = "name";
	/** The key for the text box holding the duration of a spell */
	public static final String DURATION = "duration";
	/** The key for the text box holding the magnitude of a spell */
	public static final String MAGNITUDE = "magnitude";
	/** The key for the text box holding the speed of a projectile spell */
	public static final String SPEED = "speed";
	/** The key for the text box holding the size of a projectile spell */
	public static final String SIZE = "size";
	/** The key for the text box holding the range of a projectile spell */
	public static final String RANGE = "range";
	
	/** A map holding the text box holding the values for the spell */
	private final HashMap<String, ZusassTextBox> textBoxes;
	
	/** The button used to create a spell */
	private final SpellCreateButton createButton;
	
	/** See {@link StatChooseButton} */
	private final StatChooseButton statChooseButton;
	
	/** See {@link PositiveNegativeButton} */
	private final PositiveNegativeButton positiveNegativeButton;
	
	/** See {@link CastTypeButton} */
	private final CastTypeButton castTypeButton;
	
	/** See {@link EffectTypeButton} */
	private final EffectTypeButton effectTypeButton;
	
	/** The object holding the fields for the projectile spell type */
	private final MenuHolder projectileBoxesHolder;
	
	/** The currently selected spell cast type */
	private SpellCastType selectedCastType;
	
	/** The object holding the fields for the status effect spell type */
	private final MenuHolder statusEffectBoxesHolder;
	
	/** The currently selected spell effect type */
	private SpellEffectType selectedEffectType;
	
	/** The object holding the fields for the modifier type */
	private final ModifierTypeButton modifierTypeButton;
	
	/** The currently selected type for a modifier */
	private ModifierType selectedModifierType;
	
	/** The spell which is defined by this menu, or null if one cannot be created */
	private Spell currentSpell;
	
	/** The text thing holding the current cost of the spell */
	private final ZusassMenuText spellCostText;
	
	/**
	 * Create the menu
	 *
	 * @param zgame The game using the menu
	 */
	public SpellMakerMenu(ZusassGame zgame){
		super(zgame, "Spell Creation");
		this.setSendToTopOnClick(true);
		this.currentSpell = null;
		
		this.setFill(new ZColor(.4, 0, .6, .8));
		this.setBorder(new ZColor(.3, 0, .5, .5));
		this.setDraggableColor(new ZColor(.6, 0, .85, .8));
		this.makeDraggable(10, 30);
		// issue#30 make this draggable again when the buffer issue is fixed
		this.setDraggable(false);
		this.setDraggableSides(false);
		this.setMinWidth(600.0);
		this.setMinHeight(500.0);
		
		this.getTitleThing().setFontSize(40);
		this.getTitleThing().setTextY(50);
		
		this.format(zgame.getWindow(), new PercentFormatter(.8, .95, .5, .5));
		
		// The button for creating a new spell
		this.createButton = new SpellCreateButton(this, zgame);
		this.addThing(this.createButton);
		
		// The button for resetting the menu
		var resetButton = new ZusassButton(1, 1, 160, 40, "Reset", zgame){
			@Override
			public void click(Game game){
				reset();
			}
		};
		resetButton.setFormatter(new PixelFormatter(null, 15.0, null, 15.0));
		resetButton.setFontSize(30);
		resetButton.centerText();
		this.addThing(resetButton);
		
		this.textBoxes = new HashMap<>();
		
		this.initTextBox(NAME, zgame, "Name", 120, MenuTextBox.Mode.DEFAULT, this);
		
		this.projectileBoxesHolder = makeNewMenuHolder();
		this.statusEffectBoxesHolder = makeNewMenuHolder();
		
		this.initTextBox(DURATION, zgame, "Duration", 170, MenuTextBox.Mode.FLOAT_POS, this.statusEffectBoxesHolder);
		this.initTextBox(MAGNITUDE, zgame, "Magnitude", 220, MenuTextBox.Mode.FLOAT_POS, this);
		this.initTextBox(SPEED, zgame, "Speed", 270, MenuTextBox.Mode.FLOAT_POS, this.projectileBoxesHolder);
		this.initTextBox(SIZE, zgame, "Size", 320, MenuTextBox.Mode.FLOAT_POS, this.projectileBoxesHolder);
		this.initTextBox(RANGE, zgame, "Range", 370, MenuTextBox.Mode.FLOAT_POS, this.projectileBoxesHolder);
		this.addThing(this.statusEffectBoxesHolder);
		this.addThing(this.projectileBoxesHolder);
		
		// The button for selecting the spell type
		this.statChooseButton = new StatChooseButton(this, zgame);
		this.addThing(this.statChooseButton);
		
		// The button for selecting if the spell will be positive or negative
		this.positiveNegativeButton = new PositiveNegativeButton(this, zgame);
		this.addThing(this.positiveNegativeButton);
		
		// The button for selecting the effect type, like instant effect or status effect
		this.effectTypeButton = new EffectTypeButton(this, zgame);
		this.addThing(this.effectTypeButton);
		
		// The button for selecting the cast type, so self or projectile
		this.castTypeButton = new CastTypeButton(this, zgame);
		this.addThing(this.castTypeButton);
		
		// The button for the modifier type
		this.modifierTypeButton = new ModifierTypeButton(this, zgame);
		this.addThing(this.modifierTypeButton);
		
		// Text for showing the spell cost
		this.spellCostText = new ZusassMenuText(0, 0, 300, 32, "", zgame);
		this.spellCostText.setFill(new ZColor(1));
		this.spellCostText.setBorder(new ZColor(0));
		this.spellCostText.setBorderWidth(2);
		this.spellCostText.setFontColor(new ZColor(0));
		this.spellCostText.setFontSize(24);
		this.spellCostText.setFormatter(new PixelFormatter(12.0, null, null, 20.0));
		this.addThing(this.spellCostText);
		
		this.updateMenuState();
		this.reformat(zgame);
		this.reset();
	}
	
	/** @return A new menu holder for this menu */
	private MenuHolder makeNewMenuHolder(){
		var m = new MenuHolder();
		m.invisible();
		m.setWidth(this.getWidth());
		m.setHeight(this.getHeight());
		return m;
	}
	
	/**
	 * Set up a text box for input
	 *
	 * @param key The string to use for a key to put into {@link #textBoxes}
	 * @param zgame The game using the box
	 * @param hint The hint for the box
	 * @param y The y position of the box
	 * @param mode The input mode of the box
	 * @param addTo The {@link MenuThing} to add the text box to
	 * @return The new box
	 */
	private ZusassTextBox initTextBox(String key, ZusassGame zgame, String hint, double y, MenuTextBox.Mode mode, MenuThing addTo){
		var box = new ZusassTextBox(0, y, 1, 40, zgame){
			@Override
			public void setCurrentText(String text){
				super.setCurrentText(text);
				updateMenuState();
			}
			
			@Override
			public void setSelected(boolean selected){
				super.setSelected(selected);
				selectOneTextBox(zgame, selected, this);
			}
		};
		box.setHint(hint + "...");
		box.setMode(mode);
		box.setFontSize(30);
		box.setFormatter(new PercentFormatter(.5, null, 0.5, null));
		addTo.addThing(box);
		box.format();
		
		var w = 300;
		var h = box.getHeight();
		var text = new ZusassMenuText(box.getRelX() - w, box.getRelY() - h * .1, w, h, hint + ":", zgame, true);
		text.setFontColor(new ZColor(0));
		text.setFontSize(30);
		text.centerTextVertical();
		text.alignTextXRight(0);
		addTo.addThing(text);
		
		this.textBoxes.put(key, box);
		return box;
	}
	
	/** Update the current text of {@link #effectTypeButton}, basically just to make sure it has the correct text if anything that this button depends on, changes */
	public void updateEffectTypeButton(){
		if(this.effectTypeButton == null) return;
		this.effectTypeButton.setText(this.effectTypeButton.getText());
	}
	
	/** Update the current text of {@link #positiveNegativeButton}, basically just to make sure it has the correct text if anything that this button depends on, changes */
	public void updatePositiveNegativeButton(){
		if(this.positiveNegativeButton == null) return;
		this.positiveNegativeButton.setText(this.positiveNegativeButton.getText());
	}
	
	/**
	 * Update which fields are displayed
	 */
	public void updateDisplayedFields(){
		this.updateDisplayedFields(this.selectedCastType, this.selectedEffectType, true);
	}
	
	/**
	 * Update which fields are displayed
	 *
	 * @param effectType The new effect type for the spell
	 */
	public void updateDisplayedFields(SpellEffectType effectType){
		this.updateDisplayedFields(this.selectedCastType, effectType, true);
	}
	
	/**
	 * Update which fields are displayed
	 *
	 * @param castType The new cast type for the button
	 */
	public void updateDisplayedFields(SpellCastType castType){
		this.updateDisplayedFields(castType, this.selectedEffectType, true);
	}
	
	/**
	 * Update which fields are displayed
	 *
	 * @param castType The new cast type for the spell
	 * @param effectType The new effect type for the spell
	 * @param recursive true if this call should propagate to {@link #updateMenuState()}, false otherwise
	 */
	public void updateDisplayedFields(SpellCastType castType, SpellEffectType effectType, boolean recursive){
		if(effectType == SpellEffectType.STATUS_EFFECT) this.addThing(this.statusEffectBoxesHolder);
		else this.removeThing(this.statusEffectBoxesHolder, false);
		this.selectedEffectType = effectType;
		
		if(castType == SpellCastType.PROJECTILE) this.addThing(this.projectileBoxesHolder);
		else this.removeThing(this.projectileBoxesHolder, false);
		this.selectedCastType = castType;
		if(recursive) this.updateMenuState();
		this.updateCurrentSpell();
	}
	
	/** @param modifierType The new value for {@link #selectedModifierType} */
	public void updateModifierType(ModifierType modifierType){
		this.selectedModifierType = modifierType;
		if(this.positiveNegativeButton != null) this.positiveNegativeButton.setDisabled(modifierType == ModifierType.MULT_MULT);
		this.updateCurrentSpell();
	}
	
	@Override
	public void onRemove(Game game){
		super.onRemove(game);
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		player.setInputDisabled(false);
	}
	
	/**
	 * Set it so that only the given text box is selected
	 *
	 * @param zgame The game using this menu
	 * @param selected true if the box was set to selected, false otherwise
	 * @param box The box which was selected or not selected
	 */
	private void selectOneTextBox(ZusassGame zgame, boolean selected, ZusassTextBox box){
		// Unselect the rest of the text boxes
		if(selected){
			for(var b : this.textBoxes.values()){
				if(box != b) b.setSelected(false);
			}
		}
		
		// Disable player input while in a text box
		zgame.getPlayer().setInputDisabled(selected);
		
		// Disable key input on other menus while a text box is selected
		this.setPropagateKeyAction(!selected);
	}
	
	/**
	 * Format the components of this menu based on its current size
	 *
	 * @param zgame The game to reformat to
	 */
	private void reformat(ZusassGame zgame){
		var t = this.getTitleThing();
		t.format(new MultiFormatter(new PercentFormatter(1.0, 1.0, 0.5, 0.5), new PixelFormatter(null, null, 50.0, null)));
		t.centerTextHorizontal();
		
		for(var b : this.textBoxes.values()) b.regenerateBuffer();
	}
	
	@Override
	public void onDragEnd(Game game, boolean sideDrag){
		super.onDragEnd(game, sideDrag);
		this.reformat((ZusassGame)game);
	}
	
	@Override
	public boolean isDefaultDestroyRemove(){
		return false;
	}
	
	/**
	 * Set the text of a text box
	 *
	 * @param key The key for the text box in the map {@link #textBoxes}
	 * @param text The text to set to
	 */
	public void setTextBoxText(String key, String text){
		var box = this.textBoxes.get(key);
		if(box == null) return;
		box.setCurrentText(text);
	}
	
	/**
	 * @param key The key of the text value to get
	 * @return The text the user has input for a spell value, or null if there is no value
	 */
	public String getStringInput(String key){
		var b = this.textBoxes.get(key);
		if(b == null) return null;
		return b.getText();
	}
	
	/**
	 * @param key The key of the double value to get
	 * @return The double the user has input for a spell value, or null if there is no value
	 */
	public Double getDoubleInput(String key){
		var b = this.textBoxes.get(key);
		if(b == null) return null;
		return b.getTextAsDouble();
	}
	
	/** @return The stat selected for the spell */
	public StatSpellType getSelectedStat(){
		return this.statChooseButton == null ? null : this.statChooseButton.getSelectedStat();
	}
	
	/** @return See {@link #selectedCastType} */
	public SpellCastType getSelectedCastType(){
		return this.selectedCastType;
	}
	
	/** @return See {@link #selectedEffectType} */
	public SpellEffectType getSelectedEffectType(){
		return this.selectedEffectType;
	}
	
	/** @return See {@link #selectedModifierType} */
	public ModifierType getSelectedModifierType(){
		return this.selectedModifierType;
	}
	
	/** @return true if the selected will be a buff, false otherwise */
	public boolean isBuffSelected(){
		if(selectedModifierType == ModifierType.MULT_MULT) return true;
		return PositiveNegativeButton.BUFF.equals(this.positiveNegativeButton.getSelectedValue());
	}
	
	/** @return true if the instant effect cast type can be selected, false otherwise */
	public boolean canSelectInstant(){
		return this.effectTypeButton == null || !this.effectTypeButton.isDisabled();
	}
	
	/** Update the state of the menu, the currently created spell, and if the create button should be disabled or enabled */
	public void updateMenuState(){
		var disabled = this.getSelectedStat() == null;
		
		var stat = this.statChooseButton == null ? null : this.statChooseButton.getSelectedStat();
		if(stat != null){
			this.effectTypeButton.setDisabled(!stat.canUseInstant());
			if(!stat.canUseInstant()) this.updateDisplayedFields(this.selectedCastType, SpellEffectType.STATUS_EFFECT, false);
		}
		
		// If the multiply modifier type is selected, also ensure the text for the positive negative button has the correct text
		if(selectedModifierType == ModifierType.MULT_MULT) this.updatePositiveNegativeButton();
		
		var requiredBoxes = new ArrayList<String>();
		requiredBoxes.add(NAME);
		requiredBoxes.add(MAGNITUDE);
		if(this.selectedEffectType == SpellEffectType.STATUS_EFFECT) requiredBoxes.add(DURATION);
		if(this.selectedCastType == SpellCastType.PROJECTILE){
			requiredBoxes.add(SPEED);
			requiredBoxes.add(SIZE);
			requiredBoxes.add(RANGE);
		}
		
		for(var k : requiredBoxes){
			if(disabled) break;
			var v = this.getStringInput(k);
			if(v == null || v.isEmpty()) disabled = true;
		}
		this.createButton.setDisabled(disabled);
		this.updateCurrentSpell();
	}
	
	/** Update {@link #currentSpell} to the current values of this menu */
	public void updateCurrentSpell(){
		if(this.spellCostText == null) return;
		this.currentSpell = this.createSpell();
		var sb = new StringBuilder("Cost:");
		if(this.currentSpell != null){
			var cost = this.currentSpell.getCost();
			sb.append(String.format(" %.2f", cost));
		}
		this.spellCostText.setText(sb.toString());
		this.spellCostText.centerTextVertical();
		this.spellCostText.alignTextXLeft(4);
	}
	
	/** Reset the state of this menu to have nothing input */
	public void reset(){
		for(var b : this.textBoxes.values()) b.setCurrentText("1");
		this.textBoxes.get(NAME).setCurrentText("");
	}
	
	/** @return The current spell as defined by the current parameters of this menu, or null if no spell can be created */
	public Spell createSpell(){
		var duration = this.getDoubleInput(SpellMakerMenu.DURATION);
		var magnitude = this.getDoubleInput(SpellMakerMenu.MAGNITUDE);
		var size = this.getDoubleInput(SpellMakerMenu.SIZE);
		var range = this.getDoubleInput(SpellMakerMenu.RANGE);
		var speed = this.getDoubleInput(SpellMakerMenu.SPEED);
		var stat = this.getSelectedStat();
		if(stat == null) return null;
		var buff = this.isBuffSelected();
		var modifierType = this.getSelectedModifierType();
		if(modifierType == null) return null;
		var castType = this.getSelectedCastType();
		if(castType == null) return null;
		var effectType = stat.canUseInstant() ? this.getSelectedEffectType() : SpellEffectType.STATUS_EFFECT;
		if(effectType == null) return null;
		
		Spell spell;
		SpellEffect effect;
		if(effectType == SpellEffectType.STATUS_EFFECT){
			if(Arrays.asList(duration, stat, magnitude).contains(null)) return null;
			StatModifier modifier = switch(modifierType){
				case ADD -> new StatModifier(buff ? magnitude : -magnitude, ModifierType.ADD);
				case MULT_MULT -> new StatModifier(magnitude, ModifierType.MULT_MULT);
				case MULT_ADD -> new StatModifier(buff ? magnitude : -magnitude, ModifierType.MULT_ADD);
			};
			
			effect = new SpellEffectStatusEffect(new StatEffect(duration, modifier, stat.getStatus()));
		}
		else {
			if(Arrays.asList(stat, buff, magnitude).contains(null)) return null;
			effect = new SpellEffectStatAdd(stat.getInstant(), buff ? magnitude : -magnitude);
		}
		
		if(castType == SpellCastType.PROJECTILE) {
			if(Arrays.asList(effect, size, range, speed).contains(null)) return null;
			spell = new ProjectileSpell(effect, size, range, speed);
		}
		else spell = new SelfSpell(effect);
		
		var name = this.getStringInput(SpellMakerMenu.NAME);
		if(name == null) name = "";
		spell.setName(name);
		
		return spell;
	}
	
}
