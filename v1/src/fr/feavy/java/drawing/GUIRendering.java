package fr.feavy.java.drawing;

import java.awt.Graphics2D;

import fr.feavy.java.Main;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.listeners.EventListener;

public class GUIRendering extends Rendering implements EventListener{

	public GUIRendering() {
		Main.addEventListener(this);
	}
	
	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}
	
}
