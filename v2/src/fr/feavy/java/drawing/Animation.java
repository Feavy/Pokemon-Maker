package fr.feavy.java.drawing;

import fr.feavy.java.rendering.GameScreen;

/**
 * Animations superclass
 * @author Feavy
 *
 */
public class Animation{

	private GameScreen display;
	
	public Animation(GameScreen display){
		this.display = display;
	}

	public GameScreen getDisplay(){
		return display;
	}
	
	/**
	 * Start the animation
	 */
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
}
