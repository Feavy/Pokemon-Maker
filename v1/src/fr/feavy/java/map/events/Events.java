package fr.feavy.java.map.events;

import java.util.ArrayList;
import java.util.List;

import fr.feavy.java.drawing.GameScreen;

public class Events {
	
	public static List<RunnableEvent> events;
	
	public static final int WARP = 0;
	
	/**
	 * Initialize all the event
	 * @return
	 */
	public static boolean initialize(){
		events = new ArrayList<RunnableEvent>();
		events.add(new WarpEvent());
		return true;
	}
	
}
