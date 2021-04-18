package fr.feavy.java.map;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import fr.feavy.java.Main;
import fr.feavy.java.data.Direction;
import fr.feavy.java.events.RunnableEvent;
import fr.feavy.java.events.Event;
import fr.feavy.java.events.Warp;
import fr.feavy.java.events.entity.NPC;
import fr.feavy.java.rendering.GameScreen;

public class Map {

	private int width;
	private int height;
	private String name;
	private int id;

	private int borderTileID;

	private int[] sideMaps;

	private int[][] tilesID;
	private int[][] movementsID;

	private HashMap<Coordonate, List<Event>> events = new HashMap<Coordonate, List<Event>>();

	/**
	 * Instantiate a map
	 * @param id Map's id
	 * @param name Map's name
	 * @param width Map's width
	 * @param height Map's height
	 * @param topMapID Id of the map which is connected at top (-1 if no connection)
	 * @param rightMapID Id of the map which is connected at right (-1 if no connection)
	 * @param bottomMapID Id of the map which is connected at bottom (-1 if no connection)
	 * @param leftMapID Id of the map which is connected at left (-1 if no connection)
	 * @param tiles array containing all tiles' id
	 * @param movements array containing all movements' id
	 * @param borderTileID Tile's id which the player will see when reaching the borders
	 */
	public Map(int id, String name, int width, int height, int topMapID, int rightMapID, int bottomMapID, int leftMapID, int[][] tiles, int[][] movements,
			int borderTileID) {

		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.borderTileID = borderTileID;

		this.sideMaps = new int[] { leftMapID, topMapID, rightMapID, bottomMapID };

		this.tilesID = tiles;
		this.movementsID = movements;

	}

	/**
	 * Add a NPC to the map
	 * @param x Default Abscissa of the NPC
	 * @param y Default Ordinate of the NPC
	 * @param npc The NPC itself
	 */
	public void addNPC(int x, int y, NPC npc) {
		List<Event> l = events.get(new Coordonate(x, y));
		if (l != null)
			l.add(npc);
		else {
			l = new ArrayList<Event>();
			l.add(npc);
			events.put(new Coordonate(x, y), l);
		}
	}

	/**
	 * Remove a NPC of the map
	 * @param x Default Abscissa of the NPC
	 * @param y Default Ordinate of the NPC
	 * @param npc The NPC itself
	 */
	public void removeNPC(int x, int y, NPC npc) {
		List<Event> l = events.get(new Coordonate(x, y));
		if (l != null) {
			l.remove(npc);
			if (l.size() == 0)
				events.replace(new Coordonate(x, y), null);
		}
	}
	
	/**
	 * Add a warp to the map
	 * @param x Default Abscissa of the warp
	 * @param y Default Ordinate of the warp
	 * @param npc The warp itself
	 */
	public void addWarp(int x, int y, Warp w) {
		List<Event> l = events.get(new Coordonate(x, y));
		if (l != null) {
			l.add(w);
			events.replace(new Coordonate(x, y), l);
		} else {
			l = new ArrayList<Event>();
			l.add(w);
			events.put(new Coordonate(x, y), l);
		}
	}

	/**
	 * 
	 * @param x Abscissa to search for
	 * @param y Ordinate to search for
	 * @return true if there is a event, false if not
	 */
	public boolean isEvent(int x, int y) {
		return (events.get(new Coordonate(x, y)) != null);
	}

	/**
	 * 
	 * @param x Abscissa of the event
	 * @param y Ordinate of the event
	 * @return The event itself
	 */
	public Event getEvent(int x, int y) {
		return events.get(new Coordonate(x, y)).get(0);
	}

	/**
	 * @return Border tile's id
	 */
	public int getBorderTileID() {
		return borderTileID;
	}

	/**
	 * @param side Direction where search for
	 * @return true if there is a map, false if not
	 */
	public boolean isMapSet(int side) {
		return sideMaps[side] != -1;
	}

	/**
	 * 
	 * @param side Direction where the map is in relation to this map
	 * @return The map in question
	 */
	public Map getSideMap(int side) {
		int mapID = sideMaps[side];
		if (mapID != -1)
			try {
				return Main.getProject().getMap(mapID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * Get tile's id located to a specified location
	 * @param x Abscissa of the tile
	 * @param y Ordinate of the tile
	 * @return Tile's id
	 */
	public int getTileID(int x, int y) {
		if (x < 0 || x >= movementsID.length || y < 0 || y >= movementsID[0].length)
			return borderTileID;
		return tilesID[x][y];
	}

	public boolean isNPC(int x, int y){
		List<Event> evts = events.get(new Coordonate(x, y));
		if(evts == null)
			return false;
		for(Event e : evts){
			if(e instanceof NPC)
				return true;
		}
		return false;
	}
	
	/**
	 * Get movement's id located to a specified location
	 * 0: default | 1: block | 2: surf
	 * @param x Abscissa of the tile
	 * @param y Ordinate of the tile
	 * @return Movent's id
	 */
	public int getMovement(int x, int y) {
		if (x < 0 || x >= movementsID.length || y < 0 || y >= movementsID[0].length)
			return 0;
		
		return (isNPC(x, y)) ? 1 : movementsID[x][y];
	}
	
	/**
	 * Get all tiles' id in a specified square
	 * @param startwidth Abscissa of a corner
	 * @param startheight Ordinate of a corner
	 * @param lengthwidth Square width
	 * @param lengthheight Square height
	 * @return
	 */
	public int[][] getTiles(int startwidth, int startheight, int lengthwidth, int lengthheight) {

		int[][] response = new int[lengthwidth][lengthheight];

		for (int i = 0; i < lengthwidth; i++)
			for (int j = 0; j < lengthheight; j++) {
				if (startwidth + i < width && startheight + j < height && startwidth + i >= 0 && startheight + j >= 0) {
					response[i][j] = tilesID[startwidth + i][startheight + j];
					continue;
				} else
					response[i][j] = -1;
			}
		return response;

	}
	
	/**
	 * Load all events in a certain range (square)
	 * @param startX Square top-left corner abscissa
	 * @param startY Square top-left corner ordinate
	 * @param endX Square bottom-right corner abscissa 
	 * @param endY Square bottom-right corner ordinate
	 * @param display Instance of game screen
	 */
	public List<NPC> getNPCs(int startX, int startY, int endX, int endY) {

		List<NPC> NPCs = new ArrayList<>();
		List<Event> evts = new ArrayList<>();
		
		for(int i = startX; i <= endX; i++)
			for(int j = startY; j <= endY; j++){
				evts = events.get(new Coordonate(i, j));
				if(evts != null)
					for(Event et : evts)
						if(et instanceof NPC)
							NPCs.add((NPC)et);
			}
		return NPCs;
		
	}
	
	public int[][] getMovements() {
		return movementsID;
	}

	public int getID() {
		return id;
	}

	public int[] getSideMapsIDs() {
		return sideMaps;
	}

	public String getName() {
		return name;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public void setTopMapID(int id) {
		sideMaps[Direction.UP] = id;
	}

	public void setRightMapID(int id) {
		sideMaps[Direction.RIGHT] = id;
	}

	public void setLeftMapID(int id) {
		sideMaps[Direction.LEFT] = id;
	}

	public void setBottomMapID(int id) {
		sideMaps[Direction.DOWN] = id;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setName(String name) {
		this.name = name;
	}

}
