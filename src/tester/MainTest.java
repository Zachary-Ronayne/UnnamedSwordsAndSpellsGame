package tester;

import zgame.GameWindow;

/**
 * A simple main class used for testing the game code
 */
public class MainTest{

	public static void main(String[] args){
		GameWindow window = new GameWindow("test", 1280, 720, 200, true, false);
		window.start();
	}
	
}
