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

import java.util.List;

/** The {@link ZusassMenu} for creating spells */
public class SpellMakerMenu extends ZusassMenu{
	
	/** The text box holding the user entered name for the spell */
	private final ZusassTextBox spellNameTextBox;
	
	/** The text box holding the duration of a spell */
	private final ZusassTextBox durationTextBox;
	
	/** The text box holding the magnitude of a spell */
	private final ZusassTextBox magnitudeTextBox;
	
	/** The text box holding the speed of a projectile spell */
	private final ZusassTextBox speedTextBox;
	
	/** The text box holding the size of a projectile spell */
	private final ZusassTextBox sizeTextBox;
	
	/** The text box holding the range of a projectile spell */
	private final ZusassTextBox rangeTextBox;
	
	/** The button used to create a spell */
	private final SpellCreateButton createButton;
	
	/**
	 * Create the menu
	 *
	 * @param zgame The game using the menu
	 */
	public SpellMakerMenu(ZusassGame zgame){
		super(zgame, "Spell Creation");
		this.getAllThings().addClass(ZusassTextBox.class);
		
		this.setFill(new ZColor(.4, 0, .6, .8));
		this.setBorder(new ZColor(.3, 0, .5, .5));
		this.setDraggableColor(new ZColor(.6, 0, .85, .8));
		this.makeDraggable(10, 30);
		this.setMinWidth(600.0);
		this.setMinHeight(400.0);
		
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
		
		// TODO disable all other input, i.e. player movement, when the text box is selected
		this.spellNameTextBox = new ZusassTextBox(0, 120, 1, 50, zgame){
			@Override
			public void setText(String text){
				super.setText(text);
				updateDisabled();
			}
		};
		this.spellNameTextBox.setHint("Enter a name...");
		this.spellNameTextBox.setFormatter(new PercentFormatter(.9, null, 0.5, null));
		this.addThing(this.spellNameTextBox);
		
		this.durationTextBox = initTextBox(zgame, "Duration...", 180, MenuTextBox.Mode.FLOAT_POS);
		this.magnitudeTextBox = initTextBox(zgame, "Magnitude...", 230, MenuTextBox.Mode.FLOAT_POS);
		this.speedTextBox = initTextBox(zgame, "Speed...", 280, MenuTextBox.Mode.FLOAT_POS);
		this.sizeTextBox = initTextBox(zgame, "Size...", 330, MenuTextBox.Mode.FLOAT_POS);
		this.rangeTextBox = initTextBox(zgame, "Range...", 380, MenuTextBox.Mode.FLOAT_POS);
		
		// TODO make a way of abstracting this out so that you don't have to do this when something gets resized
		
		// TODO make buttons not highlight on mouse hover if they are not currently clickable
		this.reformat(zgame);
	}
	
	/**
	 * Set up a text box for input
	 * @param zgame The game using the box
	 * @param hint The hint for the box
	 * @param y The y position of the box
	 * @param mode The input mode of the box
	 * @return The new box
	 */
	private ZusassTextBox initTextBox(ZusassGame zgame, String hint, double y, MenuTextBox.Mode mode){
		var box = new ZusassTextBox(0, y, 1, 40, zgame){
			@Override
			public void setSelected(boolean selected){
				super.setSelected(selected);
				if(selected){
					var boxes = getTextBoxes();
					for(var b : boxes){
						if(this != b) b.setSelected(false);
					}
				}
			}
		};
		box.setHint(hint);
		box.setMode(mode);
		box.setFontSize(30);
		box.setFormatter(new PercentFormatter(.5, null, 0.5, null));
		this.addThing(box);
		return box;
	}
	
	/** @return The text boxes used by this menu */
	private List<ZusassTextBox> getTextBoxes(){
		return this.getAllThings().get(ZusassTextBox.class);
	}
	
	/**
	 * Format the components of this menu based on its current size
	 * @param zgame The game to reformat to
	 */
	private void reformat(ZusassGame zgame){
		var t = this.getTitleThing();
		t.format(new MultiFormatter(new PercentFormatter(1.0, 1.0, 0.5, 0.5), new PixelFormatter(null, null, 50.0, null)));
		t.centerTextHorizontal();
		
		var boxes = getTextBoxes();
		for(var b : boxes) b.regenerateBuffer();
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
	
	/** @return The text the user has input for the spell name */
	public String getEnteredName(){
		if(this.spellNameTextBox == null) return null;
		return this.spellNameTextBox.getText();
	}
	
	// TODO abstract out all of these text boxes to a map or something so there isn't so much copy pasted crap
	
	/** @return The duration for the spell, or null if one was not entered */
	public Double getDuration(){
		if(this.spellNameTextBox == null) return null;
		return this.durationTextBox.getTextAsDouble();
	}
	
	/** @return The magnitude for the spell, or null if one was not entered */
	public Double getMagnitude(){
		if(this.magnitudeTextBox == null) return null;
		return this.magnitudeTextBox.getTextAsDouble();
	}
	
	/** @return The speed for the spell, or null if one was not entered */
	public Double getSpeed(){
		if(this.speedTextBox == null) return null;
		return this.speedTextBox.getTextAsDouble();
	}
	
	/** @return The speed for the spell, or null if one was not entered */
	public Double getSize(){
		if(this.sizeTextBox == null) return null;
		return this.sizeTextBox.getTextAsDouble();
	}
	
	/** @return The speed for the spell, or null if one was not entered */
	public Double getRange(){
		if(this.rangeTextBox == null) return null;
		return this.rangeTextBox.getTextAsDouble();
	}
	
	/** Update the state of the create button for if it should be disabled or enabled */
	public void updateDisabled(){
		var name = this.getEnteredName();
		var d = name == null || name.isEmpty();
		d |= this.getDuration() == null || this.getMagnitude() == null || this.getSpeed() == null || this.getSize() == null || this.getRange() == null;
		this.createButton.setDisabled(d);
	}
	
	/** Reset the state of this menu to have nothing input */
	public void reset(){
		this.spellNameTextBox.setText("");
		this.durationTextBox.setText("");
		this.magnitudeTextBox.setText("");
		this.speedTextBox.setText("");
		this.sizeTextBox.setText("");
		this.rangeTextBox.setText("");
	}
	
}
