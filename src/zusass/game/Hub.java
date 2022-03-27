package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.utils.ZRenderUtils;
import zgame.things.Door;
import zgame.things.Room;
import zgame.things.entity.Player;

/** The {@link Room} which represents the main hub of the game, i.e. where the player can enter levels, make items, etc. */
public class Hub extends Room{
	
	/** The object for the main character the player controls */
	private Player player;
	
	/** Create the hub in the default state */
	public Hub(){
		super();
		this.setWidth(2000);
		this.setHeight(1000);
		
		this.player = new Player(0, 875, 75, 125);
		this.player.setLockCamera(true);
		this.addThing(this.player);

		Door levelDoor = new Door(500, 850);
		levelDoor.setLeadRoom(new LevelRoom(1), 0, 375);
		this.addThing(levelDoor);
	}
	
	@Override
	public void render(Game game, Renderer r){
		ZRenderUtils.checkerboard(r, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 40, 20, new ZColor(.2), new ZColor(.3));
		super.render(game, r);
	}
	
}
