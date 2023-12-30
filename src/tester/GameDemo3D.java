package tester;

import zgame.core.Game;

public class GameDemo3D extends Game{
	
	public static void main(String[] args){
		var game = new GameDemo3D();
		game.getWindow().center();
		
		game.start();
	}
	
	@Override
	public String getGlobalSettingsLocation(){
		return "./testGame/settings";
	}
}
