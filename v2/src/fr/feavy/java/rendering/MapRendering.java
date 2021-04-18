package fr.feavy.java.rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import fr.feavy.java.Main;
import fr.feavy.java.data.Direction;
import fr.feavy.java.debug.FrameCalculator;
import fr.feavy.java.drawing.Animation;
import fr.feavy.java.drawing.Tiles;
import fr.feavy.java.events.Event;
import fr.feavy.java.events.Events;
import fr.feavy.java.events.Warp;
import fr.feavy.java.events.entity.NPC;
import fr.feavy.java.map.Map;

public class MapRendering implements Rendering {

	private int xScale = 0, yScale = 0, cameraX = 0, cameraY = 0;

	private int changeXScale, changeYScale;

	ScrollingRunnable runnable;
	boolean threadAlive = false;

	private Map map, sideMap;

	private int direction = -1;

	boolean animationRunning = false;

	boolean onTopEvent = false;

	FrameCalculator fc;

	MapRendering instance;

	MapRendering mapRendering;
	EntityRendering entityRendering;
	GUIRendering guiRendering;

	private GameScreen screen;

	public MapRendering(GameScreen screen, Map map, int x, int y) {
		runnable = new ScrollingRunnable(0);
		this.map = map;
		setCamera(x, y);
		this.screen = screen;
	}

	public Map getCurrentMap() {
		return map;
	}

