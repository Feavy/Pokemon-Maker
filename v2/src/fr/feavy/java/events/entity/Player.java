package fr.feavy.java.events.entity;

import java.awt.Graphics;
import java.io.IOException;

import fr.feavy.java.Main;
import fr.feavy.java.rendering.GameScreen;

public class Player extends Entity{
	
	private GameScreen display;
	private boolean updated = false;
	
	public Player() throws IOException{
		super(true);
		this.display = Main.getGameDisplay();
	}
	
	/**
	 * Start player movement animation
	 * @param facing Direction where start moving
	 */
	public void startMoving(int facing) {
		
		super.facing = facing;
		if(!display.isMapSet())return;
		display.setScrollingDirection(facing);
		super.startMovingAnimation();
		updated = false;
	}
	
	/**
	 * @return return false if player facing needs an update, true if not
	 */
	public boolean isFacingUpdated(){
		return updated;
	}
	
	/**
	 * Update player facing
	 */
	public void updateFacing(){
		setFacingDirection(facing);
		updated = true;
	}
	
	/**
	 * Stop moving animation / Stop scrolling
	 */
	public void stopMoving(){
		if(!display.isMapSet())return;
		display.stopScrolling();
		super.stopMovingAnimation();
	}
	
	public void draw(Graphics g){
		g.drawImage(sprites, (isMirror) ? 4*64-5 : 4*64+3, 4*60, (isMirror) ? 5*64-5 : 5*64+3, 4*60+64, currentSprite[0], currentSprite[1], currentSprite[2], currentSprite[3], null);
	}
	
}
