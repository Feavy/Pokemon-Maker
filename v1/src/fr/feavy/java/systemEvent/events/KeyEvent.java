package fr.feavy.java.systemEvent.events;

public class KeyEvent extends Event{

	private int keyCode;
	
	public KeyEvent(String eventID, int keyCode) {
		super(eventID);
		this.keyCode = keyCode;
	}

	public boolean isKeyArrow(){
		return (keyCode >= 37 && keyCode <= 40);
	}
	
	public int getKeyCode(){
		return keyCode;
	}
	
}
