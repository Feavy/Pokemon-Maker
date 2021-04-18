package fr.feavy.java.drawing;

import java.awt.Graphics2D;
import java.awt.Point;

import fr.feavy.java.entity.Player;

public abstract class Rendering {

	protected static Point cameraLocation = new Point(0,0);
	protected static int xOffset = 0;
	protected static int yOffset = 0;
	protected static Player player;
	
	public abstract void draw(Graphics2D g);
	
}
