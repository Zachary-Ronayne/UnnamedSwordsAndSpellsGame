package zusass.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZRect;
import zgame.menu.Menu;
import zusass.ZusassGame;

/** A base menu class for {@link Menu} in the {@link ZusassGame} */
public abstract class ZusassMenu extends Menu{

	/** A text buffer holding the text to display the title */
	private TextBuffer titleBuffer;
	
	/**
	 * Create the new menu with the given title
	 * 
	 * @param title See {@link #title}
	 */
	public ZusassMenu(ZusassGame zgame, String title){
		this(zgame, title, 600, 110);
	}
	
	/**
	 * Create the new menu with the given title and position of the title
	 * 
	 * @param title See {@link #title}
	 * @param x The x position of the title
	 * @param y The y position of the title
	 */
	public ZusassMenu(ZusassGame zgame, String title, double x, double y){
		super(0, 0, zgame.getScreenWidth(), zgame.getScreenHeight(), false);
		this.setFill(new ZColor(0.2, 0.2, 0.2));
		
		this.titleBuffer = new TextBuffer((int)zgame.getScreenWidth(), (int)zgame.getScreenHeight());
		this.titleBuffer.setFont(zgame.getDefaultFont().size(100));
		this.titleBuffer.setTextX(x);
		this.titleBuffer.setTextY(y);
		this.setTitle(title);
	}

	@Override
	public void destroy(){
		this.titleBuffer.destroy();
		super.destroy();
	}

	@Override
	public void renderSelf(Game game, Renderer r, ZRect bounds){
		super.renderSelf(game, r, bounds);
		
		// Title
		r.setColor(new ZColor(.8));
		this.getTitleBuffer().drawToRenderer(0, 0, r);
	}

	/** @return See {@link #titleBuffer} */
	public TextBuffer getTitleBuffer(){
		return this.titleBuffer;
	}
	
	/** @return The text of the title */
	public String getTitle(){
		return this.getTitleBuffer().getText();
	}
	
	/** @param title The new text for the title */
	public void setTitle(String title){
		this.getTitleBuffer().setText(title);
	}
	
	/** @return The x position of the title */
	public double getTitleX(){
		return this.getTitleBuffer().getTextX();
	}
	
	/** @param titleX The new text x position of the title */
	public void setTitleX(double titleX){
		this.getTitleBuffer().setTextX(titleX);
	}
	
	/** @return The y position of the title */
	public double getTitleY(){
		return this.titleBuffer.getTextY();
	}
	
	/** @param titleY The new text y position of the title */
	public void setTitleY(double titleY){
		this.getTitleBuffer().setTextY(titleY);
	}
	
}
