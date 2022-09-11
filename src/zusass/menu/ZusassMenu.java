package zusass.menu;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.menu.Menu;
import zusass.ZusassData;

/** A base menu class for {@link Menu} in the {@link zusass.ZusassGame} */
public abstract class ZusassMenu extends Menu<ZusassData>{
	
	/** The title of this {@link ZusassMenu} */
	private String title;
	
	/** The x coordinate of the title */
	private double titleX;
	
	/** The y coordinate of the title */
	private double titleY;
	
	/**
	 * Create the new menu with the given title
	 * @param title See {@link #title}
	 */
	public ZusassMenu(String title){
		this(title, 600, 110);
	}
	
	/**
	 * Create the new menu with the given title and position of the title
	 * @param title See {@link #title}
	 * @param x See {@link #titleX}
	 * @param y See {@link #titleY}
	 */
	public ZusassMenu(String title, double x, double y){
		// TODO make title a buffer
		this.title = title;
		this.titleX = x;
		this.titleY = y;
	}
	
	@Override
	public void renderBackground(Game<ZusassData> game, Renderer r){
		super.renderBackground(game, r);
		
		// Background color
		r.setColor(0.2, 0.2, 0.2);
		r.fill();
		
		// Title
		r.setColor(new ZColor(.8));
		r.setFont(game.getFont("zfont"));
		r.setFontSize(100);
		r.drawText(this.getTitleX(), this.getTitleY(), this.getTitle());
	}
	
	/** @return See {@link #title} */
	public String getTitle(){
		return this.title;
	}
	
	/** @param title See {@link #title} */
	public void setTitle(String title){
		this.title = title;
	}
	
	/** @return See {@link #titleX} */
	public double getTitleX(){
		return this.titleX;
	}
	
	/** @param titleX See {@link #titleX} */
	public void setTitleX(double titleX){
		this.titleX = titleX;
	}
	
	/** @return See {@link #titleY} */
	public double getTitleY(){
		return this.titleY;
	}
	
	/** @param titleY See {@link #titleY} */
	public void setTitleY(double titleY){
		this.titleY = titleY;
	}
	
}
