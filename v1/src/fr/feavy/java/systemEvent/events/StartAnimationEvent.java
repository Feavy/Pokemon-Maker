package fr.feavy.java.systemEvent.events;

import fr.feavy.java.drawing.animations.Animation;

public class StartAnimationEvent extends Event{

	private Animation anim;
	
	public StartAnimationEvent(Animation anim) {
		super("startAnimationEvent");
		this.anim = anim;
	}

	public Animation getAnimation(){ return anim; }
	
}
