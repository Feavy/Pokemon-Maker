package fr.feavy.java.rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import fr.feavy.java.Main;
import fr.feavy.java.debug.FrameCalculator;
import fr.feavy.java.drawing.Animation;
import fr.feavy.java.eventListener.CameraEvent;
import fr.feavy.java.events.Event;
import fr.feavy.java.events.Warp;
import fr.feavy.java.map.Map;

public class GameScreen extends JPanel implements CameraEvent{

	public static int SCROLLING_SPEED = 5;
	public static final int FRAME_SKIP = 0;
	
	private boolean animationRunning = false;

	private FrameCalculator fc;
	
	private MapRendering mapRendering;
	private EntityRendering entityRendering;
	private GUIRendering guiRendering;

	public GameScreen(Map m, int x, int y) {
		
		mapRendering = new MapRendering(this, m, x, y);
		entityRendering = new EntityRendering(this, mapRendering);
		guiRendering = new GUIRendering();
		
		fc = new FrameCalculator();
		/* TODO Start FPS mesurer */
		// new Thread(fc).start();
		setCamera(x, y);
	}

	public Map getCurrentMap() {
		return mapRendering.getCurrentMap();
	}

	/**
	 * Function used to teleport the camera to a location of a specified map
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
		mapRendering.warpCameraTo(m, x, y);

	}

	/**
	 * Set the camera to a new position
	 * 
	 * @param x
	 *            Abscissa of the location
	 * @param y
	 *            Ordinate of the location
	 */
	private void setCamera(int x, int y) {
		mapRendering.setCamera(x, y);
		repaint();
	}

	/**
	 * @return abscissa of the camera's location
	 */
	public int getCameraX() {
		return mapRendering.getCameraX();
	}

	/**
	 * @return ordinate of the camera's location
	 */
	public int getCameraY() {
		return mapRendering.getCameraY();
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
		repaint();
	}

	/**
	 * Stop a started animation and unfreeze the game
	 */
	public void stopAnimation() {
		animationRunning = false;
		repaint();
	}

	/**
	 * Refresh the screen Doesn't refresh if a animation is running
	 */
	@Override
	public void repaint() {
		if (animationRunning)
			return;
		super.repaint();
	}

	/**
	 * GAME RENDER
	 */
	@Override
	public void paintComponent(Graphics g) {

		/**
		 *
		 * --= GAME RENDER =--
		 *
		 **/

		super.paintComponents(g);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		
		/**
		 * 
		 * --= MAP =--
		 * 
		 **/
		
		if(isMapSet()){
			mapRendering.render(g);
			if(entityRendering.getActiveNPCAmount() > 0)
				entityRendering.render(g);
		}

		/**
		 *
		 * --= DEBUG =--
		 *
		 **/

		fc.addFrame();

		g.drawString(mapRendering.getCameraX() + " ; " + mapRendering.getCameraY(), 1, 15);

	}

	/**
	 * Start/change map scrolling
	 * 
	 * @param direction
	 *            New scrolling direction
	 */
	public void setScrollingDirection(int direction) {
		mapRendering.setScrollingDirection(direction);
	}

	/**
	 * Stop the scrolling which is currently active
	 */
	public void stopScrolling() {
		mapRendering.stopScrolling();
	}

	/**
	 * @return A boolean which says if a map is currently loaded
	 */
	public boolean isMapSet() {
		return mapRendering.isMapSet();
	}

	/**
	 * Initialize the game with this map
	 * 
	 * @param m
	 *            Map which will be initialized
	 */
	public void setMap(Map m) {
		mapRendering.setMap(m);
	}

	@Override
	public void cameraStartMovingEvent(int cameraX, int cameraY, int direction) {
		entityRendering.cameraStartMovingEvent(cameraX, cameraY, direction);
	}

	@Override
	public void cameraEndMovingEvent(int cameraX, int cameraY, int direction) {
		entityRendering.cameraEndMovingEvent(cameraX, cameraY, direction);		
	}

	@Override
	public void cameraReachBorderEvent(Map sideMap) {
		entityRendering.cameraReachBorderEvent(sideMap);	
	}

	public void unLoadAllNPCs() {
		entityRendering.unLoadAllNPCs();
		
	}

}
