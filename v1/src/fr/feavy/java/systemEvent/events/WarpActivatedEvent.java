package fr.feavy.java.systemEvent.events;

import fr.feavy.java.map.events.Warp;

public class WarpActivatedEvent extends Event{

	private Warp w;
	
	public WarpActivatedEvent(Warp w) {
		super("warpActivatedEvent");
		this.w = w;
	}
	
	public Warp getWarp(){ return w; }
	
}
