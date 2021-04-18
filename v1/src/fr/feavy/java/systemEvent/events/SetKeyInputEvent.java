package fr.feavy.java.systemEvent.events;

public class SetKeyInputEvent extends Event{

	private boolean activated;
	
	public SetKeyInputEvent(boolean activated) {
		super("setKeyInputEvent");
		this.activated = activated;
	}

	public boolean newStade(){ return activated; };
	
}
