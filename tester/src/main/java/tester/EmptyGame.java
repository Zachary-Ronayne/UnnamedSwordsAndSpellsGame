package tester;

import zgame.core.Game;

// TODO fix not creating global settings automatically/logging a stack trace when it was able to create it
// TODO fix SoundManager not initialized on get being logged when init sounds is false

/** A default {@link Game} that does nothing, here as a test for if the most basic version of a game works */
public class EmptyGame extends Game{
	
	public EmptyGame(){
		super();
		this.setInitSounds(false);
	}
	
	public static void main(String[] args){
		new EmptyGame().start();
	}
}
