package fr.feavy.java.data;

public enum TallGrassTiles {

	TALL_GRASS_1(39), TALL_GRASS_2(77), TALL_GRASS_3(608);
	
	private int tileID;
	
	TallGrassTiles(int tileID){
		this.tileID = tileID;
	}
	
	public int id(){
		return tileID;
	}
	
}
