package fr.feavy.java.map.events;

public class Warp extends Event{

	private int mapID, x, y;
	
	public Warp(int mapID, int x, int y){
		this.mapID = mapID;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return Map id in which the warp transport
	 */
	public int getMapID(){
		return mapID;
	}
	
	/**
	 * @return Abscissa where the warp transport
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * @return Ordinate where the warp transport
	 */
	public int getY(){
		return y;
	}
	
}
