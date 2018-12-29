package fr.feavy.java.entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;

import fr.feavy.java.Main;
import fr.feavy.java.constants.Direction;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.events.KeyPressedEvent;
import fr.feavy.java.systemEvent.events.KeyReleasedEvent;
import fr.feavy.java.systemEvent.listeners.EventListener;

public class Player extends Entity implements EventListener{

	private int pendingDirection = -1;
	private boolean pendingNewMovement = false;
	
	public Player() throws IOException {
		super(new Point(0,0), "player", Direction.DOWN, false, 0, true);
		Main.addEventListener(this);
		pendingDirection = -1;
		pendingNewMovement = false;
	}

	@Override
	public void onEvent(Event e){
		if(e.getID().equals("keyPressedEvent")){
			KeyPressedEvent ke = (KeyPressedEvent)e;
			int direction = ke.getKeyCode()-37;
			if(!isMovingAnimationActive()){
				if(isMovingAnimationTerminated()){
					super.setFacingDirection(direction);
					super.startMovingAnimation();
				}else{
					pendingDirection = direction;
					pendingNewMovement = true;
				}
			}else{
				pendingDirection = direction;
			}
		}else if(e.getID().equals("keyReleasedEvent")){
			KeyReleasedEvent ke = (KeyReleasedEvent)e;
			int direction = ke.getKeyCode()-37;
			if(super.facingDirection == direction || super.facingDirection == pendingDirection)
				super.stopMovingAnimation();
		}else if(e.getID().equals("playerAnimationFinishedEvent")){
			if(pendingNewMovement){
				super.startMovingAnimation();
				pendingNewMovement = false;
			}
		}else if(e.getID().equals("cameraScrollingChangedEvent")){
			if(pendingDirection >= 0){
				super.setFacingDirection(pendingDirection);
				pendingDirection = -1;
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(!sprites.isMirror())
			g.drawImage(sprites.getCurrentSprite(), 4*65, 4*60, 64, 64, null);
		else
			g.drawImage(sprites.getCurrentSprite(), 4*63, 4*60, 64, 64, null);
	}

}
