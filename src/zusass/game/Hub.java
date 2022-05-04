package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.things.Door;
import zgame.things.Room;
import zgame.things.entity.Player;
import zgame.things.entity.Tile;

/** The {@link Room} which represents the main hub of the game, i.e. where the player can enter levels, make items, etc. */
public class Hub extends Room{
	
	/** The number of tiles in a {@link Hub} on the x axis */
	private static final int X_TILES = 24;
	/** The number of tiles in a {@link Hub} on the y axis */
	private static final int Y_TILES = 14;

	/** The object for the main character the player controls */
	private Player player;
	
	/** Create the hub in the default state */
	public Hub(){
		super();
		this.initTiles(X_TILES, Y_TILES);
		
		for(int i = 0; i < X_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				boolean i0 = i % 2 == 0;
				boolean j0 = j % 2 == 0;
				this.getTiles()[i][j] = new Tile(i, j, (i0 == j0) ? new ZColor(.2) : new ZColor(.3));
			}
		}
		
		this.player = new Player(0, 875, 75, 125);
		this.player.setLockCamera(true);
		this.addThing(this.player);
		
		Door levelDoor = new Door(500, 0);
		levelDoor.setY(this.getY() + this.getHeight() - levelDoor.getHeight());
		// TODO make this not a magic number, i.e. make a reliable way to position the player
		levelDoor.setLeadRoom(new LevelRoom(1), 0, 387.0);
		this.addThing(levelDoor);
	}
	
	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
	}
	
}
