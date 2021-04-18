package fr.feavy.java.events;

import fr.feavy.java.Main;
import fr.feavy.java.data.Direction;
import fr.feavy.java.drawing.FadeScreen;
import fr.feavy.java.map.Map;
import fr.feavy.java.rendering.GameScreen;

public class WarpEvent extends RunnableEvent{

	public WarpEvent(){

	}
	
	public void callEvent(Warp w){
		GameScreen display = Main.getGameDisplay();
		display.startAnimation(new FadeScreen(display));
		Map m = Main.getProject().getMap(w.getMapID());
		display.warpCameraTo(m, w.getX(), w.getY());
		display.stopAnimation();
	}
	
}
