package fr.feavy.java.drawing;


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

import fr.feavy.java.Main;
import fr.feavy.java.data.TallGrassTiles;

public class Tiles {

	private static HashMap<Integer, Tile> tiles = new HashMap<Integer, Tile>();
	
	public static void initialize(Main main){
		List<Tile> animatedTiles = new ArrayList<Tile>();
		BufferedImage img;
		Tile t;
		for(int j = 0; j < 1444; j++){
			try{
				img = getImage("/tiles/"+ Integer.toHexString(j) + ".png");
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
	
	public static BufferedImage getImage(String path) throws IOException{
		return ImageIO.read(Main.class.getResource(path));
	}
	
	public static Image getTile(int id){
		return tiles.get(id).getImage();
	}
	
	public static boolean isTallGrass(int id){
		for(TallGrassTiles t : TallGrassTiles.values())
			if(t.id() == id)return true;
		return false;
	}
	
	// --= TILES ANIMATION =--
	
	static class AnimationRunnable implements Runnable{
		
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
				
				if(Main.getGameDisplay() != null)
					Main.getGameDisplay().repaint();
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
