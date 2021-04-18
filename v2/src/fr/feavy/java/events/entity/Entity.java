package fr.feavy.java.events.entity;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import fr.feavy.java.Main;
import fr.feavy.java.data.Direction;
import fr.feavy.java.events.Event;

public class Entity extends Event{

	protected int facing = -1;
	private boolean isTrainer = false;
	private int[] absoluteLocation = new int[2];
	protected int movementType = 0;
	protected int[] currentSprite = new int[4];
	protected boolean isMirror = false;
	private int range = 0;
	
	private boolean isMoving = false;
	
	private Thread movingThread;
	private MovingAnimationThread movingRunnable;
	
	private final int SPRITE_MOVEMENT_SPEED = 140;
	
	protected Image sprites;
	
	/**
	 * 
	 * @param location lLocation of the entity
	 * @param spriteName Name of the sprite file (without extension)
	 * @param facing Direction where the entity is facing
	 * @param movementType Entity's Movement type
	 * @param isTrainer Is the entity a trainer
	 * @param range If trainer: range of view
	 */
	public Entity(int[] location, String spriteName, int facing, int movementType, boolean isTrainer, int range){
		
		this.isTrainer = isTrainer;
		this.movementType = movementType;
		this.absoluteLocation = location;
		this.range = range;
		try {
			sprites = ImageIO.read(getClass().getResource("/sprites/"+spriteName+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param isPlayer Create entity player
	 * @throws IOException
	 */
	public Entity(boolean isPlayer) throws IOException{
		this.absoluteLocation = new int[]{0,0};
		sprites = ImageIO.read(getClass().getResource("/sprites/player.png"));
		
		setFacingDirection(3);
	}
	
	/**
	 * Start the moving animation
	 */
	public void startMovingAnimation(){
		
		isMoving = true;
		
		movingRunnable = new MovingAnimationThread();
		movingThread = new Thread(movingRunnable);
		movingThread.start();
	}
	
	/**
	 * Stop the moving animation
	 */
	public void stopMovingAnimation(){
		isMoving = false;
		movingRunnable.stopMoving();
	}
	
	/**
	 * Get default location of the entity
	 * @return
	 */
	public int[] getAbsLocation(){
		return absoluteLocation;
	}
	
	public void setFacingDirection(int facing){
		this.facing = facing;
		isMirror = false;
		
		switch (facing) {
		case Direction.UP:
			currentSprite = new int[]{16,32,32,48};
			break;
		case Direction.DOWN:
			currentSprite = new int[]{16,16,32,32};
			break;
		case Direction.RIGHT:
			currentSprite = new int[]{16,0,0,16};
			isMirror = true;
			break;
		case Direction.LEFT:
			currentSprite = new int[]{0,0,16,16};
			break;
		}
	}
	
	/**
	 * @return true if npc's movement animation is running, false if not
	 */
	public boolean isMovementAnimationRunning(){
		return isMoving;
	}
	
	/**
	 * @return direction where the entity is facing
	 */
	public int getFacingDirection(){
		return facing;
	}
	
	/**
	 * Draw the entity on screen
	 * @param g Graphics of the game screen
	 */
	public void draw(Graphics g){
		
	}
	
	/**
	 * Moving animation
	 * @author Feavy
	 *
	 */
	class MovingAnimationThread implements Runnable{
		
		private boolean change = false;
		private int stop = 0;
		
		private int movementStep = 0;
		
		public MovingAnimationThread(){
			isMoving = true;
		}
		
		public void run() {
			
			while(true){
				
				isMirror = false;
				if(stop == 2) movementStep = 0;
				if(stop == 1) movementStep = 1;
				
				if(facing == Direction.RIGHT || facing == Direction.LEFT){
					
					if(facing == Direction.RIGHT){
						currentSprite = new int[]{16+16*movementStep,0,16*movementStep,16};
						isMirror = true;
					}else
						currentSprite = new int[]{16*movementStep,0,16+16*movementStep,16};
					
					movementStep = (movementStep == 1) ? movementStep-1 : movementStep+1;
					
				}else{
					
					isMirror = (movementStep == 2);
					
					if(facing == Direction.DOWN)
						currentSprite = (movementStep == 0) ? new int[]{16,16,32,32} : (movementStep == 1) ? new int[]{0,16,16,32} : new int[]{16,16,0,32};
					else
						currentSprite = (movementStep == 0) ? new int[]{16,32,32,48} : (movementStep == 1) ? new int[]{0,32,16,48} : new int[]{16,32,0,48};
					
					movementStep = (movementStep != 0) ? 0 : (change) ? 2 : 1;
					
					if(movementStep == 0)
						change = !change;
					
				}

				Main.refreshScreen();

				if(stop > 0)
					stop++;
			
				if(stop == 3)
					return;
				
				try {
					Thread.sleep(SPRITE_MOVEMENT_SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("animation_runnable, stop="+stop);
			}
			
		}
		
		public void stopMoving(){
			stop = 1;
		}
		
	}
	
}
