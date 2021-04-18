package fr.feavy.java.systemEvent.events;

import java.awt.Point;

public class CameraScrollingChangedEvent extends Event{

	private Point oldDest, newDest;
	
	public CameraScrollingChangedEvent(Point oldDest, Point newDest) {
		super("cameraScrollingChangedEvent");
		this.oldDest = oldDest;
		this.newDest = newDest;
	}
	
	public Point getOldDest(){ return oldDest; }
	public Point getNewDest(){ return newDest; }

}
