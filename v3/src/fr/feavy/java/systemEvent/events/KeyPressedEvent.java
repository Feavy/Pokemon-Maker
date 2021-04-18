package fr.feavy.java.systemEvent.events;

public class KeyPressedEvent extends KeyEvent{
	
	public KeyPressedEvent(int keyCode) {
		super("keyPressedEvent", keyCode);
	}
	
}
