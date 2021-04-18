package fr.feavy.java.data;


public class KeyCode {
	
	/**
	 * @param keyCode key id
	 * @return true is the code is an array
	 */
	public static boolean isArrow(int keyCode){
		return (keyCode-37 >= 0 && keyCode-37 <= 3);
	}
	
}
