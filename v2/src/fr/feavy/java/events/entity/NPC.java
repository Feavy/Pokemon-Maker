package fr.feavy.java.events.entity;

import java.awt.Graphics;

import fr.feavy.java.Main;
import fr.feavy.java.data.Direction;
import fr.feavy.java.events.entity.MovementManager.MovementRunnable;
import fr.feavy.java.map.Map;
import fr.feavy.java.rendering.GameScreen;

public class NPC extends Entity {

	private int x, y;
	private int xSpriteScale, ySpriteScale;

	private MovingRunnable movementAnimationRunnable;
	private MovementRunnable movementRunnable;

	private Map parrentMap;

	private NPC instance;

	private GameScreen display;

	private boolean isLoaded = true;

	public NPC(Map parrentMap, int[] location, String spriteName, int facing, int movementType, boolean isTrainer, int range) {
		super(location, spriteName, facing, movementType, isTrainer, range);
		this.x = location[0];
		this.y = location[1];
		this.xSpriteScale = 0;
		this.ySpriteScale = 0;
		this.parrentMap = parrentMap;
		instance = this;
		super.setFacingDirection(facing);
	}

	/**
	 * Initialize the NPC
	 * 
	 * @param display
	 *            Instance of the game screen
	 */
	public void initialize(GameScreen display) {
		System.out.println("load");
		this.display = display;
		movementRunnable = MovementManager.getMovementRunnable(this, movementType);
		new Thread(movementRunnable).start();
		isLoaded = true;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	/**
	 * Unload the NPC
	 */
	public void unLoad() {
		System.out.println("unload");
		isLoaded = false;
		if(isMovementAnimationRunning())
			stopMovingAnimation();
		movementRunnable.stopMovement();
		movementRunnable = null;
		if (movementAnimationRunnable != null)
			movementAnimationRunnable.stopMovement();
	}

	/**
	 * Get NPC current location
	 * 
	 * @return int array containing abscissa and ordinate of its location
	 */
	public int[] getLocation() {
		return new int[] { x, y };
	}

	/**
	 * Move the player with a specified direction
	 * 
	 * @param direction
	 *            direction where to move
	 */
	public boolean move(int direction, int stepCount, boolean isContinuous) {

		if(!isLoaded)
			return false;
		
		if (movementAnimationRunnable != null)
			if (movementAnimationRunnable.isRunning())
				return false;
		setFacingDirection(direction);
		movementAnimationRunnable = new MovingRunnable(direction, stepCount);
		if (movementAnimationRunnable.canMove()) {
			new Thread(movementAnimationRunnable).start();
			if (!isContinuous) {
				startMovingAnimation();
				stopMovingAnimation();
			} else if (isContinuous && !isMovementAnimationRunning())
				startMovingAnimation();
			return true;
		}
		return false;
	}

	/**
	 * Draw the NPC on screen
	 * 
	 * @param g
	 *            Graphics instance of the game screen
	 * @param cameraX
	 *            Camera abscissa
	 * @param cameraY
	 *            Camera ordinate
	 * @param xScale
	 *            horizontal shift of the camera
	 * @param yScale
	 *            vertical shift of the camera
	 */
	public void draw(Graphics g, int cameraX, int cameraY, int xScale, int yScale) {

		int diffX = x - cameraX;
		int diffY = y - cameraY;
		g.drawImage(sprites, /* dx1 */ ((isMirror) ? (4 + diffX) * 64 - 5 : (4 + diffX) * 64 + 3) + xScale - xSpriteScale,
				/* dy1 */ (4 + diffY) * 64 - yScale + ySpriteScale - 16,
				/* dx2 */ ((isMirror) ? (4 + diffX + 1) * 64 - 5 : (4 + diffX + 1) * 64 + 3) + xScale - xSpriteScale,
				/* dy2 */ (5 + diffY) * 64 - yScale + ySpriteScale - 16, currentSprite[0], currentSprite[1], currentSprite[2], currentSprite[3], null);
	}

	/**
	 * NPC movement animation
	 * 
	 * @author Feavy
	 *
	 */
	class MovingRunnable implements Runnable {

		private int changeXScale, changeYScale, stepRemaining;
		private boolean stop = false;

		public MovingRunnable(int direction, int stepCount) {
			this.stepRemaining = stepCount;
			changeXScale = 0;
			changeYScale = 0;
			if (direction == Direction.RIGHT)
				changeXScale = -1;
			else if (direction == Direction.LEFT)
				changeXScale = 1;
			else if (direction == Direction.DOWN)
				changeYScale = 1;
			else
				changeYScale = -1;
		}

		public boolean canMove() {

			if (x - display.getCameraX() - changeXScale < -5 || x - display.getCameraX() - changeXScale > 6)
				return false;
			else if (Math.abs(y - display.getCameraY() + changeYScale) > 5)
				return false;
			else if (parrentMap.getMovement(x - changeXScale, y + changeYScale) == 1
					|| Math.abs(x - display.getCameraX() - changeXScale) + Math.abs(y - display.getCameraY() + changeYScale) == 0)
				return false;

			return true;

		}

		public void run() {

			do {

				if (!canMove()) {
					continue;
				}
				
				parrentMap.addNPC(x+changeXScale, y+changeYScale, instance);
				
				for (int i = 1; i <= 64; i++) {

					if (changeXScale > 0)
						xSpriteScale += GameScreen.FRAME_SKIP + 1;
					else if (changeXScale < 0)
						xSpriteScale -= GameScreen.FRAME_SKIP + 1;
					else if (changeYScale > 0)
						ySpriteScale += GameScreen.FRAME_SKIP + 1;
					else if (changeYScale < 0)
						ySpriteScale -= GameScreen.FRAME_SKIP + 1;

					Main.refreshScreen();

					try {
						Thread.sleep(GameScreen.SCROLLING_SPEED * (GameScreen.FRAME_SKIP + 1));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				parrentMap.removeNPC(x, y, instance);
				x -= xSpriteScale / 64;
				y += ySpriteScale / 64;
				xSpriteScale = 0;
				ySpriteScale = 0;
				stepRemaining--;

			} while (stepRemaining != 0 && !stop);
			
			stop = true;

		}
		
		public boolean isRunning() {
			return !stop;
		}
		
		public void stopMovement(){
			stop = true;
		}
		
	}

}
