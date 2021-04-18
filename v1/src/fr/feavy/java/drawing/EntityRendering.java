package fr.feavy.java.drawing;

import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.listeners.EventListener;

import java.awt.Graphics2D;

import fr.feavy.java.Main;
import fr.feavy.java.entity.Player;

public class EntityRendering extends Rendering implements EventListener{

	public EntityRendering(Player player) {
		Main.addEventListener(this);
		super.player = player;
	}
	
	@Override
	public void draw(Graphics2D g) {
		player.draw(g);
	}

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}

}
