package tester;

import zgame.core.Game;

/** A default {@link Game} that does nothing, here as a test for if the most basic version of a game works */
public class EmptyGame extends Game{
	public static void main(String[] args){
		new EmptyGame().start();
	}
}
