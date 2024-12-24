package zusass.game;

import zgame.core.Game;
import zgame.core.graphics.Renderer;
import zgame.things.still.tiles.BaseTiles3D;
import zgame.things.type.GameThing;
import zgame.world.Room;
import zusass.ZusassGame;
import zusass.game.things.ZusassTags;
import zusass.game.things.entities.mobs.ZusassMob;

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
		
		// TODO add the door
		// The door to start at the highest level gotten to
//		this.setTile(9, 10, BaseTiles2D.WALL_DARK);
//		this.setTile(6, 11, BaseTiles2D.WALL_DARK);
//		Tile2D t = this.getTile(9, 10);
//
//		double doorX = t.getX();
//		ZusassData data = zgame.getData();
//		LevelDoor highDoor = new LevelDoor(doorX, 0, data.getHighestRoomLevel(), this);
//		highDoor.setY(t.getY() - highDoor.getHeight());
//		this.addThing(highDoor);
//
//		// The door to start from level 1
//		LevelDoor levelDoor = new LevelDoor(doorX, 0, 1, this);
//		levelDoor.setY(this.maxY() - levelDoor.getHeight());
//		this.addThing(levelDoor);
		
		// TODO add the spell maker
//		// Add the spell maker
//		var spellMaker = new SpellMakerThing(zgame, 100, 0);
//		spellMaker.setY(this.maxY() - spellMaker.getHeight());
//		this.addThing(spellMaker);
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