	/**
	 * Function used to teleport the player to a location of a specified map
	 * (Teleportation mod is Warp)
	 * 
	 * @param m
	 *            Map where the player will be teleported
	 * @param x
	 *            Abscissa of the location
	 * @param y
	 *            Ordinate of the location
	 */
	public void warpCameraTo(Map m, int x, int y) {
		setMap(m);
		setCamera(x, y);
		if (map.isEvent(x, y) && !onTopEvent) {
			Event e = map.getEvent(x, y);
			if (e instanceof Warp) {
				Main.getPlayer().stopMovingAnimation();
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						Main.getPlayer().startMoving(Main.getPlayer().getFacingDirection());
					}
				}, 100l);
			}
		}

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (Main.getKeyPressed() != -1) {
					Main.getPlayer().setFacingDirection(Main.getKeyPressed() - 37);
					setScrollingDirection(Main.getKeyPressed() - 37);
				} else
					Main.getPlayer().stopMoving();
			}
		}, 200l);

	}

	/**
	 * Set the camera to a new position
	 * 
	 * @param x
	 *            Abscissa of the location
	 * @param y
	 *            Ordinate of the location
	 */
	void setCamera(int x, int y) {
		cameraX = x;
		cameraY = y;
	}

	/**
	 * @return abscissa of the camera's location
	 */
	public int getCameraX() {
		return cameraX;
	}

	/**
	 * @return ordinate of the camera's location
	 */
	public int getCameraY() {
		return cameraY;
	}

	/**
	 * Play a animation (Game is frozen while playing it)
	 * 
	 * @param anim
	 *            Animation which will be played
	 */
	public void startAnimation(Animation anim) {
		animationRunning = true;
		anim.start();
	}

	/**
	 * Stop a started animation and unfreeze the game
	 */
	public void stopAnimation() {
		animationRunning = false;
	}

	/**
	 * GAME RENDER
	 */
	@Override
	public void render(Graphics g) {

		/**
		 * 
		 * --= MAP =--
		 * 
		 **/

		if (sideMap != null)
			if (cameraY == -1 || cameraY == map.height() || cameraX == -1 || cameraX == map.width())
				switchMap(direction);

		if (cameraY <= 4 && direction != Direction.UP && map.isMapSet(Direction.UP)) {
			sideMap = map.getSideMap(Direction.UP);
			direction = Direction.UP;
		} else if (cameraY + 5 >= map.height() && direction != Direction.DOWN && map.isMapSet(Direction.DOWN)) {
			sideMap = map.getSideMap(Direction.DOWN);
			direction = Direction.DOWN;
		} else if (cameraX <= 4 && direction != Direction.LEFT && map.isMapSet(Direction.LEFT)) {
			sideMap = map.getSideMap(Direction.LEFT);
			direction = Direction.LEFT;
		} else if (cameraX + 6 >= map.width() && direction != Direction.RIGHT && map.isMapSet(Direction.RIGHT)) {
			sideMap = map.getSideMap(Direction.RIGHT);
			direction = Direction.RIGHT;
		} else if (direction == Direction.DOWN && cameraY + 5 < map.height() || direction == Direction.UP && cameraY > 4) {
			sideMap = null;
			direction = -1;
		} else if (direction == Direction.RIGHT && cameraX + 6 < map.width() || direction == Direction.LEFT && cameraX > 4) {
			sideMap = null;
			direction = -1;
		}

		int[][] visiblesTiles = map.getTiles(cameraX - 5, cameraY - 5, 12, 11);
		int[][] sideVisibleTiles = null;

		if (sideMap != null)
			if (direction == Direction.UP || direction == Direction.DOWN)
				sideVisibleTiles = sideMap.getTiles(cameraX - 5, cameraY - 5 + ((direction == Direction.UP) ? sideMap.height() : -map.height()), 12, 11);
			else
				sideVisibleTiles = sideMap.getTiles(cameraX - 5 + ((direction == Direction.LEFT) ? sideMap.width() : -map.width()), cameraY - 5, 12, 11);

		boolean playerDrawn = false;

		for (int k = -1; k < 10; k++)
			for (int j = -1; j < 11; j++) {

				if (j == 0 && ((k == 4 + changeYScale) || (k == 4 && changeYScale == 1)) && !playerDrawn)
					if (Tiles.isTallGrass(map.getTileID(cameraX - changeXScale, cameraY + changeYScale)) && Tiles.isTallGrass(map.getTileID(cameraX, cameraY))) {
						Main.getPlayer().draw(g);
						playerDrawn = true;
					}

				if (visiblesTiles[j + 1][k + 1] != -1) {
					g.drawImage(Tiles.getTile(visiblesTiles[j + 1][k + 1]), j * 64 + xScale, k * 64 - yScale, 64, 64, null);
				} else {
					if (sideVisibleTiles != null) {
						if (sideVisibleTiles[j + 1][k + 1] != -1)
							g.drawImage(Tiles.getTile(sideVisibleTiles[j + 1][k + 1]), j * 64 + xScale, k * 64 - yScale, 64, 64, null);
						else if ((direction == Direction.UP && cameraY + k + 1 <= 4) || (direction == Direction.DOWN && cameraY + k + 1 > map.height() + 4)
								|| (cameraX + j + 1 <= 4 && direction == Direction.LEFT) || (cameraX + j + 1 > map.width() + 6 && direction == Direction.RIGHT))
							g.drawImage(Tiles.getTile(sideMap.getBorderTileID()), j * 64 + xScale, k * 64 - yScale, 64, 64, null);
						else
							g.drawImage(Tiles.getTile(map.getBorderTileID()), j * 64 + xScale, k * 64 - yScale, 64, 64, null);
					} else {
						g.drawImage(Tiles.getTile(map.getBorderTileID()), j * 64 + xScale, k * 64 - yScale, 64, 64, null);
					}
				}
			}

		if (!playerDrawn)
			Main.getPlayer().draw(g);

	}

	/**
	 * Switching map when player reaches the border of the current map
	 * 
	 * @param side
	 *            Direction of the map in which the player will be
	 */
	void switchMap(int side) {
		screen.cameraReachBorderEvent(sideMap);
		map = sideMap;
		sideMap = null;
		direction = -1;
		if (side == Direction.UP)
			cameraY = map.height() - 1;
		else if (side == Direction.DOWN)
			cameraY = 0;
		else if (side == Direction.RIGHT)
			cameraX = 0;
		else if (side == Direction.LEFT)
			cameraX = map.width() - 1;

	}

	/**
	 * Start/change map scrolling
	 * 
	 * @param direction
	 *            New scrolling direction
	 */
	public void setScrollingDirection(int direction) {

		if (threadAlive)
			runnable.restart(direction);
		else {
			runnable = new ScrollingRunnable(direction);
			threadAlive = true;
			new Thread(runnable).start();
		}

	}

	/**
	 * Stop the scrolling which is currently active
	 */
	public void stopScrolling() {
		if (runnable != null)
			runnable.stop();
	}

	/**
	 * @return A boolean which says if a map is currently loaded
	 */
	public boolean isMapSet() {
		return (map != null);
	}

	/**
	 * Initialize the game with this map
	 * 
	 * @param m
	 *            Map which will be initialized
	 */
	public void setMap(Map m) {
		this.map = m;
	}

	/**
	 * 
	 * Scrolling thread
	 * 
	 * @author Feavy
	 *
	 */
	class ScrollingRunnable implements Runnable {

		int newXScale = 0;
		int newYScale = 0;

		boolean directionChanged = false;
		
		boolean end = false;

		private int direction;

		/**
		 * Start a new scrolling thread
		 * 
		 * @param direction
		 *            Scrolling direction
		 */
		public ScrollingRunnable(int direction) {
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

			this.direction = direction;

		}

		public void run() {

			Main.getPlayer().updateFacing();

			screen.cameraStartMovingEvent(cameraX, cameraY, direction);

			while (!end || (xScale % 64 != 0 || yScale % 64 != 0)) {

				if (changeXScale != 0 && changeYScale != 0) {
					if (end)
						break;
					updateScale();
				}

				if (map.getMovement(cameraX - changeXScale, cameraY + changeYScale) != 0 && xScale%64 == 0 && yScale%64 == 0) {
					checkEvent(cameraX - changeXScale, cameraY + changeYScale);
					break;
				}

				if (changeXScale > 0)
					xScale += GameScreen.FRAME_SKIP + 1;
				else if (changeXScale < 0)
					xScale -= GameScreen.FRAME_SKIP + 1;
				else if (changeYScale > 0)
					yScale += GameScreen.FRAME_SKIP + 1;
				else if (changeYScale < 0)
					yScale -= GameScreen.FRAME_SKIP + 1;

				screen.repaint();

				if (xScale % 64 == 0 && yScale % 64 == 0) {

					if (!Main.getPlayer().isFacingUpdated())
						Main.getPlayer().updateFacing();

					// map.loadEvents(cameraX, cameraY,
					// Main.getPlayer().getFacing(), instance);

					screen.cameraEndMovingEvent(cameraX, cameraY, direction);

					cameraX -= xScale / 64;
					cameraY += yScale / 64;
					xScale = 0;
					yScale = 0;

					checkEvent(cameraX, cameraY);

					if(end)
						break;
					
					if(directionChanged){
						updateScale();
						directionChanged = false;
					}
					
					screen.cameraStartMovingEvent(cameraX, cameraY, direction);
				}

				try {
					Thread.sleep(GameScreen.SCROLLING_SPEED * (GameScreen.FRAME_SKIP + 1));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			changeXScale = 0;
			changeYScale = 0;
			screen.repaint();

			threadAlive = false;
			
		}

		/**
		 * Check if a event is in a location. If there is a event, it will be
		 * executed.
		 * 
		 * @param x
		 *            Abscissa of the event to search for
		 * @param y
		 *            Ordinate of the event to search for
		 * @return true if there is a event
		 */
		boolean checkEvent(int x, int y) {

			if (map.isEvent(x, y)) {
				onTopEvent = (x == cameraX && y == cameraY);
				Event et = map.getEvent(x, y);
				if (et instanceof Warp) {
					screen.unLoadAllNPCs();
					Events.events.get(Events.WARP).callEvent((Warp) et);
				}
				return true;
			}
			return false;

		}

		/**
		 * Change the scrolling direction (if a new direction was set)
		 */
		void updateScale() {
			changeXScale = newXScale;
			changeYScale = newYScale;
			newXScale = 0;
			newYScale = 0;
		}

		/**
		 * Set a new scrolling direction (used in order to don't stop the
		 * current thread)
		 * 
		 * @param direction
		 *            New scrolling direction
		 */
		public void restart(int direction) {
			end = false;
			if (direction == Direction.RIGHT)
				newXScale = -1;
			else if (direction == Direction.LEFT)
				newXScale = 1;
			else if (direction == Direction.DOWN)
				newYScale = 1;
			else
				newYScale = -1;
			this.direction = direction;
			directionChanged = true;
		}

		/**
		 * Stop the current scrolling
		 */
		void stop() {
			end = true;
		}

	}

	public int getXScale() {
		return xScale;
	}

	public int getYScale() {
		return yScale;
	}

}
