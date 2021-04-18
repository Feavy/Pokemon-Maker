package fr.feavy.java.drawing;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Tile {

	private boolean isAnimated = false;
	private BufferedImage image;
	private Image scalledImage;
	
	private boolean animationLoop = false;
	
	public Tile(BufferedImage image, boolean isAnimated){
		this.image = image;
		this.isAnimated = isAnimated;
		if(isAnimated)
			animationLoop = image.getWidth() % 16 != 0;
	}
	
	/**
	 * 
	 * Set new image of image's animation
	 * @param step number of the image
	 */
	public void setStep(int step){
		scalledImage = image.getSubimage(16*step, 0, 16, 16);
	}
	
	/**
	 * Get a 16*16 image of the tile, if animated
	 * the returned image correspond to the step set
	 * @return Image which has to be shown
	 */
	public Image getImage(){
		return (isAnimated)?scalledImage:image;
	}
	
	/**
	 * @return true if the animation has to go forward and backword or false if not
	 */
	public boolean isAnimationLoop(){
		return animationLoop;
	}
	
	/**
	 * @return true if the tile is animated, false if not.
	 */
	public boolean isAnimated(){
		return isAnimated;
	}
	
}
