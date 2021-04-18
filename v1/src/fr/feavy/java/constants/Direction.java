package fr.feavy.java.constants;

public class Direction {

	public static final int LEFT = 0;
	public static final int UP = 1;
	public static final int RIGHT = 2;
	public static final int DOWN = 3;
	
	public static int getOppositeDirection(int direction){
		return (direction >= 2) ? direction-2 : direction+2;
	}
	
	public static boolean isVertical(int direction){
		return direction%2 != 0;
	}
	
	public static int getXDestination(int direction){
		return (direction == LEFT) ? -1 : (direction == RIGHT) ? 1 : 0;
	}
	
	public static int getYDestination(int direction){
		return (direction == UP) ? -1 : (direction == DOWN) ? 1 : 0;
	}
	
}