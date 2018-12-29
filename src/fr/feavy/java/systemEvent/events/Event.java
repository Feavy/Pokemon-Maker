package fr.feavy.java.systemEvent.events;

public abstract class Event {

	private String eventID;
	
	public Event(String eventID){
		this.eventID = eventID;
	}
	
	public String getID(){
		return eventID;
	}
	
}
