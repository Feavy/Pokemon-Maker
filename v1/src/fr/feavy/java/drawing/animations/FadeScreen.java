package fr.feavy.java.drawing.animations;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import fr.feavy.java.drawing.GameScreen;

public class FadeScreen extends Animation{

	public FadeScreen(){
		super();
	}

	public void start(GameScreen screen) {
		
		Graphics2D g = (Graphics2D)screen.getGraphics();
		AlphaComposite ac;
		g.setColor(Color.BLACK);
		for(int i = 0; i < 100; i++){
			ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(i*0.001));
			g.setComposite(ac);
			g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
