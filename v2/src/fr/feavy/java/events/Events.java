package fr.feavy.java.events;

import java.util.ArrayList;
import java.util.List;

import fr.feavy.java.events.RunnableEvent;
import fr.feavy.java.events.WarpEvent;

public class Events {

	public static boolean _init = init();
	
	public static List<RunnableEvent> events;
	
	public static final int WARP = 0;
	
	/**
	 * Initialize all the event
	 * @return
	 */
	public static boolean init(){
		events = new ArrayList<RunnableEvent>();
		events.add(new WarpEvent());
		return true;
	}
	
}
