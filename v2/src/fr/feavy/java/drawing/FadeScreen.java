package fr.feavy.java.drawing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import fr.feavy.java.rendering.GameScreen;

public class FadeScreen extends Animation{

	public FadeScreen(GameScreen display){
		super(display);
	}

	public void start() {
		
		GameScreen display = super.getDisplay();
		
		Graphics2D g = (Graphics2D)display.getGraphics();
		AlphaComposite ac;
		g.setColor(Color.BLACK);
		for(int i = 0; i < 100; i++){
			ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(i*0.001));
			g.setComposite(ac);
			g.fillRect(0, 0, display.getWidth(), display.getHeight());
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
