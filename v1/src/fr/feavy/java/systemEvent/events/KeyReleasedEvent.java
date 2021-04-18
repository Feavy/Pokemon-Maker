package fr.feavy.java.systemEvent.events;

public class KeyReleasedEvent extends KeyEvent{

	public KeyReleasedEvent(int keyCode) {
		super("keyReleasedEvent", keyCode);
	}

}
