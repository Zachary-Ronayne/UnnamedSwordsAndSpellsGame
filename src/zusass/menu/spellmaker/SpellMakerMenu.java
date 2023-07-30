package zusass.menu.spellmaker;

import zgame.core.Game;
import zgame.core.graphics.ZColor;
import zgame.menu.MenuTextBox;
import zgame.menu.format.MultiFormatter;
import zgame.menu.format.PercentFormatter;
import zgame.menu.format.PixelFormatter;
import zusass.ZusassGame;
import zusass.menu.ZusassMenu;
import zusass.menu.comp.ZusassButton;
import zusass.menu.mainmenu.comp.newgamemenu.ZusassTextBox;

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
	private final ZusassButton statChooseButton;
	
	/**
	 * Create the menu
	 *
	 * @param zgame The game using the menu
	 */
	public SpellMakerMenu(ZusassGame zgame){
		super(zgame, "Spell Creation");
		
		this.setFill(new ZColor(.4, 0, .6, .8));
		this.setBorder(new ZColor(.3, 0, .5, .5));
		this.setDraggableColor(new ZColor(.6, 0, .85, .8));
		this.makeDraggable(10, 30);
		this.setMinWidth(600.0);
		this.setMinHeight(500.0);
		
		this.getTitleThing().setFontSize(40);
		this.getTitleThing().setTextY(50);
		
		this.format(zgame.getWindow(), new PercentFormatter(.8, .95, .5, .5));
		
		// The button for creating a new spell
		this.createButton = new SpellCreateButton(this, zgame);
		this.addThing(this.createButton);
		
		// The button for selecting the spell type
		this.statChooseButton = new StatChooseButton(this, zgame);
		this.addThing(this.statChooseButton);
		
		// TODO a button for selecting if the spell will be positive or negative
		
		// TODO a button for selecting the cast type, so self or projectile
		
		// TODO a button for selecting the effect type, like instant effect or status effect
		
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
		
		var spellNameTextBox = this.initTextBox(NAME, zgame, "Enter a name...", 120, MenuTextBox.Mode.DEFAULT, false);
		spellNameTextBox.setHeight(50);
		spellNameTextBox.setFormatter(new PercentFormatter(.9, null, 0.5, null));
		this.addThing(spellNameTextBox);
		
		this.initTextBox(DURATION, zgame, "Duration...", 180, MenuTextBox.Mode.FLOAT_POS, true);
		this.initTextBox(MAGNITUDE, zgame, "Magnitude...", 230, MenuTextBox.Mode.FLOAT_POS, true);
		this.initTextBox(SPEED, zgame, "Speed...", 280, MenuTextBox.Mode.FLOAT_POS, true);
		this.initTextBox(SIZE, zgame, "Size...", 330, MenuTextBox.Mode.FLOAT_POS, true);
		this.initTextBox(RANGE, zgame, "Range...", 380, MenuTextBox.Mode.FLOAT_POS, true);
		
		this.reformat(zgame);
	}
	
	/**
	 * Set up a text box for input
	 *
	 * @param key The string to use for a key to put into {@link #textBoxes}
	 * @param zgame The game using the box
	 * @param hint The hint for the box
	 * @param y The y position of the box
	 * @param mode The input mode of the box
	 * @return The new box
	 */
	private ZusassTextBox initTextBox(String key, ZusassGame zgame, String hint, double y, MenuTextBox.Mode mode, boolean add){
		var box = new ZusassTextBox(0, y, 1, 40, zgame){
			@Override
			public void setText(String text){
				super.setText(text);
				updateDisabled();
			}
			
			@Override
			public void setSelected(boolean selected){
				super.setSelected(selected);
				selectOneTextBox(zgame, selected, this);
			}
		};
		box.setHint(hint);
		box.setMode(mode);
		box.setFontSize(30);
		box.setFormatter(new PercentFormatter(.5, null, 0.5, null));
		if(add) this.addThing(box);
		this.textBoxes.put(key, box);
		return box;
	}
	
	/**
	 * Set it so that only the given text box is selected
	 *
	 * @param zgame The game using this menu
	 * @param selected true if the box was set to selected, false otherwise
	 * @param box The box which was selected or not selected
	 */
	private void selectOneTextBox(ZusassGame zgame, boolean selected, ZusassTextBox box){
		if(selected){
			var boxes = this.textBoxes;
			for(var b : boxes.values()){
				if(box != b) b.setSelected(false);
			}
			zgame.getPlayer().setInputDisabled(true);
		}
		if(!selected) zgame.getPlayer().setInputDisabled(false);
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
	
	/** Update the state of the create button for if it should be disabled or enabled */
	public void updateDisabled(){
		var disabled = false;
		for(var k : this.textBoxes.keySet()){
			var v = this.getStringInput(k);
			if(v == null || v.isEmpty()){
				disabled = true;
				break;
			}
		}
		this.createButton.setDisabled(disabled);
	}
	
	/** Reset the state of this menu to have nothing input */
	public void reset(){
		for(var b : this.textBoxes.values()) b.setText("");
	}
	
}
