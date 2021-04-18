package fr.feavy.java.entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import fr.feavy.java.Main;
import fr.feavy.java.map.events.Event;
import fr.feavy.java.systemEvent.events.EntitySpriteChangedEvent;
import fr.feavy.java.systemEvent.events.PlayerAnimationFinishedEvent;
import fr.feavy.java.systemEvent.listeners.EventListener;

public abstract class Entity extends Event implements EventListener{

	private Point location;
	protected Sprite sprites;
	protected int facingDirection;
	private boolean isTrainer;
	private int range;
	private boolean isPlayer;
	
	private AnimationRunnable animationRunnable;
	
	public Entity(Point location, String spriteName, int facingDirection, boolean isTrainer, int range, boolean isPlayer) throws IOException{
		this.location = location;
		this.facingDirection = facingDirection;
		this.isTrainer = isTrainer;
		this.range = range;
		this.sprites = new Sprite(ImageIO.read(getClass().getResource("/sprites/"+spriteName+".png")));
		this.isPlayer = isPlayer;
		animationRunnable = new AnimationRunnable();
	}
	
	@Override
	public void onEvent(fr.feavy.java.systemEvent.events.Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callEvent(fr.feavy.java.systemEvent.events.Event e) {
		Main.callEvent(e);
	}
	
	public void startMovingAnimation(){
		animationRunnable = new AnimationRunnable();
		new Thread(animationRunnable).start();
	}
	
	public void stopMovingAnimation(){
		animationRunnable.stop();
	}
	
	public boolean isMovingAnimationActive(){
		return animationRunnable.isRunning();
	}
	
	public boolean isMovingAnimationTerminated(){
		return animationRunnable.isTerminated();
	}
	
	public void setFacingDirection(int direction){
		facingDirection = direction;
		sprites.setSpriteLookingDirection(direction);
	}
	
	public abstract void draw(Graphics2D g);
	
	private class AnimationRunnable implements Runnable{

		private boolean running = false;
		private boolean isTerminated;
		
		public AnimationRunnable() {
			running = false;
			isTerminated = true;
		}
		
		@Override
		public void run() {
			running = true;
			isTerminated = false;
			while(running){
				paintNextSprite();
			}
			do{
				paintNextSprite();
			}while(!sprites.isDefaultSprite());
			if(isPlayer)
				callEvent(new PlayerAnimationFinishedEvent());
			isTerminated = true;
		}
		
		private void paintNextSprite(){
			sprites.nextSprite();
			
			callEvent(new EntitySpriteChangedEvent());
			
			try {
				Thread.sleep(160);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public boolean isRunning(){ return running; }
		
		public boolean isTerminated(){ return isTerminated; }
		
		public void stop(){ running = false; }
		
	}
	
}
