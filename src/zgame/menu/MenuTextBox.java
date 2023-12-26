package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.TextOption;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.GameFont;
import zgame.core.utils.ZArrayUtils;
import zgame.core.utils.ZRect;
import zgame.core.utils.ZStringUtils;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A {@link MenuThing} that can be used to have a user type things in
 */
public class MenuTextBox extends MenuButton{
	
	/** The amount of distance the text of this {@link MenuTextBox} will render to modify what part of the string is visible */
	private double textOffset;
	
	/** The current width of the text of this text box */
	private double textWidth;
	
	/** The text which currently is entered in this text box */
	private String currentText;
	
	/** The color to use for displaying {@link #currentText} */
	private ZColor textColor;
	
	/** A text label to show before the main text */
	private String label;
	
	/** The width that the {@link #label} string takes up */
	private double labelWidth;
	
	/** The text to show as a hint of what should be typed in the text box */
	private String hint;
	
	/** The width that the {@link #hint} string takes up */
	private double hintWidth;
	
	/** The color to use for {@link #hint} */
	private ZColor hintColor;
	
	/** The size of the blinking cursor showing where the text will be typed */
	private double cursorWidth;
	
	/** The color of the cursor showing where the text will be typed */
	private ZColor cursorColor;
	
	/** The amount of time, in seconds, to keep the cursor hidden or blinked on */
	private double blinkTime;
	
	/** The current count of time until the next cursor blink needs to happen */
	private double currentBlinkTime;
	
	/** true if the cursor will currently display, false otherwise */
	private boolean blinkCursor;
	
	/** The index of the current text where new text will be inserted */
	private int cursorIndex;
	
	/** The distance the cursor will be drawn from the beginning of the string */
	private double cursorLocation;
	
	/** The bounds of each letter rendered, from the beginning of the text to that letter */
	private ZRect[] letterBounds;
	
	/** The current mode of this text box */
	private Mode mode;
	
	/** An enum describing what can be entered in this text box */
	public enum Mode{
		/** The default behavior, all values allowed */
		DEFAULT,
		/** Only allow valid integers to be typed in */
		INT,
		/** Only allow positive integers */
		INT_POS,
		/** Only allow valid floating point values */
		FLOAT,
		/** Only allow positive floating point values */
		FLOAT_POS,
	}
	
