package fr.feavy.java.rendering;

import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import fr.feavy.java.data.Direction;
import fr.feavy.java.eventListener.CameraEvent;
import fr.feavy.java.events.Event;
import fr.feavy.java.events.entity.NPC;
import fr.feavy.java.map.Coordonate;
import fr.feavy.java.map.Map;

public class EntityRendering implements Rendering, CameraEvent {

	private GameScreen screen;
	private MapRendering mapRendering;
	private Vector<NPC> activeNPCs = new Vector<NPC>();
	
	public EntityRendering(GameScreen gameScreen, MapRendering mapRendering) {
		screen = gameScreen;
		this.mapRendering = mapRendering;
	}

	@Override
	public void render(Graphics g) {
		drawNPCs(g, mapRendering.getCameraX(), mapRendering.getCameraY(), mapRendering.getXScale(), mapRendering.getYScale());
	}

	public int getActiveNPCAmount(){
		return activeNPCs.size();
	}
	
	@Override
	public void cameraStartMovingEvent(int cameraX, int cameraY, int direction) {
		Map map = screen.getCurrentMap();

		int[] square = getSquare(cameraX, cameraY, direction);
		
		loadNPCs(map.getNPCs(square[0], square[1], square[2], square[3]), screen);
	}

	@Override
	public void cameraEndMovingEvent(int cameraX, int cameraY, int direction) {
	}

	@Override
	public void cameraReachBorderEvent(Map sideMap) {
		unLoadAllNPCs();
	}

	/**
	 * Draw the loaded NPC on screen
	 * 
	 * @param g
	 *            Instance of game screen's graphics
	 * @param cameraX
	 *            Abscissa of the camera
	 * @param cameraY
	 *            Ordinate of the camera
	 * @param xScale
	 *            Camera horizontal shift
	 * @param yScale
	 *            Camera vertical shift
	 */
	public void drawNPCs(Graphics g, int cameraX, int cameraY, int xScale, int yScale) {
		for (NPC npc : activeNPCs)
			npc.draw(g, cameraX, cameraY, xScale, yScale);
	}

	/**
	 * Load specified events (NPC)
	 * 
	 * @param list
	 *            A list containing the events
	 * @param display
	 *            Instance of game screen
	 */
	private void loadNPCs(List<NPC> list, GameScreen display) {
		if (list.size() == 0)
			return;
		for (NPC e : list) {
			if (!activeNPCs.contains(e)) {
				activeNPCs.add(e);
				e.initialize(display);
			}
		}
	}

	/**
	 * Unload specified events (NPC)
	 * 
	 * @param list
	 *            A list containing the events
	 */
	private void unloadNPCs(List<NPC> list) {
		if (list.size() == 0)
			return;
		for (NPC e : list) {
			activeNPCs.remove(e);
			if(e.isLoaded())
				e.unLoad();
		}
	}

	/**
	 * Unload all loaded events (NPC)
	 */
	public void unLoadAllNPCs() {
		unloadNPCs((List<NPC>) activeNPCs.clone());
	}

	private int[] getSquare(int x, int y, int direction) {

		int startX = 0, startY = 0, endX = 0, endY = 0;

		switch (direction) {
		case Direction.UP:
			startX = x - 5;
			startY = y - 5;
			endX = startX + 12;
			endY = startY;
			break;
		case Direction.DOWN:
			startX = x - 5;
			startY = y + 5;
			endX = startX + 12;
			endY = startY;
			break;
		case Direction.LEFT:
			startX = x - 5;
			startY = y - 5;
			endX = startX;
			endY = startY + 11;
			break;
		case Direction.RIGHT:
			startX = x + 6;
			startY = y - 5;
			endX = startX;
			endY = startY + 11;
			break;
		}

		return new int[]{startX, startY, endX, endY};
		
	}

}
