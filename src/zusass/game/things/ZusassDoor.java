package zusass.game.things;

import zgame.core.Game;
import zgame.core.input.mouse.ZMouseInput;
import zgame.core.utils.ZRect;
import zgame.things.still.Door;
import zusass.ZusassGame;
import zusass.game.things.entities.mobs.ZusassPlayer;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door{
	
	/**
	 * Create a new door at the given position
	 * 
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public ZusassDoor(double x, double y){
		super(x, y, false);
	}

	@Override
	public void tick(Game game, double dt){
		super.tick(game, dt);
		this.handleDoorPress((ZusassGame)game);
	}
	
	/**
	 * Utility method for {@link #tick(Game, double)} for checking if the player clicked a door
	 * If the player is attempting to click on a door, have the player enter the door, otherwise do nothing
	 * 
	 * @param game The game used by the tick method
	 */
	private void handleDoorPress(ZusassGame game){
		ZMouseInput mi = game.getMouseInput();

		// Find the player
		ZusassPlayer player = game.getCurrentRoom().getPlayer();
		if(player == null) return;
		boolean pressed = player.isEnterRoomPressed();
		// If the button to enter a door is marked as pressed, but the button is up, then do not enter the door
		if(!pressed || mi.leftDown()) return;
		
		// Otherwise, check if the player intersects the door, and the player clicked on the door, then enter it
		ZRect dBounds = this.getBounds();
		if(!dBounds.intersects(player.getBounds()) || !dBounds.contains(game.mouseGX(), game.mouseGY())) return;
		this.enterRoom(game.getCurrentRoom(), player, game);
	}
	
}
