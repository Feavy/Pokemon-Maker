package fr.feavy.java.constants;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.plaf.SliderUI;

import fr.feavy.java.Main;
import fr.feavy.java.drawing.GameScreen;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.events.RefreshScreenEvent;
import fr.feavy.java.systemEvent.listeners.EventListener;

public class Tiles implements EventListener{

	private static HashMap<Integer, Tile> tiles = new HashMap<Integer, Tile>();
	private static int[] tallGrassIDs = new int[3];
	
	public void initialize(){
		
		Main.addEventListener(this);
		
		tallGrassIDs[0] = 39;
		tallGrassIDs[1] = 77;
		tallGrassIDs[2] = 608;
		
		List<Tile> animatedTiles = new ArrayList<Tile>();
		BufferedImage img;
		Tile t;
		for(int j = 0; j < 1444; j++){
			try{
				img = ImageIO.read(Main.class.getResource("/tiles/"+ Integer.toHexString(j) + ".png"));
				t = new Tile(img, img.getWidth()>16);
				if(t.isAnimated())
					animatedTiles.add(t);
				tiles.put(j, t);
			}catch(Exception e){
				
			}
		}
		
		Thread th = new Thread(new AnimationRunnable(animatedTiles.toArray(new Tile[animatedTiles.size()])));
		th.start();
		
	}

	public static Image getTile(int id){
		return tiles.get(id).getImage();
	}
	
	public static boolean isTallGrass(int id){
		for(int i : tallGrassIDs)
			if(i == id)return true;
		return false;
	}
	
	// --= TILES ANIMATION =--
	
	class AnimationRunnable implements Runnable{
		
		private Tile[] animatedTiles;
		private int step = 0;
		private int loopStep = 0;
		
		private boolean increase = true;
		
		public AnimationRunnable(Tile[] animatedTiles) {
			this.animatedTiles = animatedTiles;
		}
		
		@Override
		public void run() {
			while(true){
								
				for(Tile t : animatedTiles)
					t.setStep(t.isAnimationLoop() ? loopStep : step);
				
				step++;
				if(step > 3 )step = 0;
				
				if(increase)
					loopStep++;
				else
					loopStep--;
				
				if(loopStep == 4)increase = false;
				else if(loopStep == 0)increase = true;
				
				callEvent(new RefreshScreenEvent());
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onEvent(Event e) {
		
	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}
	
}
