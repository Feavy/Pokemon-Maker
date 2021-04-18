package fr.feavy.java.drawing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import fr.feavy.java.Main;
import fr.feavy.java.entity.Player;
import fr.feavy.java.map.Map;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.events.StartAnimationEvent;
import fr.feavy.java.systemEvent.listeners.EventListener;
import fr.feavy.java.systemEvent.listeners.KeyListener;

public class GameScreen extends JPanel implements EventListener{

	public final static int MAP_RENDERING_INDEX = 0;
	public final static int ENTITY_RENDERING_INDEX = 1;
	public final static int GUI_RENDERING_INDEX = 2;
	
	private Rendering[] renderings = new Rendering[3];
	
	private boolean initialized = false;
	
	private boolean isAnimationRunning = false;
	
	public GameScreen(Map map, Player player){
		Main.addEventListener(this);
		isAnimationRunning = false;
		renderings[0] = new MapRendering(map);
		renderings[1] = new EntityRendering(player);
		renderings[2] = new GUIRendering();
	}
	
	@Override
	public void repaint() {
		if(isAnimationRunning)
			return;
		super.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(initialized)
			draw((Graphics2D)g);
		
	}
	
	private void draw(Graphics2D g) {
		for(Rendering r : renderings)
			r.draw(g);
	}
	
	public Rendering getRendering(int index){ return renderings[index]; }
	
	public void initialized(){ initialized = true; }

	@Override
	public void onEvent(Event e) {
		if(e.getID().equals("refreshScreenEvent")){
			repaint();
		}else if(e.getID().equals("entitySpriteChangedEvent")){
			repaint();
		}else if(e.getID().equals("startAnimationEvent")){
			isAnimationRunning = true;
			StartAnimationEvent sae = (StartAnimationEvent)e;
			sae.getAnimation().start(this);
			isAnimationRunning = false;
			repaint();
		}
	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}
	
}
