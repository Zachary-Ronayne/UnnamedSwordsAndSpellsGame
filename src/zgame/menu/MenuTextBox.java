package zgame.menu;

import zgame.core.Game;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A {@link MenuThing} that can be used to have a user type things in
 */
public class MenuTextBox<D>extends MenuButton<D>{

	/** true if this {@link MenuTextBox} is selected and will accept text input, false otherwise */
	private boolean selected;

	/**
	 * Create a new {@link MenuTextBox} with the given values
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param w See {@link #getWidth()}
	 * @param h See {@link #getHeight()}
	 */
	public MenuTextBox(double x, double y, double w, double h){
		super(x, y, w, h);
		this.selected = false;
		this.setTextX(5);
		this.setTextY(this.getHeight() - 5);
		this.setFontSize(20);
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
		}
		if(toAdd != null){
			if(shift) toAdd = Character.toUpperCase(toAdd);
			this.setText(this.getText() + toAdd);
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
	
}
