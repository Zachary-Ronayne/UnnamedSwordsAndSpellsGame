package zusass.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.font.TextBuffer;
import zgame.core.utils.ZRect;
import zgame.menu.Menu;
import zgame.menu.MenuText;
import zusass.ZusassGame;

/** A base menu class for {@link Menu} in the {@link ZusassGame} */
public abstract class ZusassMenu extends Menu{
	
	/** A thing holding the text to display the title */
	private final MenuText titleThing;
	
	/**
	 * Create the new menu with the given title
	 *
	 * @param title The text for {@link #titleThing}
	 */
	public ZusassMenu(ZusassGame zgame, String title){
		this(zgame, title, 600, 110);
	}
	
	/**
	 * Create the new menu with the given title and position of the title
	 *
	 * @param title The text for {@link #titleThing}
	 * @param x The x position of the title
	 * @param y The y position of the title
	 */
	public ZusassMenu(ZusassGame zgame, String title, double x, double y){
		super(0, 0, zgame.getScreenWidth(), zgame.getScreenHeight(), false);
		this.setFill(new ZColor(0.2, 0.2, 0.2));
		
		this.titleThing = new MenuText(0, 0, zgame.getScreenWidth(), zgame.getScreenHeight(), zgame);
		this.titleThing.setText(title);
		this.titleThing.setFont(zgame.getDefaultFont().size(100));
		this.titleThing.setTextX(x);
		this.titleThing.setTextY(y);
		this.titleThing.invisible();
		this.titleThing.setFontColor(new ZColor(.8));
		this.addThing(this.titleThing);
	}
	
	/** @return See {@link #titleThing} */
	public MenuText getTitleThing(){
		return this.titleThing;
	}
	
	/** @return The text of the title */
	public String getTitle(){
		return this.getTitleThing().getText();
	}
	
	/** @param title The new text for the title */
	public void setTitle(String title){
		this.getTitleThing().setText(title);
	}
	
	/** @return The x position of the title */
	public double getTitleX(){
		return this.getTitleThing().getTextX();
	}
	
	/** @param titleX The new text x position of the title */
	public void setTitleX(double titleX){
		this.getTitleThing().setTextX(titleX);
	}
	
	/** @return The y position of the title */
	public double getTitleY(){
		return this.getTitleThing().getTextY();
	}
	
	/** @param titleY The new text y position of the title */
	public void setTitleY(double titleY){
		this.getTitleThing().setTextY(titleY);
	}
	
}
