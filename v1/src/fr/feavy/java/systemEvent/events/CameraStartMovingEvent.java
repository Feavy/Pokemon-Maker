package fr.feavy.java.systemEvent.events;

public class CameraStartMovingEvent extends Event{

	public CameraStartMovingEvent(int currentX, int currentY, int direction) {
		super("cameraStartMovingEvent");
	}

}
