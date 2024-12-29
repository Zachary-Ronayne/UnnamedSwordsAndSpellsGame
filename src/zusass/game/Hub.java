package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.still.tiles.BaseTiles3D;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.ZusassGame;
import zusass.game.things.LevelDoor;
import zusass.game.things.ZusassTags;
import zusass.game.things.entities.mobs.ZusassMob;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** The {@link Room} which represents the main hub of the game, i.e. where the player can enter levels, make items, etc. */
public class Hub extends ZusassRoom{
	
	/** The number of tiles in a {@link Hub} on the x axis */
	private static final int X_TILES = 8;
	/** The number of tiles in a {@link Hub} on the y axis */
	private static final int Y_TILES = 5;
	/** The number of tiles in a {@link Hub} on the z axis */
	private static final int Z_TILES = 8;
	
	/**
	 * Create the hub in the default state
	 *
	 * @param zgame The {@link Game} using this hub
	 */
	public Hub(ZusassGame zgame){
		super();
		this.initTiles(X_TILES, Y_TILES, Z_TILES, BaseTiles3D.AIR);
		this.setTileBoundaries();
		this.setAllBoundaries(true);
		// TODO make the tiles not just air and use the textured tile
		
		// TODO make this be something that makes sense
		// Make a floor
		for(int i = 0; i < X_TILES; i++){
			for(int k = 0; k < Z_TILES; k++){
				this.setTile(i, 0, k, (i % 2 == k % 2) ? BaseTiles3D.SOLID_DARK : BaseTiles3D.SOLID_LIGHT);
			}
		}
		
		// The door to start at the highest level gotten to
		var t = this.getTile(1, 1, 1);
		var data = zgame.getData();
		var highDoor = new LevelDoor(t.getX(), 1, t.getZ(), data.getHighestRoomLevel(), this);
		this.addThing(highDoor);
		
		// The door to start from level 1
		t = this.getTile(3, 1, 1);
		var levelDoor = new LevelDoor(t.getX(), 1, t.getZ(), 0, this);
		this.addThing(levelDoor);
		
		// TODO add the spell maker
//		// Add the spell maker
//		var spellMaker = new SpellMakerThing(zgame, 100, 0);
//		spellMaker.setY(this.maxY() - spellMaker.getHeight());
//		this.addThing(spellMaker);
	}
	
	/**
	 * Put the current player of the game into the hub at the default position
	 * @param zgame The game to use
	 */
	public void placePlayer(ZusassGame zgame){
		var player = zgame.getPlayer();
		player.setX(2);
		player.setY(1);
		player.setZ(3);
		this.addThing(player);
		player.updateCameraPos(zgame.getCamera3D());
	}
	
	@Override
	public void addThing(GameThing thing){
		super.addThing(thing);
		if(thing.hasTag(ZusassTags.HUB_ENTER_RESTORE)) ((ZusassMob)thing).setResourcesMax();
	}
	
	@Override
	public void render(Game game, Renderer r){
		super.render(game, r);
	}
	
}
