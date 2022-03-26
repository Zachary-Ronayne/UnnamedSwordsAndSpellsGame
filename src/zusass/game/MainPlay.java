package zusass.game;

import zgame.core.state.PlayState;
import zgame.things.entity.Player;

/**
 * The main {@link PlayState} used by the ZUSASS game
 * I initially called this ZUSASSPlay, but I um... changed it
 */
public class MainPlay extends PlayState{
	
	public MainPlay(){
		this.getCurrentRoom().addThing(new Player(0, 0, 75, 125));
	}
	
}
