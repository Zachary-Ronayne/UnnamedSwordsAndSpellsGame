package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.still.tiles.BaseTiles;
import zgame.things.still.tiles.Tile;
import zgame.world.Room;
import zusass.ZusassData;
import zusass.ZusassGame;
import zusass.game.things.LevelDoor;
import zusass.game.things.entities.ZusassPlayer;

/** The {@link Room} which represents the main hub of the game, i.e. where the player can enter levels, make items, etc. */
public class Hub extends Room{
	
	/** The number of tiles in a {@link Hub} on the x axis */
	private static final int X_TILES = 24;
	/** The number of tiles in a {@link Hub} on the y axis */
	private static final int Y_TILES = 14;
	
	/** The object for the main character the player controls */
	private ZusassPlayer player;
	
	/**
	 * Create the hub in the default state
	 * 
	 * @param zgame The {@link Game} using this hub
	 */
	public Hub(ZusassGame zgame){
		super();
		this.initTiles(X_TILES, Y_TILES);
		
		for(int i = 0; i < X_TILES; i++){
			for(int j = 0; j < Y_TILES; j++){
				boolean i0 = i % 2 == 0;
				boolean j0 = j % 2 == 0;
				this.setTile(i, j, (i0 == j0) ? BaseTiles.BACK_LIGHT : BaseTiles.BACK_DARK);
			}
		}
		// The door to start at the highest level gotten to
		this.setTile(9, 10, BaseTiles.WALL_DARK);
		this.setTile(6, 11, BaseTiles.WALL_DARK);
		Tile t = this.getTile(9, 10);
		
		double doorX = t.getX();
		ZusassData data = zgame.getData();
		LevelDoor highDoor = new LevelDoor(doorX, 0, data.getHighestRoomLevel(), this);
		highDoor.setY(t.getY() - highDoor.getHeight());
		this.addThing(highDoor);
		
		// The door to start from level 1
		LevelDoor levelDoor = new LevelDoor(doorX, 0, 1, this);
		levelDoor.setY(this.maxY() - levelDoor.getHeight());
		this.addThing(levelDoor);
		
		// Placing the player
		this.player = new ZusassPlayer();
		this.player.setX(20);
		this.player.setY(this.maxY() - this.player.getHeight());
		this.player.setLockCamera(true);
		this.addThing(this.player);
		this.player.centerCamera(zgame);
	}
	
	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
	}
	
}
