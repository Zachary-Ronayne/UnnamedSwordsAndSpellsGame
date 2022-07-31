package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.entity.Player;
import zgame.things.still.tiles.BaseTiles;
import zgame.world.Room;
import zusass.game.things.LevelDoor;

/** The {@link Room} which represents the main hub of the game, i.e. where the player can enter levels, make items, etc. */
public class Hub extends Room{
	
	/** The number of tiles in a {@link Hub} on the x axis */
	private static final int X_TILES = 24;
	/** The number of tiles in a {@link Hub} on the y axis */
	private static final int Y_TILES = 14;
	
	/** The object for the main character the player controls */
	private Player player;
	
	/**
	 * Create the hub in the default state
	 * 
	 * @param game The {@link Game} using this hub
	 */
	public Hub(Game game){
		super();
		this.initTiles(X_TILES, Y_TILES);
		
		for(int i = 0; i < X_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				boolean i0 = i % 2 == 0;
				boolean j0 = j % 2 == 0;
				this.setTile(i, j, (i0 == j0) ? BaseTiles.BACK_LIGHT : BaseTiles.BACK_DARK);
			}
		}
		this.player = new Player(0, 875, 75, 125);
		this.player.setLockCamera(true);
		this.addThing(this.player);
		this.player.centerCamera(game);
		
		LevelDoor levelDoor = new LevelDoor(500, 0, 1, this);
		levelDoor.setY(this.maxY() - levelDoor.getHeight());
		this.addThing(levelDoor);
	}
	
	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
	}
	
}
