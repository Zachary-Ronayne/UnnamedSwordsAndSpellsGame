package zgame.things;

import java.util.Collection;

import zgame.core.Game;
import zgame.core.GameTickable;
import zgame.core.graphics.Renderer;
import zgame.things.entity.EntityThing;

public class Door extends PositionedThing implements RectangleBounds, GameTickable{
	
	/** The default value of {@link #width} */
	public static final double WIDTH = 70;
	/** The default value of {@link #height} */
	public static final double HEIGHT = 150;
	
	/** The width of this door */
	private double width;
	/** The height of this door */
	private double height;
	
	/** The {@link Room} which this door leads to. Can be null to make this a real fake door */
	private Room room;
	/** The x coordinate to place objects which go through this door */
	private double roomX;
	/** The y coordinate to place objects which go through this door */
	private double roomY;
	
	/**
	 * Create a new door at the given position
	 * 
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public Door(double x, double y){
		super(x, y);
		this.width = WIDTH;
		this.height = HEIGHT;
		
		this.setLeadRoom(null, 0, 0);
	}
	
	/**
	 * Set the place this {@link Door} leads to
	 * 
	 * @param r See {@link #room}
	 * @param x See {@link #roomX}
	 * @param y See {@link #roomY}
	 */
	public void setLeadRoom(Room r, double x, double y){
		this.room = r;
		this.roomX = x;
		this.roomY = y;
	}
	
	/**
	 * Move the given {@link PositionedThing} from the given room to {@link #room}
	 * 
	 * @param r The room which the thing is coming from, can be null if there is no room the thing is coming from
	 * @param thing The thing to move
	 * @param game The {@link Game} where this room entering takes place
	 */
	public void enterRoom(Room r, PositionedThing thing, Game game){
		if(r != null) r.removeThing(thing);
		if(this.room != null){
			thing.setX(this.roomX);
			thing.setY(this.roomY);
			thing.enterRoom(r, this.room, game);
		}
	}
	
	@Override
	public double getWidth(){
		return this.width;
	}
	
	@Override
	public double getHeight(){
		return this.height;
	}
	
	@Override
	public void tick(Game game, double dt){
		Collection<EntityThing> entities = game.getCurrentRoom().getEntities();
		// Check every entity and if it touches this door, move it to this Room
		for(EntityThing e : entities) if(e.intersects(this.getX(), this.getY(), this.getWidth(), this.getHeight())) this.enterRoom(game.getCurrentRoom(), e, game);
	}
	
	@Override
	public void render(Game game, Renderer r){
		r.setColor(.25, .125, 0);
		r.drawRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
}
