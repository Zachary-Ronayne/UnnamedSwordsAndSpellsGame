package zgame.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.GameFont;
import zgame.core.utils.ZRect;
import zgame.core.utils.ZStringUtils;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A {@link MenuThing} that can be used to have a user type things in
 */
public class MenuTextBox<D>extends MenuButton<D>{
	
	/** true if this {@link MenuTextBox} is selected and will accept text input, false otherwise */
	private boolean selected;
	
	/** The amount of distance the text of this {@link MenuTextBox} will render to modify what part of the string is visible */
	private double textOffset;

	/** The current width of the text of this text box */
	private double textWidth;
	
	/** The text to show as a hint of what should be typed in the text box */
	private String hint;
	
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
	
	/**
	 * Create a new {@link MenuTextBox} with the given values
	 * 
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 * @param game The game associated with this thing
	 */
	public MenuTextBox(double x, double y, double w, double h, Game<D> game){
		super(x, y, w, h, game);
		this.selected = false;
		this.setTextX(5);
		this.setTextY(this.getHeight() - 5);
		this.setFont(new GameFont(null, 20, 0, 0));
		this.textOffset = 0;
		this.textWidth = 0;
		
		this.hint = "";
		this.hintColor = new ZColor(.5);
		
		this.cursorWidth = 5;
		this.cursorColor = new ZColor(0, .5);
		this.blinkTime = .7;
		this.currentBlinkTime = 0;
		this.blinkCursor = false;
	}
	
	@Override
	public void tick(Game<D> game, double dt){
		super.tick(game, dt);
		this.currentBlinkTime += dt;
		if(this.getCurrentBlinkTime() > this.getBlinkTime()){
			this.blinkCursor = !this.blinkCursor;
			this.currentBlinkTime -= this.getBlinkTime();
		}
	}
	
	@Override
	public void mouseAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.mouseAction(game, button, press, shift, alt, ctrl);
		this.setSelected(this.getBounds().contains(game.mouseSX(), game.mouseSY()));
	}
	
	@Override
	public void keyAction(Game<D> game, int button, boolean press, boolean shift, boolean alt, boolean ctrl){
		super.keyAction(game, button, press, shift, alt, ctrl);
		if(!this.isSelected()) return;
		if(press) return;
		
		if(button == GLFW_KEY_BACKSPACE){
			String t = this.getText();
			if(t.length() > 0) this.setText(t.substring(0, t.length() - 1));
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
			case GLFW_KEY_L -> toAdd = 'k';
			case GLFW_KEY_K -> toAdd = 'l';
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
		}
		if(toAdd != null){
			if(shift){ if('a' <= toAdd && toAdd <= 'z') toAdd = Character.toUpperCase(toAdd); }
			this.setText(ZStringUtils.concat(this.getText(), toAdd));
		}
	}
	
	/** @return See {@link #selected} */
	public boolean isSelected(){
		return this.selected;
	}
	
	/** @param selected See {@link #selected} */
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	@Override
	public void render(Game<D> game, Renderer r){
		this.updateTextOffset(r);
		super.render(game, r);

		if(this.getText().isEmpty()){
			r.setColor(this.getHintColor());
			this.drawText(r, this.getHint());
		}
		
		if(this.isSelected() && this.isBlinkCursor()){
			r.setColor(this.getCursorColor());
			double fontSize = this.getFontSize();
			r.drawRectangle(this.getX() + this.getTextX() + this.getTextWidth(), this.getY() + this.getTextY() - fontSize, this.getCursorWidth(), fontSize);
		}
	}
	
	/**
	 * Update the current value of {@link #textOffset} based on the state of the given {@link Renderer}
	 * 
	 * @param r The renderer
	 */
	private void updateTextOffset(Renderer r){
		if(this.getTextWidth() > this.getTextLimit()) this.textOffset = this.getTextLimit() - this.getTextWidth();
		else this.textOffset = 0;
	}
	
	@Override
	public double getTextX(){
		return super.getTextX() + this.textOffset;
	}
	
	/** @return The space between the end of this text box and where the string will start to shift over to the left to keep the end of the string visible */
	public double getTextLimit(){
		double w = this.getWidth();
		return Math.max(w - 10, w * 0.9) - this.getCursorWidth();
	}

	@Override
	public void setText(String text){
		super.setText(text);
		this.textWidth = this.getFont().stringWidth(this.getText());
	}

	/** @return See {@link #textWidth} */
	public double getTextWidth(){
		return this.textWidth;
	}
	
	/** @return See {@link #hint} */
	public String getHint(){
		return this.hint;
	}
	
	/** @param hint See {@link #hint} */
	public void setHint(String hint){
		this.hint = hint;
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
	
	/** @param cursorWidth See {@link #cursorColor} */
	public void setCursorColor(ZColor cursorColor){
		this.cursorColor = cursorColor;
	}
	
	/** @return See {@link #blinkTime} */
	public double getBlinkTime(){
		return this.blinkTime;
	}
	
	/** @param cursorWidth See {@link #blinkTime} */
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
	
}
