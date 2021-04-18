package fr.feavy.java.map.events;

public class WarpEvent implements RunnableEvent{

	@Override
	public void callEvent(Event e) {
		
		Warp w = (Warp)e;
		
		/*screen.startAnimation(new FadeScreen(screen));
		Map m = Main.getProject().getMap(w.getMapID());
		screen.warpCameraTo(m, w.getX(), w.getY());
		screen.stopAnimation();*/
		}
	
}