	/**
	 * Create a new {@link MenuTextBox} with the given values
	 *
	 * @param x See {@link #getRelX()}
	 * @param y See {@link #getRelY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param game The game associated with this thing
	 */
	public MenuTextBox(double x, double y, double w, double h, Game game){
		super(x, y, w, h, game);
		this.hint = "";
		this.label = "";
		
		this.mode = Mode.DEFAULT;
		this.setTextX(5);
		this.setTextY(this.getHeight() - 5);
		this.setFont(new GameFont(this.getFont().getAsset(), 20, 0, 0));
		this.textOffset = 0;
		this.textWidth = 0;
		
		this.currentText = "";
		this.textColor = new ZColor(0);
		this.hintWidth = 0;
		this.labelWidth = 0;
		this.hintColor = new ZColor(.5);
		
		this.cursorWidth = 5;
		this.cursorColor = new ZColor(0, .5);
		this.blinkTime = .7;
		this.currentBlinkTime = 0;
		this.blinkCursor = false;
		this.setCursorIndex(-1);
		this.setText("");
		this.setCurrentText("");
		this.bufferWidthToWindow(game);
	}
	
	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		this.currentBlinkTime += dt;
		if(this.getCurrentBlinkTime() > this.getBlinkTime()){
			this.blinkCursor = !this.blinkCursor;
			this.currentBlinkTime -= this.getBlinkTime();
		}
	}
	
	@Override
	public boolean mouseActionFocused(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		boolean input = super.mouseActionFocused(game, button, press, shift, alt, ctrl);
		// Determine if the text box is selected
		if(this.isFocused(game)) {
			this.setCursorIndex(this.getCurrentText().length() - 1);
			return true;
		}
		return input;
		
		// Not doing further mouse input for now
		// // Only check for clicking on the string to select the index if the box is selected
		// if(!this.isSelected()) return;
		
		// // Check through each letter bounds, starting at the end, to see which letter is selected
		// for(int i = 0; i < this.letterBounds.length; i++){
		// 	if(this.letterBounds[i].contains(mx, my)){
		// 		this.setCursorIndex(i);
		// 		return;
		// 	}
		// }
		// // If no letter was selected, either select the lowest or highest index possible
		// // issue#7 fix issue where it selects the beginning of the string if clicking above the text, probably because it's not finding the string hitbox
		// // issue#8 does this work correctly, using centerX?
		// if(mx < this.centerX()) this.setCursorIndex(-1);
		// else this.setCursorIndex(this.getCurrentText().length() - 1);
	}
	
	@Override
	public void keyActionFocused(Game game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyActionFocused(game, button, press, shift, alt, ctrl);
		if(!press) return;
		
		if(button == GLFW_KEY_BACKSPACE){
			String t = this.getCurrentText();
			if(this.getCursorIndex() < t.length() && this.cursorIndex >= 0){
				var atEnd = this.getCursorIndex() == t.length() - 1;
				this.setCurrentText(ZStringUtils.removeChar(t, this.getCursorIndex()));
				if(atEnd) this.cursorRight();
				else this.cursorLeft();
			}
			return;
		}
		else if(button == GLFW_KEY_DELETE){
			String t = this.getCurrentText();
			if(this.getCursorIndex() + 1 < t.length()) this.setCurrentText(ZStringUtils.removeChar(t, this.getCursorIndex() + 1));
			return;
		}
		// I'm aware GLFW key integers generally match ASCII, but doing it this way gives me more explicit control over what keys type what
		Character toAdd = null;
		switch(button){
			case GLFW_KEY_A -> toAdd = 'a';
			case GLFW_KEY_B -> toAdd = 'b';
			case GLFW_KEY_C -> toAdd = 'c';
			case GLFW_KEY_D -> toAdd = 'd';
			case GLFW_KEY_E -> toAdd = 'e';
			case GLFW_KEY_F -> toAdd = 'f';
			case GLFW_KEY_G -> toAdd = 'g';
			case GLFW_KEY_H -> toAdd = 'h';
			case GLFW_KEY_I -> toAdd = 'i';
			case GLFW_KEY_J -> toAdd = 'j';
			case GLFW_KEY_L -> toAdd = 'l';
			case GLFW_KEY_K -> toAdd = 'k';
			case GLFW_KEY_M -> toAdd = 'm';
			case GLFW_KEY_N -> toAdd = 'n';
			case GLFW_KEY_O -> toAdd = 'o';
			case GLFW_KEY_P -> toAdd = 'p';
			case GLFW_KEY_Q -> toAdd = 'q';
			case GLFW_KEY_R -> toAdd = 'r';
			case GLFW_KEY_S -> toAdd = 's';
			case GLFW_KEY_T -> toAdd = 't';
			case GLFW_KEY_U -> toAdd = 'u';
			case GLFW_KEY_V -> toAdd = 'v';
			case GLFW_KEY_W -> toAdd = 'w';
			case GLFW_KEY_X -> toAdd = 'x';
			case GLFW_KEY_Y -> toAdd = 'y';
			case GLFW_KEY_Z -> toAdd = 'z';
			case GLFW_KEY_SPACE -> toAdd = ' ';
			case GLFW_KEY_GRAVE_ACCENT -> toAdd = shift ? '~' : '`';
			case GLFW_KEY_1 -> toAdd = shift ? '!' : '1';
			case GLFW_KEY_2 -> toAdd = shift ? '@' : '2';
			case GLFW_KEY_3 -> toAdd = shift ? '#' : '3';
			case GLFW_KEY_4 -> toAdd = shift ? '$' : '4';
			case GLFW_KEY_5 -> toAdd = shift ? '%' : '5';
			case GLFW_KEY_6 -> toAdd = shift ? '^' : '6';
			case GLFW_KEY_7 -> toAdd = shift ? '&' : '7';
			case GLFW_KEY_8 -> toAdd = shift ? '*' : '8';
			case GLFW_KEY_9 -> toAdd = shift ? '(' : '9';
			case GLFW_KEY_0 -> toAdd = shift ? ')' : '0';
			case GLFW_KEY_MINUS -> toAdd = shift ? '_' : '-';
			case GLFW_KEY_EQUAL -> toAdd = shift ? '+' : '=';
			case GLFW_KEY_LEFT_BRACKET -> toAdd = shift ? '{' : '[';
			case GLFW_KEY_RIGHT_BRACKET -> toAdd = shift ? '}' : ']';
			case GLFW_KEY_BACKSLASH -> toAdd = shift ? '|' : '\\';
			case GLFW_KEY_SEMICOLON -> toAdd = shift ? ':' : ';';
			case GLFW_KEY_APOSTROPHE -> toAdd = shift ? '"' : '\'';
			case GLFW_KEY_COMMA -> toAdd = shift ? '<' : ',';
			case GLFW_KEY_PERIOD -> toAdd = shift ? '>' : '.';
			case GLFW_KEY_SLASH -> toAdd = shift ? '?' : '/';
			case GLFW_KEY_LEFT -> this.cursorLeft();
			case GLFW_KEY_RIGHT -> this.cursorRight();
			case GLFW_KEY_HOME -> this.setCursorIndex(-1);
			case GLFW_KEY_END -> this.setCursorIndex(this.getCurrentText().length());
		}
		if(toAdd != null){
			if(shift && 'a' <= toAdd && toAdd <= 'z') toAdd = Character.toUpperCase(toAdd);
			this.applyCharacter(toAdd);
		}
	}
	
	/**
	 * Apply the given character to this text box.
	 * @param c The character to add
	 */
	public void applyCharacter(Character c){
		var m = this.getMode();
		switch(m){
			case DEFAULT -> insertCharacter(c);
			case INT, INT_POS -> {
				if('0' <= c && c <= '9') insertCharacter(c);
			}
			case FLOAT, FLOAT_POS -> {
				if('0' <= c && c <= '9') insertCharacter(c);
				else if(c == '.'){
					var s = this.getCurrentText();
					var dotIndex = s.indexOf('.');
					var initialCursor = this.getCursorIndex();
					if(initialCursor > -1 || s.indexOf('-') == -1){
						if(dotIndex != -1) {
							this.setCurrentText(ZStringUtils.removeChar(this.getCurrentText(), dotIndex));
							if(initialCursor >= this.getCurrentText().length()) this.cursorRight();
							else if(dotIndex < initialCursor) this.cursorLeft();
						}
						insertCharacter('.');
					}
				}
			}
		}
		if((m == Mode.INT || m == Mode.FLOAT) && c == '-'){
			var s = this.getCurrentText();
			if(s != null && s.length() > 0){
				if(s.charAt(0) == '-') this.setCurrentText(s.substring(1));
				else this.setCurrentText('-' + s);
			}
		}
	}
	
	/**
	 * Insert the given character to the current value at {@link #cursorIndex}
	 * @param c The character to insert
	 */
	public void insertCharacter(Character c){
		this.setCurrentText(ZStringUtils.insertString(this.getCurrentText(), Math.max(0, this.getCursorIndex() + 1), c));
		this.cursorRight();
	}
	
	@Override
	public void render(Game game, Renderer r, ZRect bounds){
		TextOption op;
		if(this.getCurrentText().isEmpty()) op = new TextOption(this.getHint(), this.getHintColor());
		else op = new TextOption(this.getDisplayText(), this.getTextColor());
		this.getTextBuffer().setOptions(ZArrayUtils.singleList(op));
		
		super.render(game, r, bounds);
		
		if(!this.isDisabled() && this.isFocused(game) && this.isBlinkCursor()){
			r.setColor(this.getCursorColor());
			double fontSize = this.getFontSize();
			r.drawRectangle(bounds.getX() + this.getCursorX(), bounds.getY() + this.getTextY() - fontSize, this.getCursorWidth(), fontSize);
		}
	}
	
	/** @return The text to render when not showing the hint */
	public String getDisplayText(){
		return new StringBuilder(this.getLabel()).append(this.getCurrentText()).toString();
	}
	
	@Override
	public double getTextX(){
		return super.getTextX() + this.getTextOffset();
	}
	
	/** @return The relative x coordinate of the cursor */
	public double getCursorX(){
		return this.getTextX() + this.getCursorLocation() + (this.getCurrentText().isEmpty() ? this.hintWidth : this.labelWidth);
	}
	
	/** @return The space between the end of this text box and where the string will start to shift over to the left to keep the end of the string visible */
	public double getTextLimit(){
		double w = this.getWidth();
		return Math.max(w - 10, w * 0.9) - this.getCursorWidth();
	}
	
	/** @return See {@link #currentText} */
	public String getCurrentText(){
		return this.currentText;
	}
	
	/**
	 * Do not call directly to set the text inside this text box, use {@link #setCurrentText(String)} instead
	 */
	@Override
	public void setText(String text){
		super.setText(text);
	}
	
	/** @param currentText See {@link #currentText} */
	public void setCurrentText(String currentText){
		this.currentText = currentText;
		this.updateLetterBounds();
		this.textWidth = this.letterBounds[this.getCurrentText().length()].getWidth();
		if(this.cursorIndex > currentText.length() - 1) this.setCursorIndex(currentText.length() - 1);
		this.getTextBuffer().setText(this.getCurrentText());
		this.setText(this.getCurrentText());
	}
	
	/** Update the current state of {@link #letterBounds} based on the values in this object */
	private void updateLetterBounds(){
		GameFont f = this.getFont();
		this.letterBounds = f.characterBounds(this.getRelX() + this.getTextX(), this.getRelY() + this.getTextY(), this.getDisplayText(), 1);
	}
	
	/** @return See {@link #textColor} */
	public ZColor getTextColor(){
		return this.textColor;
	}
	
	/** @param textColor See {@link #textColor} */
	public void setTextColor(ZColor textColor){
		this.textColor = textColor;
	}
	
	/** @return See {@link #textWidth} */
	public double getTextWidth(){
		return this.textWidth;
	}
	
	/** @return See {@link #textOffset} */
	public double getTextOffset(){
		return this.textOffset;
	}
	
	/** @return See {@link #hint} */
	public String getHint(){
		return this.hint;
	}
	
	/** @param hint See {@link #hint} */
	public void setHint(String hint){
		this.hint = hint;
		this.updateHintWidth();
	}
	
	/** Recalculate the value of {@link #hintWidth} */
	private void updateHintWidth(){
		this.hintWidth = this.getFont().stringWidth(this.getHint());
	}
	
	/** @return See {@link #label} */
	public String getLabel(){
		return this.label;
	}
	
	/** @param label See {@link #label} */
	public void setLabel(String label){
		this.label = label;
		this.updateLabelWidth();
	}
	
	/** Recalculate the value of {@link #labelWidth} */
	private void updateLabelWidth(){
		this.labelWidth = this.getFont().stringWidth(this.getLabel());
		this.updateLetterBounds();
	}
	
	@Override
	public void setFont(GameFont font){
		super.setFont(font);
		this.updateLabelWidth();
		this.updateHintWidth();
	}
	
	/** @return See {@link #hintColor} */
	public ZColor getHintColor(){
		return this.hintColor;
	}
	
	/** @param hintColor See {@link #hintColor} */
	public void setHintColor(ZColor hintColor){
		this.hintColor = hintColor;
	}
	
	/** @return See {@link #cursorWidth} */
	public double getCursorWidth(){
		return this.cursorWidth;
	}
	
	/** @param cursorWidth See {@link #cursorWidth} */
	public void setCursorWidth(double cursorWidth){
		this.cursorWidth = cursorWidth;
	}
	
	/** @return See {@link #cursorColor} */
	public ZColor getCursorColor(){
		return this.cursorColor;
	}
	
	/** @param cursorColor See {@link #cursorColor} */
	public void setCursorColor(ZColor cursorColor){
		this.cursorColor = cursorColor;
	}
	
	/** @return See {@link #blinkTime} */
	public double getBlinkTime(){
		return this.blinkTime;
	}
	
	/** @param blinkTime See {@link #blinkTime} */
	public void setBlinkTime(double blinkTime){
		this.blinkTime = blinkTime;
	}
	
	/** @return See {@link #currentBlinkTime} */
	public double getCurrentBlinkTime(){
		return this.currentBlinkTime;
	}
	
	/** @return See {@link #blinkCursor} */
	public boolean isBlinkCursor(){
		return this.blinkCursor;
	}
	
	/** @return See {@link #cursorIndex} */
	public int getCursorIndex(){
		return this.cursorIndex;
	}
	
	/** @param cursorIndex See {@link #cursorIndex}. If the index is out of bounds of {@link #getCurrentText()}, it goes on the end of the string it's closest to */
	public void setCursorIndex(int cursorIndex){
		// Keep the cursor index in bounds
		if(cursorIndex < -1) cursorIndex = -1;
		var text = this.getCurrentText();
		if(cursorIndex >= text.length()) cursorIndex = text.length() - 1;
		
		// Reset the blinking time
		if(cursorIndex != this.cursorIndex){
			this.blinkCursor = true;
			this.currentBlinkTime = 0;
		}
		// Update the new index
		this.cursorIndex = cursorIndex;
		
		// issue#9 replace this stringWidth call with a reference from this.letterBounds
		double newCursorLoc = this.getFont().stringWidth(text.substring(0, this.getCursorIndex() + 1));
		
		// If the text takes up less space than the text box, position it so that the beginning of the text aligns with the beginning of the box
		if(this.getTextWidth() < this.getTextLimit()){
			this.textOffset = 0;
		}
		// If the new location of the cursor would place it outside the text box, reposition the text so that the cursor will be inside the text box
		else{
			double cursorRel = newCursorLoc + this.getTextOffset();
			// issue#10 make this reposition it by only one character
			if(cursorRel > this.getTextLimit() || cursorRel < 0) this.textOffset = this.getTextLimit() - newCursorLoc;
			// If the location would put the beginning of the string after the beginning of the box, reposition it so that it will be at the beginning
			if(this.textOffset > 0) this.textOffset = 0;
		}
		// Update the cursor location
		this.cursorLocation = this.cursorIndex < 0 ? 0 : newCursorLoc;
	}
	
	/** Move the cursor one character left. Does nothing if the cursor is already all the way left */
	public void cursorLeft(){
		this.setCursorIndex(this.getCursorIndex() - 1);
	}
	
	/** Move the cursor one character right. Does nothing if the cursor is already all the way right */
	public void cursorRight(){
		this.setCursorIndex(this.getCursorIndex() + 1);
	}
	
	/** @return See {@link #cursorLocation} */
	public double getCursorLocation(){
		return this.cursorLocation;
	}
	
	/** @return See {@link #mode} */
	public Mode getMode(){
		return this.mode;
	}
	
	/** @param mode See {@link #mode} */
	public void setMode(Mode mode){
		this.mode = mode;
	}
	
}
