package fr.feavy.java.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.feavy.java.map.events.Event;
import fr.feavy.java.map.events.Warp;

public class Map {

	private int width;
	private int height;
	private String name;
	private int id;

	private int borderTileID;

	private int[] borderMapsID;

	private int[][][] tiles;
	private java.util.Map<Point, List<Event>> events = new HashMap<Point, List<Event>>();
	
	public Map(int id, String name, int width, int height, int[] borderMapsID, int borderTileID, int[][][] tiles, HashMap<Point, Warp> warps) {

		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.borderTileID = borderTileID;
		this.borderMapsID = borderMapsID;
		this.tiles = tiles;

		List<Event> e;
		
		for(Point p : warps.keySet()){
			e = new ArrayList<Event>();
			e.add(warps.get(p));
			events.put(p, e);
		}
		
	}

	public String getName(){ return name; }
	
	public int getTileID(int x, int y) { return tiles[x][y][0]; }
	
	public int getMovementID(int x, int y) { if(x >= 0 && y >= 0 && x < width && y < height) return tiles[x][y][1]; else return 0;}
	
	public int getWidth(){ return width; }
	public int getHeight(){ return height; }

	public int getBorderTileID() { return borderTileID; }

	public boolean isEvent(Point location) { return events.containsKey(location); }

	public List<Event> getEvents(Point location) { return events.get(location); }
	
	public Warp getWarp(Point location){
		for(Event e : events.get(location)){
			if(e instanceof Warp)return (Warp)e;
		}
		return null;
	}

	public boolean isMap(int direction) { return borderMapsID[direction] != -1; }

	public int getBorderMapID(int direction) {return borderMapsID[direction]; }
	
}
