package zusass.game.things.entities.mobs;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.entity.EntityThing;
import zgame.things.entity.MobThing;
import zgame.things.type.RectangleHitBox;
import zusass.ZusassGame;

/** A generic mob which uses health, status, etc */
public abstract class ZusassMobRect extends MobThing implements RectangleHitBox, StatThing{
	
	/** The width of this mob */
	private double width;
	/** The height of this mob */
	private double height;
	
	/** The stats used by this mob */
	private Stats stats;
	
	/**
	 * Create a new mob with the given bounds
	 * 
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 * @param width The mob's width
	 * @param height The mob's height
	 */
	public ZusassMobRect(double x, double y, double width, double height){
		super(x, y);
		this.width = width;
		this.height = height;
		
		this.stats = new Stats();
	}
	
	@Override
	public void tick(Game game, double dt){
		ZusassGame zgame = (ZusassGame)game;
		this.tickStats(zgame, dt);
		
		super.tick(game, dt);
	}
	
	@Override
	protected void render(Game game, Renderer r){
		// Temporary simple rendering
		r.setColor(0, .5, 0);
		r.drawRectangle(this.getBounds());
	}
	
	@Override
	public Stats getStats(){
		return this.stats;
	}
	
	@Override
	public EntityThing get(){
		return this;
	}
	
	/** @return See {@link #width} */
	@Override
	public double getWidth(){
		return this.width;
	}
	
	/** @param width See {@link #width} */
	public void setWidth(double width){
		this.width = width;
	}
	
	/** @return See {@link #height} */
	@Override
	public double getHeight(){
		return this.height;
	}
	
	/** @param height See {@link #height} */
	public void setHeight(double height){
		this.height = height;
	}
	
}
