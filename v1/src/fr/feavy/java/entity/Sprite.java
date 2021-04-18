package fr.feavy.java.entity;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import fr.feavy.java.constants.Direction;

public class Sprite {

	private BufferedImage spriteImages;
	private boolean isMirror;
	private Image currentSpriteImage;
	
	private final int WIDTH = 16;
	
	private int lookingDirection = 0;
	
	private Point currentSpritePosition;
	
	public Sprite(BufferedImage spriteImages){
		this.spriteImages = spriteImages;
		this.isMirror = false;
		currentSpritePosition = new Point(0,0);
	}
	
	public void setSpriteLookingDirection(int direction){
		this.lookingDirection = direction;
		this.isMirror = false;
		switch (direction) {
		case Direction.UP:
			currentSpritePosition.setLocation(0, 2*WIDTH);
			break;
		case Direction.DOWN:
			currentSpritePosition.setLocation(0, WIDTH);
			break;
		case Direction.RIGHT:
			currentSpritePosition.setLocation(0, 0);
			isMirror = true;
			break;
		case Direction.LEFT:
			currentSpritePosition.setLocation(0, 0);
			break;
		}
		updateImage();
	}
	
	private void updateImage(){
		
		BufferedImage i = spriteImages.getSubimage(currentSpritePosition.x, currentSpritePosition.y, WIDTH, WIDTH);
		
		if(isMirror){
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-i.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			currentSpriteImage = op.filter(i, null);
		}else{
			currentSpriteImage = i;
		}
	}
	
	public boolean isDefaultSprite(){
		return currentSpritePosition.x == 0;
	}
	
	public void nextSprite(){
		if(!Direction.isVertical(lookingDirection))
			currentSpritePosition.x = (currentSpritePosition.x == 0) ? 16 : 0;
		else{
			currentSpritePosition.x = (currentSpritePosition.x == 0) ? 16 : 0;
			if(currentSpritePosition.x == 16)
				isMirror = !isMirror;
		}
		updateImage();
	}
	
	public Image getCurrentSprite(){
		return currentSpriteImage;
	}
	
	public boolean isMirror(){ return isMirror; }
	
}
