package fr.feavy.java.entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;

public class NPC extends Entity{

	public NPC(Point location, String spriteName, int facingDirection, boolean isTrainer, int range)
			throws IOException {
		super(location, spriteName, facingDirection, isTrainer, range, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

}
