package zusass.game.things;

import zgame.core.Game;
import zgame.core.utils.ZRect2D;
import zgame.things.ThingClickDetector;
import zgame.things.type.bounds.Bounds2D;
import zusass.ZusassGame;

/** A {@link ThingClickDetector} used for the Zusass game */
public interface ZThingClickDetector extends ThingClickDetector, Bounds2D{
	
	@Override
	default ZRect2D getThingBounds(){
		return this.getBounds();
	}
	
	@Override
	default ZRect2D getPlayerBounds(Game game){
		var zgame = (ZusassGame)game;
		var player = zgame.getPlayer();
		if(player == null) return null;
		return player.getBounds();
	}
	
	@Override
	default boolean handlePress(Game game){
		var zgame = (ZusassGame)game;
		if(!ThingClickDetector.super.handlePress(zgame)) return false;
		return handleZPress(zgame);
	}
	
	/**
	 * Called when this thing in the Zusass game is clicked
	 * @param zgame The game where the click happened
	 * @return true if this was activated, false otherwise
	 */
	boolean handleZPress(ZusassGame zgame);
}
