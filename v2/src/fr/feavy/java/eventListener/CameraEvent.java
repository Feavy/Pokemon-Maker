package fr.feavy.java.eventListener;

import fr.feavy.java.map.Map;

public interface CameraEvent {
	
	public void cameraStartMovingEvent(int cameraX, int cameraY, int direction);
	
	public void cameraEndMovingEvent(int cameraX, int cameraY, int direction);
	
	public void cameraReachBorderEvent(Map sideMap);
	
}
