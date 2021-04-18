package fr.feavy.java.systemEvent.listeners;

import fr.feavy.java.Main;
import fr.feavy.java.systemEvent.events.Event;

public interface EventListener {
	
	public abstract void onEvent(Event e);
	
	public void callEvent(Event e);
}
