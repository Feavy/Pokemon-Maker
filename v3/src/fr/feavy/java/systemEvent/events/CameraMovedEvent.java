package fr.feavy.java.systemEvent.events;

import java.awt.Point;

public class CameraMovedEvent extends Event{

	private Point oldLocation;
	private Point newLocation;
	private boolean isBlocked;
	private int direction;
	
	public CameraMovedEvent(Point oldLocation, Point newLocation, int direction, boolean isBlocked) {
		super("cameraMovedEvent");
		this.newLocation = newLocation;
		this.oldLocation = oldLocation;
		this.isBlocked = isBlocked;
		this.direction = direction;
	}
	
	public Point getOldLocation(){ return oldLocation; }
	public Point getNewLocation(){ return newLocation; }
	
	public boolean isBlocked(){ return isBlocked; }
	public int getDirection(){ return direction; }
	
}
