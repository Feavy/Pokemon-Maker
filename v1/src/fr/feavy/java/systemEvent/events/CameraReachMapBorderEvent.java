package fr.feavy.java.systemEvent.events;

public class CameraReachMapBorderEvent extends Event{

	private int direction;
	
	public CameraReachMapBorderEvent(int direction) {
		super("cameraReachMapBorderEvent");
		this.direction = direction;
	}

	public int getDirection(){ return direction; }
	
}
