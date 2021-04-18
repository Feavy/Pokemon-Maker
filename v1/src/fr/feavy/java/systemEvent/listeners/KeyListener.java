package fr.feavy.java.systemEvent.listeners;

import java.awt.event.KeyEvent;

import fr.feavy.java.Main;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.events.KeyPressedEvent;
import fr.feavy.java.systemEvent.events.KeyReleasedEvent;
import fr.feavy.java.systemEvent.events.SetKeyInputEvent;

public class KeyListener implements EventListener, java.awt.event.KeyListener{

	private int currentKeyPressed = -1;
	private static final long SPAM_COOLDOWN = 100;
	private static long lastPress = 0;
	private boolean activated;
	
	public KeyListener() {
		Main.addEventListener(this);
		activated = true;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(!activated)return;
		long currentTime = System.currentTimeMillis();
		if(currentTime-lastPress > SPAM_COOLDOWN){
			if(e.getKeyCode() != currentKeyPressed){
				currentKeyPressed = e.getKeyCode();
				callEvent(new KeyPressedEvent(e.getKeyCode()));
				lastPress = currentTime;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		currentKeyPressed = -1;
		callEvent(new KeyReleasedEvent(e.getKeyCode()));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onEvent(Event e) {
		if(e.getID().equals("setKeyInputEvent")){
			activated = ((SetKeyInputEvent)e).newStade();
			currentKeyPressed = -1;
		}
		
	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}

}
